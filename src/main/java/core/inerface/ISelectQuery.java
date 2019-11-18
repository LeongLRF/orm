package core.inerface;

import core.TableInfo;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;

/**
 * @author Leong
 */
public interface ISelectQuery<T> extends ILambdaQuery<T> {

    /**
     * 数据库连接
     * @return 数据库连接包装类
     */
    IDbConnection getConnection();

    /**
     * 获取表信息
     * @return 表信息
     */
    TableInfo getTableInfo();

    /**
     * 查询参数
     * @return 查询参数
     */
    List<Object> getParams();

    /**
     * 查询sql
     * @return 拼接好的sql
     */
    String getSql();

    /**
     * 查询类
     * @return 实体class
     */
    Class<T> getCls();

    ISelectQuery<T> where(String column, Object value);

    /**
     * IN字符串拼接 例如：SELECT * FROM table WHERE xxx IN (xxx,xxx)
     * @param column 想要查询的字段（要跟数据库上的字段一致）
     * @param ids IN里面的内容
     * @return SelectQuery
     * @see core.SelectQuery
     */
    ISelectQuery<T> in(String column, List<Object> ids);

    /**
     * WHERE字符串拍拼接 例如：SELECT * FROM table WHERE xxx = xxx
     * @param column 想要查询的字段
     * @param value 想要查询的值
     * @return SelectQuery
     * @see core.SelectQuery
     */
    ISelectQuery<T> whereEq(String column, Object value);

    /**
     * IN子查询 例如：SELECT * FROM table WHERE id IN (SELECT xxx FROM table...)
     * @param column 查询字段
     * @param sql 子查询
     * @param values 子查询参数有
     * @return SelectQuery
     * @see core.SelectQuery
     */
    ISelectQuery<T> inSql(String column,String sql,Object ...values);

    /**
     * 查询具体字段 例如：SELECT name FROM table ...
     * @param column 查询字段
     * @return SelectQuery
     */
    ISelectQuery<T> select(String column);

    /**
     *  查询结果（多个）
     * @return 查询所得到的值
     */
    @Override
    List<T> toList();

    /**
     * 查询结果（单个）
     * @return 查询结果
     */
    @Override
    T one();

    /**
     * 排序 例如：SELECT * FROM table WHERE ... ORDER BY xxx
     * @param orderBy 排序字段
     * @return SelectQuery
     */
    ISelectQuery<T> orderBy(String orderBy);

    /**
     * 拼接sql
     * @param statements sql片段
     */
    void makeSql(List<IStatement> statements);

    /**
     * 获取sql片段
     * @return sql片段
     */
    List<IStatement> getWheres();
}
