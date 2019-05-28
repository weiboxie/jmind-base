package jmind.base.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by weibo.xwb on 2017/9/12.
 */
public class LocalCache implements ICache {

    private  final Map<String, Object> map;
    public LocalCache(Map<String,Object> map){
        this.map=map;

    }

    @Override
    public boolean exists(String key) {
        return map.containsKey(key);
    }

    @Override
    public boolean set(String key, Object value) {
         map.put(key,value);
        return true;
    }

    @Override
    public boolean set(String key, int seconds, Object value) {
        map.put(key,value);
        return  true;
    }

    @Override
    public boolean delete(String key) {
        return map.remove(key)!=null;
    }

    @Override
    public boolean mdelete(List<String> keys) {
        keys.forEach(k->map.remove(k));
        return true;
    }

    @Override
    public <T> T get(String key) {
        return (T) map.get(key);
    }

    @Override
    public Map<String, ?> getMulti(Collection<String> keys) {
        Map<String, Object> map = new HashMap<>();
        for (String key : keys) {
            Object value = get(key);
            if (value != null) {
                map.put(key, value);
            }
        }
        return map;
    }

    @Override
    public Object getCache() {
        return map;
    }

    @Override
    public void clear() {
        map.clear();
    }
}
