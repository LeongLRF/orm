package util;

import annotation.Column;
import annotation.Id;
import annotation.Table;
import com.alibaba.fastjson.JSON;
import core.ColumnInfo;
import core.DbConnection;
import core.TableInfo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;
/**
 * @author Leong
 */
@SuppressWarnings("all")
public class EntityUtil {

    public static <T> TableInfo getTableInfo(Class<T> cls) {
        Field[] fields = cls.getDeclaredFields();
        Table table = cls.getAnnotation(Table.class);
        if (table==null) {
            cls = (Class<T>) cls.getSuperclass();
            return getTableInfo(cls);
        }
        TableInfo info = new TableInfo();
        info.setCls(cls.getSuperclass());
        info.setTableName(table.value());
        Map<String, ColumnInfo> columnInfos = new HashMap<>(16);
        Arrays.stream(fields).forEach(it -> {
            if (it.isAnnotationPresent(Id.class)) {
                Id primaryKey = it.getAnnotation(Id.class);
                info.setAutoIncrement(primaryKey.idType().equals(StringPool.AUTO));
                info.setPrimaryKey(ColumnInfo.createColumn(primaryKey.value(), primaryKey.type(), ColumnInfo.PRIMARY_KEY));
            }
            if (it.isAnnotationPresent(Column.class)) {
                Column column = it.getAnnotation(Column.class);
                columnInfos.put(it.getName(), ColumnInfo.createColumn(column.value(), column.jdbcType(), ColumnInfo.NORMAL_KEY));
            }
        });
        info.setColumns(columnInfos);
        return info;
    }

    public static <T> Map<String, Object> getValues(T entity) {
        Class<?> cls = entity.getClass();
        if ( cls.getAnnotation(Table.class)==null){
            cls = cls.getSuperclass();
        }
        Field[] fields = cls.getDeclaredFields();
        Map<String, Object> values = new HashMap<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                String name = field.getName();
                name = name.replaceFirst(name.substring(0, 1), name.substring(0, 1).toUpperCase());
                Method method;
                try {
                    method = cls.getMethod("get" + name);
                    Class<?> type = field.getType();
                    if (column.jdbcType().equals(StringPool.JSON)) {
                        values.put(field.getName(), method.invoke(entity) == null ? null : JSON.toJSONString(method.invoke(entity)));
                    } else {
                        values.put(column.value(), TypeConverter.convert(method.invoke(entity), type));
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return values;
    }

    public static <T> void setId(T entity, Object id) {
        Class<?> cls = entity.getClass();
        Field[] fields = cls.getDeclaredFields();
        Stream.of(fields).filter(field -> field.isAnnotationPresent(Id.class)).findFirst()
                .map(it -> {
                    String name = it.getName();
                    name = name.replaceFirst(name.substring(0, 1), name.substring(0, 1).toUpperCase());
                    try {
                        Method method = cls.getMethod("set" + name, it.getType());
                        method.invoke(entity, TypeConverter.convert(id, it.getType()));
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    return it;
                });
    }

    public static <T> List<T> resultSetToEntity(Class<T> cls, List<Map<String, Object>> result) {
        Field[] fields = cls.getDeclaredFields();
        List<T> list = new ArrayList<>();
        for (Map<String, Object> map : result) {
            T t = DbConnection.createEntity(cls);
            for (Field field : fields) {
                if (field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(Column.class)) {
                    String dbName;
                    String type;
                    if (field.isAnnotationPresent(Id.class)) {
                        Id id = field.getAnnotation(Id.class);
                        dbName = id.value();
                        type = id.type();
                    } else {
                        Column column = field.getAnnotation(Column.class);
                        dbName = column.value();
                        type = column.jdbcType();
                    }
                    String name = field.getName();
                    name = name.replaceFirst(name.substring(0, 1), name.substring(0, 1).toUpperCase());
                    try {
                        Method method = cls.getMethod("set" + name, field.getType());
                        if (type.equals(StringPool.JSON)) {
                            if (map.get(dbName) != null) {
                                if (field.getType().getSuperclass().getName().contains("Collection")) {
                                    method.invoke(t, JSON.parseArray(map.get(dbName).toString(), field.getType()));
                                } else {
                                    method.invoke(t, JSON.parseObject(map.get(dbName).toString(), field.getType()));
                                }
                            }
                        } else {
                            method.invoke(t, map.get(dbName));
                        }
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
            list.add(t);
        }
        return list;
    }

    public static <T> Object getId(T entity) {
        Class<?> cls = entity.getClass();
        Field[] fields = cls.getDeclaredFields();
        Object id;
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                String name = field.getName();
                name = name.replaceFirst(name.substring(0, 1), name.substring(0, 1).toUpperCase());
                try {
                    Method method = cls.getMethod("get" + name);
                    id = method.invoke(entity);
                    return id;
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }
}
