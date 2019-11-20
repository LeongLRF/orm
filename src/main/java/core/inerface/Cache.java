package core.inerface;

import java.util.List;
import java.util.Map;

public interface Cache {

    Map<String, List<Object>> getCache();

    void setCache(Map<String, List<Object>> cache);
}
