package jmind.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodMonitor {

    public static enum Store {
        file, mongodb
    };

    /** 是否存储返回结果 ，默认false**/
    boolean value() default false;

    /** 1 文件 ，2 mongodb*/
    Store store() default Store.file;

    /*****是否存储参数， 默认 false ，存储 ****/
    boolean args() default false;

}
