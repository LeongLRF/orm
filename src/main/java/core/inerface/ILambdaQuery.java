package core.inerface;

import java.util.List;
import java.util.function.Function;

/**
 * @author Leong
 */
public interface ILambdaQuery<T>  {

    ILambdaQuery<T> whereEq(Function<T,Object> column,Object value);

    ILambdaQuery<T> in(Function<T,Object> column, List<Object> values);

    List<T> toList();

    T one();
}
