package core;

import core.inerface.ISelectQuery;
import core.inerface.IStatement;
import jdk.nashorn.internal.runtime.linker.LinkerCallSite;
import lombok.Data;
import util.EntityUtil;
import util.StringPool;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Leong
 */
@Data
public class Statement implements IStatement {

    String sql;

    List<Object> params = new ArrayList<>();

    boolean auto;

    public Statement(String sql, List<Object> params, boolean auto) {
        this.sql = sql;
        this.params = params;
        this.auto = auto;
    }

    public Statement(String sql, List<Object> params) {
        this.sql = sql;
        this.params = params;
    }

    public Statement(String sql) {
        this.sql = sql;
    }

    public Statement() {
    }

    public static Statement createInsertStatement(Object entity) {
        Class<?> cls = entity.getClass();
        TableInfo tableInfo = EntityUtil.getTableInfo(cls);
        List<Object> values = EntityUtil.getValues(entity);
        String sql = StringPool.INSERT +
                StringPool.SPACE +
                tableInfo.getTableName() +
                StringPool.SPACE +
                StringPool.LEFT_BRACKET +
                String.join(",", tableInfo.columns()) +
                StringPool.RIGHT_BRACKET +
                StringPool.VALUES +
                DbConnection.createParameterPlaceHolder(values.size());
        return new Statement(sql, values, tableInfo.isAutoIncrement());
    }

    public static Statement createUpdateStatement(TableInfo tableInfo, Object entity) {
        List<Object> values = EntityUtil.getValues(entity);
        return null;
    }

    public static Statement createDeleteStatement() {
        return null;
    }


    public Statement where(String column, String op, Object values) {
        return null;
    }
}
