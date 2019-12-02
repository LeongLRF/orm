package annotation;

import core.support.IdType;
import core.support.JdbcType;

import java.lang.annotation.*;
/**
 * @author Leong
 * 数据表主键注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Id {
    String value() default "";
    String type() default JdbcType.UNDEFINE;
    String idType() default IdType.AUTO;
}
