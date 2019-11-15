package core;

import com.mysql.cj.protocol.Resultset;
import core.TableInfo;
import core.inerface.IDbConnection;
import core.inerface.ISelectQuery;
import core.inerface.IStatement;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.EntityUtil;
import util.StringPool;

import javax.xml.transform.Result;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
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


    public SelectQuery(IDbConnection connection, Class<T> cls) {
        this.cls = cls;
        this.connection = connection;
        this.tableInfo = EntityUtil.getTableInfo(cls);
        refreshSql();
    }

    public void refreshSql(){
        this.sql = "SELECT " + getSelects() + " FROM " + tableInfo.getTableName();
    }

    @Override
    public ISelectQuery<T> where(String column, Object value) {
        return null;
    }

    @Override
    public ISelectQuery<T> in(String column, List<? extends Serializable> ids) {
        return null;
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
        long start = System.currentTimeMillis();
        makeSql(wheres);
        List<T> rs = connection.execute(this);
        long end = System.currentTimeMillis();
        logger.info("Cost : " + (end - start) + "ms");
        return rs;
    }

    @Override
    public T one() {
        return null;
    }

    public void makeSql(List<IStatement> statements) {
        refreshSql();
        if (!statements.isEmpty()) {
            sql = sql + " WHERE " + wheres.stream().map(IStatement::getSql).collect(Collectors.joining(" AND "));
        }
        if (!statements.isEmpty()){
            params.addAll(statements.stream().flatMap(it -> it.getParams().stream()).collect(Collectors.toList()));
        }
    }
}
