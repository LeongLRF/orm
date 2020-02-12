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

    long expireTime() default 60;

    interface Expire {
        long ONE_DAY = 60 * 60 * 24;
        long ONE_YEAR = ONE_DAY * 365;
        long ONE_MONTH = ONE_DAY * 30;
        long ONE_WEEK = ONE_DAY * 7;
    }
}
