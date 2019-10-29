package annotation;

import util.JdbcType;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {
    String name();
    String jdbcType() default JdbcType.UNDEFINE;
}
