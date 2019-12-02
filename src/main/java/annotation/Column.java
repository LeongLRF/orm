package annotation;

import core.support.JdbcType;

import java.lang.annotation.*;
/**
 * @author Leong
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {
    String value() default "";
    String jdbcType() default JdbcType.UNDEFINE;
}
