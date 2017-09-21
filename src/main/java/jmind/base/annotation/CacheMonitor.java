package jmind.base.annotation;

import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jmind.base.cache.ICache;
import jmind.base.util.GlobalConstants;

/**
 * aop 增加缓存，适合缓存过期机制
 * http://blog.csdn.net/vipwalkingdog/article/details/7687410
 * org.springframework.cache.annotation.Cacheable
 * org.springframework.cache.annotation.CacheEvict
 * @author wbxie
 * 2013-10-23
 */
@Target({ METHOD, LOCAL_VARIABLE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheMonitor {

    /**
     * memcache name
     * 2013-12-4 
     * @return
     */
    String name() default "common";

    /**
     * 缓存前缀
     * @return
     */
    String prefix() default "";

    /**
     * 缓存时间，单位秒,默认1周
    
     * 2013-10-23 
     * @return
     */
    int exp() default GlobalConstants.WEEK;

    /**
     * 是否删除缓存
     * 2013-12-4 
     * @return
     */
    boolean remove() default false;

    /**
     * Spring Expression Language (SpEL) attribute for computing the key dynamically.
     * <p>Default is "", meaning all method parameters are considered as a key.
     * 2013-10-23 
     * @return
     */
    String spel() default "";

    ICache.Type type() default ICache.Type.GUAVA;

}
