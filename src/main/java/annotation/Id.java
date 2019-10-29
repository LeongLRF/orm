package annotation;

import util.IdType;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Id {
    String type() default "long";
    String idType() default IdType.AUTO;
}
