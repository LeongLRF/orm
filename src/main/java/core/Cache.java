package core;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
/**
 * @author Leong
 * 缓存
 */
public class Cache implements core.inerface.Cache {

    @Getter
    @Setter
    public Map<String, List<Object>> cache;
}
