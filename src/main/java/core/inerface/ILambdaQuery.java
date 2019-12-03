package core.inerface;

import core.support.SFunction;

import java.util.List;
import java.util.function.Function;

/**
 * @author Leong
 * lambda表达式查询抽象
 */
public interface ILambdaQuery<T> {

    /**
     * 等于 例如 select * from table where xxx = xxx
     *
     * @param column 字段
     * @param value  参数
     * @return ILambdaQuery
     */
    ILambdaQuery<T> whereEq(SFunction<T, Object> column, Object value);

    /**
     * in查询 例如 select * from table where xxx in (xxx,xxx)
     * @param column 字段
     * @param values 参数列表
     * @return ILambdaQuery
     */
    ILambdaQuery<T> in(SFunction<T, Object> column, List<Object> values);

    /**
     * between查询 例如 select * from table where xxx between xxx and xxx
     * @param column 字段
     * @param value 参数1
     * @param value2 参数2
     * @return ILambdaQuery
     */
    ILambdaQuery<T> between(SFunction<T,Object> column,Object value,Object value2);

    /**
     * 启动查询
     * @return 查询结果
     */
    List<T> toList();

    /**
     * 启动查询
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
}
