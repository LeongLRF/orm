package core.inerface;

import java.util.List;

public interface DefaultCache {

    static List<Object> getValue(Class<?> cls, String sql) {
        System.out.println("no impl");
        return null;
    }

    static void setValue(Class<?> cls, String sql, List<Object> objects){
        System.out.println("no impl");
    }

    static void update(Class<?> cls){
        System.out.println("no impl");
    }
}
