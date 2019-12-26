package core;

import com.mysql.cj.util.StringUtils;
import core.inerface.IStatement;
import exception.ExceptionHelper;
import fj.P;
import fj.P2;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.EntityUtil;
import util.StringPool;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Leong
 * sql 片段
 */
@Data
public class Statement implements IStatement {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    String sql;

    List<Object> params = new ArrayList<>();

    boolean auto;

    public Statement(String sql, Map<String, Object> params, boolean auto) {
        this.sql = sql;
        this.params = new ArrayList<>(params.values());
        this.auto = auto;
    }

    public Statement(String sql, List<Object> params) {
        this.sql = sql;
        this.params = params;
    }

    public Statement() {
    }

    public static Statement createInsertStatement(Object entity) {
        Class<?> cls = entity.getClass();
        TableInfo tableInfo = EntityUtil.getTableInfo(cls);
        Map<String, Object> values = EntityUtil.getValues(entity);
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

    static <T> Statement createUpdateStatement(Class<T> cls, Consumer<T> updates, Object id) {
        T entity = DbConnection.createEntity(cls);
        updates.accept(entity);
        return createUpdateStatement(entity, id, cls);
    }

    static <T> Statement createUpdateStatement(T entity, Object id, Class<T> cls) {
        TableInfo tableInfo = EntityUtil.getTableInfo(cls);
        P2<String, List<Object>> setsAndObj = getSets(entity);
        List<Object> objects = setsAndObj._2();
        objects.add(id);
        String wheres = tableInfo.getPrimaryKey().getName() + " = ?";
        return createUpdateStatement(tableInfo.getTableName(), setsAndObj._1(), objects, wheres);
    }

    static <T> P2<String, List<Object>> getSets(T entity) {
        Map<String, Object> values = EntityUtil.getValues(entity);
        List<Object> objects = new ArrayList<>();
        String sets = values.entrySet().stream().filter(it -> it.getValue() != null).map(it -> {
            objects.add(it.getValue());
            return it.getKey() + " = ?";
        }).collect(Collectors.joining(","));
        return P.p(sets, objects);
    }

    static Statement createUpdateStatement(String tableName, String sets, List<Object> params, String wheres) {
        Statement statement = new Statement();
        ExceptionHelper.throwException(!StringUtils.isNullOrEmpty(wheres), "不能全表更新！你这个猪头");
        statement.sql = "UPDATE " + tableName + " SET " + sets + " WHERE " + wheres;
        statement.params = params;
        return statement;
    }

    @Override
    public PreparedStatement createPreparedStatement(Connection connection, Integer flag) {
        PreparedStatement preparedStatement;
        try {
            if (flag != null) {
                preparedStatement = connection.prepareStatement(sql, flag);
            } else {
                preparedStatement = connection.prepareStatement(sql);
            }
            DbConnection.setParams(preparedStatement, params);
            logger.info("Execute SQL : " + sql);
            logger.info("Params : " + params.toString());
            return preparedStatement;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Statement createDeleteStatement(Class<?> cls, Serializable id) {
        TableInfo tableInfo = EntityUtil.getTableInfo(cls);
        String sql = "DELETE FROM " + tableInfo.getTableName() + " WHERE " + tableInfo.getPrimaryKey().getName() + " = ?";
        return new Statement(sql, Collections.singletonList(id));
    }

    public static Statement createDeleteStatement(Class<?> cls, List<Object> id) {
        TableInfo tableInfo = EntityUtil.getTableInfo(cls);
        String sql = "DELETE FROM " + tableInfo.getTableName() + " WHERE " + tableInfo.getPrimaryKey().getName() + " in " + DbConnection.createParameterPlaceHolder(id.size());
        return new Statement(sql, id);
    }

    public static Statement createSelectStatement(Class<?> cls, Serializable id) {
        TableInfo tableInfo = EntityUtil.getTableInfo(cls);
        Statement statement = new Statement();
        statement.sql = "SELECT * FROM " + tableInfo.getTableName() + " WHERE " + tableInfo.getPrimaryKey().getName() + " = ?";
        statement.params.add(id);
        return statement;
    }

}
