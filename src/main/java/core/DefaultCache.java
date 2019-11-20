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
    public static  ConcurrentHashMap<Class<?>, core.inerface.Cache> cache = new ConcurrentHashMap<>(16);

    public static List<Object> getValue(Class<?> cls,String sql){
        core.inerface.Cache cache = getCache().get(cls);
        if (cache == null){
            return null;
        }
        return cache.getCache().get(sql);
    }

    public static void update(Class<?> cls){
        core.inerface.Cache cache = getCache().get(cls);
        if (cache==null){
            return;
        }
        cache.getCache().clear();
    }

    public static void setValue(Class<?> cls,String sql,List<Object> objects){
        core.inerface.Cache cache = new Cache();
        Map<String,List<Object>> map = new HashMap<>(16);
        map.put(sql,objects);
        cache.setCache(map);
        getCache().put(cls,cache);
    }
}
