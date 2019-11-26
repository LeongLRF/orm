package annotation;

import java.lang.annotation.*;
/**
 * @author Leong
 * 表注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
    String value() default "";
    boolean cache() default false;
    int expireTime() default 60;
}
