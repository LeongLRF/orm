package core;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Leong
 * 表
 */
@Data
public class TableInfo {

    /**
     * 类名
     */
    Class<?> cls;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 字段
     */
    private Map<String,ColumnInfo> columns;

    /**
     * 主键
     */
    private ColumnInfo primaryKey;

    /**
     * 是否自增
     */
    private boolean autoIncrement;

    List<String> columns(){
        return columns.values().stream().map(ColumnInfo::getName).collect(Collectors.toList());
    }

}
