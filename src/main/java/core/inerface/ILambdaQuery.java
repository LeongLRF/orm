package core.inerface;

import core.support.SFunction;

import java.util.List;
import java.util.function.Function;

/**
 * @author Leong
 */
public interface ILambdaQuery<T>  {

    ILambdaQuery<T> whereEq(SFunction<T,Object> column, Object value);

    ILambdaQuery<T> in(SFunction<T,Object> column, List<Object> values);

    List<T> toList();

    T one();
}
