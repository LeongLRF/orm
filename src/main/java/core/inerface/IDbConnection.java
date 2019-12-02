package core.inerface;

import annotation.Id;
import fj.P3;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
     * 数据源
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


    /**
     * 自定义sql
     * @param cls 实体类型
     * @param sql 自定义sql
     * @param values 参数
     * @return 查询结果
     */
    <T> List<T> sqlQuery(Class<T> cls, String sql, Object... values);

    /**
     * 根据主键查询
     * @param cls 实体类型
     * @param id 主键
     * @return 查询结果
     */
    <T> T getById(Class<T> cls, Serializable id);

    /**
     * 根据id批量查询
     * @param cls 实体类型
     * @param ids 主键列表
     * @return 查询结果
     */
    <T> List<T> getByIds(Class<T> cls, Collection<?> ids);

    /**
     * sql执行器
     * @param p3 查询参数 p1:实体类型 p2:sql p3:参数
     * @return 查询结果
     */
    <T> List<T> genExecute(P3<Class<?>,String,List<Object>> p3);

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
    void openTransaction(Supplier<?> f) throws SQLException;

    /**
     * 根据主键更新（非全表更新）
     * @param cls 实体类型
     * @param id 主键
     * @param updates 更新内容
     */
    <T>void updateById(Class<T> cls, Serializable id, Consumer<T> updates);

    /**
     * 单个更新,全表更新
     * @param entity 实体
     * @return 是否成功
     */
    <T>int update(T entity);

    /**
     * 批量更新 全表更新
     * @param entities 实体列表
     * @return 是否成功
     */
    <T> int update(List<T> entities);

    /**
     * 根据主键删除
     * @param cls 删除实体类型
     * @param id 主键
     */
    <T> int deleteById(Class<T> cls ,Serializable id);

    /**
     * 单个删除
     * @param entity 删除对象
     * @return 删除条数
     */
    <T> int delete(T entity);

    /**
     * 批量删除
     * @param ids 主键
     * @return 删除记录数量
     */
    <T>int deleteByIds(Class<T> cls,List<Object> ids);
}
