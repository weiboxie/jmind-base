package jmind.base.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Created by weibo.xwb on 2017/9/11.
 */
public interface ICache {

    public static enum Type {
        XMEM, GUAVA, PERSISTENT, REDIS
    };

    public boolean exists(String key);

    public boolean set(String key, Object value);

    /**
     *
     * 2013-12-4
     * @param key
     * @param seconds 过期时间，单位秒，目前只有xmemcache 实现有效  和xmemcache 接口保持一致 过期参数
     * @param value

     */
    public boolean set(String key, int seconds, Object value);

    public boolean delete(String key);

    public <T> T get(String key);

    public  Map<String, ?> getMulti(Collection<String> keys);

    public Object getCache();

    public void clear();

    default <V> V computeIfAbsent(String key,final int exp,
                              Function<String, ? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
      try {
          V v;
          if ((v = get(key)) == null) {
              V newValue;
              if ((newValue = mappingFunction.apply(key)) != null) {
                  set(key, exp,newValue);
                  return newValue;
              }
          }
          return v;
      }catch (Exception e){
          e.printStackTrace();
          return mappingFunction.apply(key);
      }
    }

    default <V> V computeIfAbsent(String key,
                              Function<? super String, ? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        try{
            V v;
            if ((v = get(key)) == null) {
                V newValue;
                if ((newValue = mappingFunction.apply(key)) != null) {
                    set(key,newValue);
                    return newValue;
                }
            }
            return v;
        }catch (Exception e){
            e.printStackTrace();
            return mappingFunction.apply(key);
        }
    }
}
