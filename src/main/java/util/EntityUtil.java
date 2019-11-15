package util;

import annotation.Column;
import annotation.Id;
import annotation.Table;
import core.ColumnInfo;
import core.DbConnection;
import core.TableInfo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class EntityUtil {

    public static <T> TableInfo getTableInfo(Class<T> cls) {
        Field[] fields = cls.getDeclaredFields();
        Table table = cls.getAnnotation(Table.class);
        TableInfo info = new TableInfo();
        info.setCls(cls);
        info.setTableName(table.name());
        Map<String, ColumnInfo> columnInfos = new HashMap<>();
        Arrays.stream(fields).forEach(it -> {
            if (it.isAnnotationPresent(Id.class)) {
                Id primaryKey = it.getAnnotation(Id.class);
                if (primaryKey.idType().equals(StringPool.AUTO)) {
                    info.setAutoIncrement(true);
                } else {
                    info.setAutoIncrement(false);
                }
                info.setPrimaryKey(ColumnInfo.createColumn(primaryKey.value(), primaryKey.type(), ColumnInfo.PRIMARY_KEY));
            }
            if (it.isAnnotationPresent(Column.class)) {
                Column column = it.getAnnotation(Column.class);
                columnInfos.put(it.getName(), ColumnInfo.createColumn(column.name(), column.jdbcType(), ColumnInfo.NORMAL_KEY));
            }
        });
        info.setColumns(columnInfos);
        return info;
    }

    public static <T> List<Object> getValues(T entity) {
        Class<?> cls = entity.getClass();
        Field[] fields = cls.getDeclaredFields();
        List<Object> values = new ArrayList<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                String name = field.getName();
                name = name.replaceFirst(name.substring(0, 1), name.substring(0, 1).toUpperCase());
                Method method;
                try {
                    method = cls.getMethod("get" + name);
                    Class<?> type = field.getType();
                    values.add(TypeConverter.convert(method.invoke(entity), type));
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return values;
    }

    public static <T> List<T> resultSetToEntity(Class<T> cls, List<Map<String, Object>> result) {
        Field[] fields = cls.getDeclaredFields();
        List<T> list = new ArrayList<>();
        for (Map<String, Object> map : result) {
            T t = DbConnection.createEntity(cls);
            for (Field field : fields) {
                if (field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(Column.class)) {
                    String dbName;
                    if (field.isAnnotationPresent(Id.class)) {
                        Id id = field.getAnnotation(Id.class);
                        dbName = id.value();
                    } else {
                        Column column = field.getAnnotation(Column.class);
                        dbName = column.name();
                    }
                    String name = field.getName();
                    name = name.replaceFirst(name.substring(0, 1), name.substring(0, 1).toUpperCase());
                    try {
                        Method method = cls.getMethod("set" + name, field.getType());
                        method.invoke(t, map.get(dbName));
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
            list.add(t);
        }
        return list;
    }
}
