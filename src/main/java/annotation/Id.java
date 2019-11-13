package annotation;

import util.IdType;
import util.JdbcType;

import java.lang.annotation.*;
/**
 * @author Leong
 * 数据表主键注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Id {
    String value() default JdbcType.DEFAULT_PK;
    String type() default "long";
    String idType() default IdType.AUTO;
}
