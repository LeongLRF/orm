package util;

import annotation.Column;
import annotation.Id;
import annotation.Table;
import core.ColumnInfo;
import core.TableInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityUtil {

    public static <T> TableInfo<T> getTableInfo(Class<T> cls) {
        Field[] fields = cls.getDeclaredFields();
        Table table = cls.getAnnotation(Table.class);
        TableInfo<T> info = new TableInfo<>();
        info.setCls(cls);
        info.setTableName(table.name());
        List<ColumnInfo> columnInfos = new ArrayList<>();
        Arrays.stream(fields).forEach(it -> {
            if (it.isAnnotationPresent(Id.class)){
                Id primaryKey = it.getAnnotation(Id.class);
                info.setPrimaryKey(ColumnInfo.createColumn(primaryKey.value(),primaryKey.type(),ColumnInfo.PRIMARY_KEY));
            }
            if (it.isAnnotationPresent(Column.class)){
                Column column = it.getAnnotation(Column.class);
                columnInfos.add(ColumnInfo.createColumn(column.name(),column.jdbcType(),ColumnInfo.NORMAL_KEY));
            }
        });
        info.setColumns(columnInfos);
        return info;
    }
}
