package core;

import core.inerface.IStatement;
import jdk.nashorn.internal.runtime.linker.LinkerCallSite;
import lombok.Data;
import util.EntityUtil;
import util.StringPool;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Leong
 */
@Data
public class Statement implements IStatement {

    String sql;

    List<Object> params;

    public Statement(String sql, List<Object> params) {
        this.sql = sql;
        this.params = params;
    }

    public static Statement createInsertStatement(TableInfo tableInfo, Object entity) {
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
        return new Statement(sql,values);
    }

    public Statement createUpdateStatement() {
        return null;
    }

    public Statement createDeleteStatement() {
        return null;
    }

    public Statement where(String column, String op, Object values) {
        return null;
    }
}
