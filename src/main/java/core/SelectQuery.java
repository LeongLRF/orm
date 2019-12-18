package core;

import com.mysql.cj.util.StringUtils;
import core.inerface.*;
import core.support.Page;
import fj.P;
import fj.P2;
import fj.P3;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.EntityUtil;
import util.StringPool;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Leong
 * 用于sql拼接
 */
@Data
public class SelectQuery<T> implements ISelectQuery<T> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private IDbConnection connection;
    private String sql;
    private Class<T> cls;
    private TableInfo tableInfo;
    private List<Object> params = new ArrayList<>();
    private List<IStatement> wheres = new ArrayList<>();
    private String selects = "*";
    private String orderBy = "";
    private String limit = "";


    SelectQuery(IDbConnection connection, Class<T> cls) {
        this.cls = cls;
        this.connection = connection;
        this.tableInfo = EntityUtil.getTableInfo(cls);
        refreshSql();
    }

    @Override
    public ILambdaQuery<T> lambdaQuery() {
        return new LambdaQuery<>(cls, connection, tableInfo);
    }

    private void refreshSql() {
        this.sql = "SELECT " + getSelects() + " FROM " + tableInfo.getTableName();
    }

    @Override
    public ISelectQuery<T> where(String sql, Object... value) {
        IStatement statement = new Statement();
        statement.setSql(sql);
        statement.getParams().addAll(Arrays.asList(value));
        this.wheres.add(statement);
        return this;
    }

    @Override
    public ISelectQuery<T> in(String column, Collection<?> values) {
        if (!values.isEmpty()) {
            IStatement statement = new Statement();
            statement.setSql(column + " in " + DbConnection.createParameterPlaceHolder(values.size()));
            statement.getParams().addAll(values);
            this.wheres.add(statement);
        }
        return this;
    }

    @Override
    public ISelectQuery<T> whereEq(String column, Object value) {
        IStatement statement = new Statement();
        statement.setSql(column + StringPool.SPACE + StringPool.EQUALS + StringPool.SPACE + StringPool.QUESTION_MARK);
        statement.getParams().add(value);
        wheres.add(statement);
        return this;
    }

    @Override
    public ISelectQuery<T> inSql(String column, String sql, Object... values) {
        return null;
    }

    @Override
    public ISelectQuery<T> select(String column) {
        this.selects = column;
        return this;
    }

    @Override
    public ISelectQuery<T> between(String column, Object value, Object value2) {
        IStatement statement = new Statement();
        statement.setSql(column + " BETWEEN ? AND ?");
        statement.getParams().add(value);
        statement.getParams().add(value2);
        wheres.add(statement);
        return this;
    }

    @Override
    public List<T> toList() {
        return connection.genExecute(this.makeSql());
    }


    P3<Class<?>, String, List<Object>> makeSql() {
        this.makeSql(this.getWheres());
        String sql = this.getSql();
        List<Object> params = this.getParams();
        if (!selects.equals("*")) {
            return P.p(Object.class, sql, params);
        }
        return P.p(cls, sql, params);
    }


    @Override
    public T one() {
        List<T> list = toList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public ISelectQuery<T> orderBy(String orderBy) {
        if (!StringUtils.isNullOrEmpty(orderBy)) {
            this.orderBy = " order by " + orderBy;
        }
        return this;
    }

    @Override
    public void makeSql(List<IStatement> statements) {
        refreshSql();
        if (!statements.isEmpty()) {
            sql = sql + " WHERE " + wheres.stream().map(IStatement::getSql).collect(Collectors.joining(" AND "));
        }
        if (!statements.isEmpty()) {
            params.addAll(statements.stream().flatMap(it -> it.getParams().stream()).collect(Collectors.toList()));
        }
        sql = sql + orderBy + limit;
    }

    @Override
    public int update(Consumer<T> updates) {
        long start = System.currentTimeMillis();
        T entity = DbConnection.createEntity(cls);
        updates.accept(entity);
        P2<String, List<Object>> p2 = Statement.getSets(entity);
        String where = wheres.stream().map(IStatement::getSql).collect(Collectors.joining(" AND "));
        params.addAll(p2._2());
        params.addAll(wheres.stream().flatMap(it -> it.getParams().stream()).collect(Collectors.toList()));
        try {
            return Statement.createUpdateStatement(tableInfo.getTableName(), p2._1(), params, where)
                    .createPreparedStatement(connection.getConnection(), null)
                    .executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            logger.info("Cost : " + (System.currentTimeMillis() - start) + "ms");
        }
        return 0;
    }

    @Override
    public ISelectQuery<T> genLike(String column, Object value, int position) {
        IStatement statement = new Statement();
        String like = null;
        switch (position) {
            case 0:
                like = "'%" + value + "%'";
                break;
            case 1:
                like = "'%" + value + "'";
                break;
            case 2:
                like = "'" + value + "%'";
                break;
            default:
        }
        statement.setSql(column + " like " + like);
        wheres.add(statement);
        return this;
    }

    @Override
    public ISelectQuery<T> apply(IFilter<T> filter) {
        return filter.apply(this);
    }

    @Override
    public ISelectQuery<T> limit(int form, int to) {
        this.limit = " limit " + form + " , " + to;
        return this;
    }

    @Override
    public Page<T> page(int page, int pageSize) {
        long total = toList().size();
        List<T> data = limit(page * pageSize, pageSize).toList();
        return new Page<>(total, page, pageSize, data);
    }

    @Override
    public Object avg(String column) {
        selects = " AVG(" + column + ") ";
        P3<Class<?>, String, List<Object>> p3 = makeSql();
        System.out.println(p3._2());
        return  connection.normalQuery(p3._2(), p3._3().toArray());
    }

    @Override
    public Object max(String column) {
        selects = " MAX(" + column + ") ";
        P3<Class<?>, String, List<Object>> p3 = makeSql();
        return connection.normalQuery(p3._2(), p3._3().toArray());
    }

    @Override
    public Object min(String column) {
        selects = " MIN(" + column + ") ";
        P3<Class<?>, String, List<Object>> p3 = makeSql();
        System.out.println(p3._2());
        return connection.normalQuery(p3._2(), p3._3().toArray());
    }

}
