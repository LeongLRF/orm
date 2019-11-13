package core;

import lombok.Data;
/**
 * @author Leong
 * 字段
 */
@Data
public class ColumnInfo {

    public static final int NORMAL_KEY = 0;
    public static final int PRIMARY_KEY = 1;
    public static final int FOREIGN_KEY = 2;

    /**
     * 字段名
     */
    private String name;

    /**
     * 字段类型
     */
    private String type;

    /**
     * 字段的健类型
     */
    private Integer keyType;


    public static ColumnInfo createColumn(String name, String type, int keyType){
        ColumnInfo columnInfo = new ColumnInfo();
        columnInfo.setName(name);
        columnInfo.setType(type);
        columnInfo.setKeyType(keyType);
        return columnInfo;
    }


}
