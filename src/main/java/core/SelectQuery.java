package core;

import com.mysql.cj.util.StringUtils;
import core.inerface.IDbConnection;
import core.inerface.ISelectQuery;
import core.inerface.IStatement;
import fj.P2;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.DbLogger;
import util.EntityUtil;
import util.StringPool;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Leong
 * 用于sql拼接
 */
@Data
public class SelectQuery<T> implements ISelectQuery<T> {

    private final Logger logger = LoggerFactory.getLogger(SelectQuery.class);

    private IDbConnection connection;
    private String sql;
    private Class<T> cls;
    private TableInfo tableInfo;
    private List<Object> params = new ArrayList<>();
    private List<IStatement> wheres = new ArrayList<>();
    private String selects = "*";
    private String orderBy = "";


    public SelectQuery(IDbConnection connection, Class<T> cls) {
        this.cls = cls;
        this.connection = connection;
        this.tableInfo = EntityUtil.getTableInfo(cls);
        refreshSql();
    }

    private void refreshSql(){
        this.sql = "SELECT " + getSelects() + " FROM " + tableInfo.getTableName();
    }

    @Override
    public ISelectQuery<T> where(String column, Object value) {
        return null;
    }

    @Override
    public ISelectQuery<T> in(String column, List<Object> values) {
        if (!values.isEmpty()){
            Statement statement = new Statement();
            statement.sql = column + " in " + DbConnection.createParameterPlaceHolder(values.size());
            statement.params.addAll(values);
            this.wheres.add(statement);
        }
        return this;
    }

    @Override
    public ISelectQuery<T> whereEq(String column, Object value) {
        Statement statement = new Statement();
        statement.sql = column + StringPool.SPACE + StringPool.EQUALS + StringPool.SPACE + StringPool.QUESTION_MARK;
        statement.params.add(value);
        wheres.add(statement);
        return this;
    }

    public ISelectQuery<T> whereEq(boolean condition, String column, Object value) {
        if (condition) {
            return whereEq(column, value);
        } else {
            return this;
        }
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
    public List<T> toList() {
        return connection.genExecute(DbConnection.makeSql(this));
    }

    @Override
    public T one() {
        List<T> list = toList();
        if (list.isEmpty()){
            return null;
        }
        return list.get(0);
    }

    @Override
    public ISelectQuery<T> orderBy(String orderBy) {
        if (!StringUtils.isNullOrEmpty(orderBy)){
            this.orderBy = " order by " + orderBy;
        }
        return this;
    }

    @Override
    public void makeSql(List<IStatement> statements) {
        refreshSql();
        if (!statements.isEmpty()) {
            sql = sql + " WHERE " + wheres.stream().map(IStatement::getSql).collect(Collectors.joining(" AND ")) + orderBy;
        }
        if (!statements.isEmpty()){
            params.addAll(statements.stream().flatMap(it -> it.getParams().stream()).collect(Collectors.toList()));
        }
    }

    @Override
    public int update(Consumer<T> updates) {
        T entity = DbConnection.createEntity(cls);
        updates.accept(entity);
        P2<String,List<Object>> p2 = Statement.getSets(entity);
        String where = wheres.stream().map(IStatement::getSql).collect(Collectors.joining(" AND ")) + orderBy;
        params.addAll(p2._2());
        params.addAll(wheres.stream().map(IStatement::getParams).collect(Collectors.toList()));
        try {
            return Statement.createUpdateStatement(tableInfo.getTableName(),p2._1(),params,where).createPreparedStatement(connection.getConnection())
                    .executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
