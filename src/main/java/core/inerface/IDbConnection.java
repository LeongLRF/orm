package core.inerface;

import annotation.Id;
import fj.P3;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Leong
 */
public interface IDbConnection {

    /**
     * 数据库连接
     * @return Connection
     */
    Connection getConnection();

    /**
     * 连接池
     * @return DataSource
     */
    DataSource getDataSource();

    /**
     * 启动查询
     * @param cls 查询对象
     * @param <T> 泛型
     * @return SelectQuery
     */
    <T> ISelectQuery<T> form(Class<T> cls);

    <T> List<T> sqlQuery(Class<T> cls, String sql, Object... values);

    <T> T getById(Class<T> cls, Serializable id);

    <T> List<T> getByIds(Class<T> cls, List<Object> ids);

    <T> List<T> gen_execute(P3<Class<T>,String,List<Object>> p3);

    /**
     * 插入单条数据
     * @param entity 插入对象
     * @param <T> 泛型
     * @return 是否成功
     */
    <T> int insert(T entity);

    /**
     * 批量插入
     * @param entities 插入对象
     * @param <T> 泛型
     * @return 是否成功
     */
    <T> int insert(List<T> entities);

    /**
     * 开启事务
     * @param f 事务内容
     * @throws SQLException SQL错误
     */
    void openTransaction(Consumer<Connection> f) throws SQLException;


}
