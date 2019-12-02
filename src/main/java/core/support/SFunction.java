package core.support;

import java.io.Serializable;
import java.util.function.Function;
/**
 * @author Leong
 * 支持序列化的function接口
 * @see TableInfoCache
 */
@FunctionalInterface
public interface SFunction<T,R> extends Function<T,R>, Serializable {
}
