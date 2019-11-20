package core.inerface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public interface DefaultCache {
    Logger LOGGER = LoggerFactory.getLogger(DefaultCache.class);

    static List<Object> getValue(Class<?> cls, String sql) {
        System.out.println("no impl");
        return null;
    }

    static void setValue(Class<?> cls, String sql, List<Object> objects){
        System.out.println("no impl");
    };

    static void update(Class<?> cls){
        System.out.println("no impl");
    };
}
