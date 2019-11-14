package core;

import core.inerface.IStatement;
import lombok.Data;
/**
 * @author Leong
 */
@Data
public class Statement implements IStatement {

    String sql;

    Object[] params;

    public static IStatement createInsertStatement(TableInfo tableInfo) {
        return null;
    }

    public IStatement createUpdateStatement() {
        return null;
    }

    public IStatement createDeleteStatement() {
        return null;
    }

    public IStatement where(String column, String op, Object values) {
        return null;
    }
}
