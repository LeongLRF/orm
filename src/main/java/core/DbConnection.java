package core;

import core.inerface.IDbConnection;
import core.inerface.ISelectQuery;
import core.inerface.IStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.EntityUtil;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Leong
 */
public class DbConnection implements IDbConnection {

    private final Logger logger = LoggerFactory.getLogger(DbConnection.class);

    private Connection connection;
    private boolean onTransaction = false;

    public DbConnection(Connection connection) {
        this.connection = connection;
        logger.info("init DbConnection ....");
    }

    public static <T> T createEntity(Class<T> cls) {
        try {
            return cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public <T> ISelectQuery<T> form(Class<T> cls) {
        return new SelectQuery<>(this, cls);
    }

    @Override
    public ResultSet execute(ISelectQuery selectQuery) {
        return null;
    }

    public boolean execute(IStatement statement,boolean isAuto){
        int genflag = java.sql.Statement.NO_GENERATED_KEYS;
        if (isAuto) {
           genflag = java.sql.Statement.RETURN_GENERATED_KEYS;
        }
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(statement.getSql(),genflag);
            setParams(preparedStatement,statement.getParams());
            logger.info("Execute sql : " + statement.getSql());
            return preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public <T> List<T> sqlQuery(Class<T> cls, String sql, Object... values) {
        return null;
    }

    @Override
    public <T> T getById(Class<T> cls, Serializable id) {
        return null;
    }

    @Override
    public <T> List<T> getByIds(Class<T> cls, List<? extends Serializable> ids) {
        return null;
    }

    @Override
    public <T> int insert(T entity) {
        Class<?> cls = entity.getClass();
        TableInfo tableInfo = EntityUtil.getTableInfo(cls);
        Statement statement = Statement.createInsertStatement(tableInfo,entity);
        if (execute(statement,tableInfo.isAutoIncrement())){
            return 1;
        }
        return 0;
    }


    static String createParameterPlaceHolder(int num) {
        return "("+ Stream.iterate("?", p -> p).limit(num).collect(Collectors.joining(",")) + ")";
    }

    private void setParams(PreparedStatement statement, List<Object> values) {
        if (values == null) {
            return;
        }
        for (int i = 0; i < values.size(); i++) {
            try {
                statement.setObject(i + 1, values.get(i));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
