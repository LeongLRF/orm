package core.inerface;

import com.sun.istack.internal.Nullable;
import core.TableInfo;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;

/**
 * @author Leong
 */
public interface ISelectQuery<T> {

    /**
     * 数据库连接
     */
    IDbConnection getConnection();

    /**
     * 表信息
     */
    TableInfo getTableInfo();

    /**
     * 查询参数
     */
    List<Object> getParams();

    /**
     * 查询sql
     */
    String getSql();

    /**
     * 查询类
     */
    Class<T> getCls();

    ISelectQuery<T> where(String column, Object value);

    ISelectQuery<T> in(String column, List<? extends Serializable> ids);

    ISelectQuery<T> whereEq(String column, Object value);

    ISelectQuery<T> inSql(String column,String sql,@Nullable Object ...values);

    List<T> toList();

    T one();
}
