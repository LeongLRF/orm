package core;

import core.inerface.ISelectQuery;
import core.inerface.IStatement;
import jdk.nashorn.internal.runtime.linker.LinkerCallSite;
import lombok.Data;
import util.EntityUtil;
import util.StringPool;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Leong
 */
@Data
public class Statement implements IStatement {

    String sql;

    List<Object> params = new ArrayList<>();

    boolean auto;

    public Statement(String sql, Map<String,Object> params, boolean auto) {
        this.sql = sql;
        this.params = new ArrayList<>(params.values());
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
        Map<String,Object> values = EntityUtil.getValues(entity);
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

    public static <T> Statement createUpdateStatement(Class<T> cls, Consumer<T> updates) {
        TableInfo tableInfo = EntityUtil.getTableInfo(cls);
        T entity = DbConnection.createEntity(cls);
        updates.accept(entity);
        Map<String,Object> values = EntityUtil.getValues(entity);
        System.out.println(entity);
        return null;
    }

    public static Statement createDeleteStatement() {
        return null;
    }

    public static Statement createSelectStatement(Class<?> cls, Serializable id) {
        TableInfo tableInfo = EntityUtil.getTableInfo(cls);
        Statement statement = new Statement();
        statement.sql = "SELECT * FROM " + tableInfo.getTableName() + " WHERE " + tableInfo.getPrimaryKey().getName() + " = ?";
        statement.params.add(id);
        return statement;
    }


    public Statement where(String column, String op, Object values) {
        return null;
    }
}
