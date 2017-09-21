package jmind.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface WebValidation {

    /**
     * 是否需要登录
     * @return
     */
    boolean login() default true;

    /**
     * ajax jsonp
     * @return
     */
    String jsonp() default "callback";

    String back() default "";
}
