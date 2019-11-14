package core;

import core.TableInfo;
import core.inerface.IDbConnection;
import core.inerface.ISelectQuery;
import core.inerface.IStatement;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.EntityUtil;

import java.io.Serializable;
import java.util.List;
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
    private TableInfo<T> tableInfo;
    private Object[] params;
    private List<IStatement> wheres;
    private String selects = "*";


    public SelectQuery (IDbConnection connection,Class<T> cls){
        this.cls = cls;
        this.connection = connection;
        this.tableInfo = EntityUtil.getTableInfo(cls);
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
        return null;
    }

    @Override
    public ISelectQuery<T> inSql(String column, String sql, Object... values) {
        return null;
    }

    @Override
    public List<T> toList() {
        return null;
    }

    @Override
    public T one() {
        return null;
    }

    public String makeSql(List<IStatement> statements){
        return this.sql;
    }
}
