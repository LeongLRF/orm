package core.inerface;

import core.support.Page;
import core.support.SFunction;
import core.support.TableInfoCache;

import java.util.List;
import java.util.function.Function;

/**
 * @author Leong
 * lambda表达式查询抽象
 */
public interface ILambdaQuery<T> extends Fun {

    /**
     * 等于 例如 select * from table where xxx = xxx
     *
     * @param column 字段
     * @param value  参数
     * @return ILambdaQuery
     */
    ILambdaQuery<T> whereEq(SFunction<T, Object> column, Object value);

    /**
     * 自定义where查询
     *
     * @param sql    自定义sql
     * @param values 参数
     * @return ILambdaQuery
     */
    ILambdaQuery<T> where(String sql, Object... values);

    /**
     * in查询 例如 select * from table where xxx in (xxx,xxx)
     *
     * @param column 字段
     * @param values 参数列表
     * @return ILambdaQuery
     */
    ILambdaQuery<T> in(SFunction<T, Object> column, List<Object> values);

    /**
     * limit 查询
     */
    ILambdaQuery<T> limit(int form ,int to);

    /**
     * between查询 例如 select * from table where xxx between xxx and xxx
     *
     * @param column 字段
     * @param value  参数1
     * @param value2 参数2
     * @return ILambdaQuery
     */
    ILambdaQuery<T> between(SFunction<T, Object> column, Object value, Object value2);
    /**
     * 分页查询
     * @param current 当前页
     * @param pageSize 每页大小
     * @return Page
     */
    Page<T> page(int current, int pageSize);

    /**
     * 启动查询
     *
     * @return 查询结果
     */
    List<T> toList();

    /**
     * 启动查询
     *
     * @return 查询结果（单个）
     */
    default T one() {
        List<T> list = toList();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    /**
     * 计数查询
     *
     * @return 查询集合大小
     */
    default int count() {
        return toList().size();
    }

    default Object sum(SFunction<T, Object> f) {
        return sum(TableInfoCache.convertToFieldName(f));
    }

    ILambdaQuery<T> select(SFunction<T,Object> f);
}
