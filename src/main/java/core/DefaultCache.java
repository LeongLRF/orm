package core;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * @author Leong
 * 一级缓存
 */
public class DefaultCache implements core.inerface.DefaultCache {

    @Getter
    public static final ConcurrentHashMap<Class<?>,Cache> CACHE = new ConcurrentHashMap<>(16);

    public static List<Object> getValue(Class<?> cls,String sql){
        Cache cache = getCACHE().get(cls);
        if (cache == null){
            return null;
        }
        return cache.cache.get(sql);
    }

    public static void update(Class<?> cls){
        Cache cache = getCACHE().get(cls);
        if (cache==null){
            return;
        }
        cache.cache.clear();
    }

    public static void setValue(Class<?> cls,String sql,List<Object> objects){
        Cache cache = new Cache();
        Map<String,List<Object>> map = new HashMap<>(16);
        map.put(sql,objects);
        cache.cache = map;
        getCACHE().put(cls,cache);
    }
}
