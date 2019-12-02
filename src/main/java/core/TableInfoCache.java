package core;

import lombok.experimental.UtilityClass;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@UtilityClass
public class TableInfoCache {

    /**
     *  表信息缓存
     * 弱引用，内存吃紧时 jvm会回收
     */
    private static Map<Class<?>, SoftReference<TableInfo>> tableInfoMap = new HashMap<>(16);

    public TableInfo get(Class<?> cls){
        return Optional.ofNullable(tableInfoMap.get(cls)).map(SoftReference::get).orElse(null);
    }

    public void set(Class<?> cls,TableInfo tableInfo){
        SoftReference<TableInfo> softReference = new SoftReference<>(tableInfo);
        tableInfoMap.put(cls,softReference);
    }

}
