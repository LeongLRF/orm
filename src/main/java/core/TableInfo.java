package core;
import lombok.Data;

import java.util.List;
/**
 * @author Leong
 * 表
 */
@Data
public class TableInfo<T> {

    /**
     * 类名
     */
    Class<T> cls;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 字段
     */
    private List<ColumnInfo> columns;

    /**
     * 主键
     */
    private ColumnInfo primaryKey;

}
