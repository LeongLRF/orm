package core;

import core.inerface.IStatement;
import lombok.Data;
import util.StringPool;

/**
 * @author Leong
 */
@Data
public class Statement implements IStatement {

    String sql;

    Object[] params;

    public Statement(String sql) {
        this.sql = sql;
    }

    public static Statement createInsertStatement(TableInfo tableInfo) {
        String sql = StringPool.INSERT + StringPool.SPACE +
                tableInfo.getTableName();
        return new Statement(sql);
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
