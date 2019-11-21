package core.inerface;

import core.TableInfo;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Leong
 */
public interface ISelectQuery<T> {

    /**
     * 数据库连接
     *
     * @return 数据库连接包装类
     */
    IDbConnection getConnection();

    /**
     * 获取表信息
     *
     * @return 表信息
     */
    TableInfo getTableInfo();

    /**
     * 查询参数
     *
     * @return 查询参数
     */
    List<Object> getParams();

    void setParams(List<Object> params);

    /**
     * 查询sql
     *
     * @return 拼接好的sql
     */
    String getSql();

    void setSql(String sql);
    /**
     * 查询类
     *
     * @return 实体class
     */
    Class<T> getCls();

    ISelectQuery<T> where(String column, Object... values);

    /**
     * IN字符串拼接 例如：SELECT * FROM table WHERE xxx IN (xxx,xxx)
     *
     * @param column 想要查询的字段（要跟数据库上的字段一致）
     * @param ids    IN里面的内容
     * @return SelectQuery
     * @see core.SelectQuery
     */
    ISelectQuery<T> in(String column, List<Object> ids);

    /**
     * WHERE字符串拍拼接 例如：SELECT * FROM table WHERE xxx = xxx
     *
     * @param column 想要查询的字段
     * @param value  想要查询的值
     * @return SelectQuery
     * @see core.SelectQuery
     */
    ISelectQuery<T> whereEq(String column, Object value);

    /**
     * whereEq 动态查询
     *
     * @param condition true则执行查询
     * @param column    想要查询的字段
     * @param value     想要查询的值
     * @return this
     */
    default ISelectQuery<T> whereEq(boolean condition, String column, Object value) {
        if (condition) {
            return whereEq(column, value);
        }
        return this;
    }

    /**
     * IN子查询 例如：SELECT * FROM table WHERE id IN (SELECT xxx FROM table...)
     *
     * @param column 查询字段
     * @param sql    子查询
     * @param values 子查询参数有
     * @return SelectQuery
     * @see core.SelectQuery
     */
    ISelectQuery<T> inSql(String column, String sql, Object... values);

    /**
     * 查询具体字段 例如：SELECT name FROM table ...
     *
     * @param column 查询字段
     * @return SelectQuery
     */
    ISelectQuery<T> select(String column);

    /**
     * between查询 例如select * from table where xxx between xxx and xxx
     *
     * @param column 查询字段
     * @param value  参数1
     * @param value2 参数2
     * @return this
     */
    ISelectQuery<T> between(String column, Object value, Object value2);

    /**
     * 同上,可以实现动态查询
     */
    default ISelectQuery<T> between(boolean condition, String column, Object value, Object value2) {
        if (condition) {
            return between(column, value, value2);
        }
        return this;
    }

    /**
     * 查询结果（多个）
     *
     * @return 查询所得到的值
     */
    List<T> toList();

    /**
     * 查询结果（单个）
     *
     * @return 查询结果
     */
    T one();

    /**
     * 排序 例如：SELECT * FROM table WHERE ... ORDER BY xxx
     *
     * @param orderBy 排序字段
     * @return SelectQuery
     */
    ISelectQuery<T> orderBy(String orderBy);

    /**
     * 拼接sql
     *
     * @param statements sql片段
     */
    void makeSql(List<IStatement> statements);

    /**
     * 获取sql片段
     *
     * @return sql片段
     */
    List<IStatement> getWheres();

    /**
     * 部分更新
     *
     * @param updates 更新内容
     * @return 更新数量
     */
    int update(Consumer<T> updates);
}
