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
        this.sql = "SELECT " + getSelects() + " FROM " + tableInfo.getTableName() + " WHERE 1=1";
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
        if (!wheres.isEmpty()) {
            sql = sql + " And " + wheres.stream().map(IStatement::getSql).collect(Collectors.joining(" AND "));
        }
        ResultSet rs = connection.execute(this);
        long end = System.currentTimeMillis();
        logger.info("Cost : "+(end - start) + "ms");
        return null;
    }

    @Override
    public T one() {
        return null;
    }

    public String makeSql(List<IStatement> statements) {
        return this.sql;
    }
}
