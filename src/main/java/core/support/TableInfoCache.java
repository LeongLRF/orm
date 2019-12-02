package core.support;

import core.TableInfo;
import lombok.experimental.UtilityClass;
import util.EntityUtil;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Leong
 * 表信息缓存与序列化lambda缓存
 */
@UtilityClass
public class TableInfoCache {

    /**
     * 表信息缓存
     * 弱引用，随时会被jvm回收
     */
    private static Map<Class<?>, SoftReference<TableInfo>> tableInfoMap = new ConcurrentHashMap<>(16);

    /**
     * lambda表达式序列化缓存
     * 弱引用，随时会被jvm回收
     */
    private static Map<Class<?>, SoftReference<SerializedLambda>> serializedLambdaMap = new ConcurrentHashMap<>(16);

    /**
     * 获取表信息
     *
     * @param cls 实体类型
     * @return 表信息
     */
    public TableInfo get(Class<?> cls) {
        return Optional.ofNullable(tableInfoMap.get(cls)).map(SoftReference::get).orElse(null);
    }

    /**
     * install表信息缓存
     *
     * @param cls       实体类型
     * @param tableInfo 表信息
     */
    public void set(Class<?> cls, TableInfo tableInfo) {
        SoftReference<TableInfo> softReference = new SoftReference<>(tableInfo);
        tableInfoMap.put(cls, softReference);
    }


    private  SoftReference<SerializedLambda> getSerializedLambda(Serializable fn) {
        return Optional.ofNullable(serializedLambdaMap.get(fn.getClass()))
                .orElseGet(() -> {
                    try {
                        Method method = fn.getClass().getDeclaredMethod("writeReplace");
                        method.setAccessible(Boolean.TRUE);
                        SerializedLambda lambda = (SerializedLambda) method.invoke(fn);
                        SoftReference<SerializedLambda> serializedLambdaSoftReference = new SoftReference<>(lambda);
                        serializedLambdaMap.put(fn.getClass(), serializedLambdaSoftReference);
                        return serializedLambdaSoftReference;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                });
    }

    /***
     * 转换方法引用为属性名
     * @param fn 可序列化的function
     * @return 列名
     */
    public  <T, Object> String convertToFieldName(SFunction<T, Object> fn) {
        SerializedLambda lambda = getSerializedLambda(fn).get();
        if (lambda != null) {
            String methodName = lambda.getImplMethodName();
            String prefix = null;
            if (methodName.startsWith("get")) {
                prefix = "get";
            } else if (methodName.startsWith("is")) {
                prefix = "is";
            }
            if (prefix == null) {
                throw new RuntimeException("请严格遵循javabean的写法");
            }
            methodName = methodName.replace(prefix, "");
            methodName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
            System.out.println(methodName);
            Class<?> cls = toClassConfident(normalName(lambda.getImplClass()));
            TableInfo tableInfo = EntityUtil.getTableInfo(cls);
            if (tableInfo == null) {
                throw new RuntimeException("没有找到相关信息");
            }
            return tableInfo.getColumns().get(methodName).getName();
        }
        return null;
    }

    /**
     * 返回正常的类名
     *
     * @param name 类名
     * @return 正常的类名
     */
    private String normalName(String name) {
        return name.replace('/', '.');
    }

    private Class<?> toClassConfident(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("找不到指定的class！请仅在明确确定会有 class 的时候，调用该方法", e);
        }
    }

}
