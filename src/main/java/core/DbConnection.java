package core;

import core.inerface.IDbConnection;
import core.inerface.ISelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.EntityUtil;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        Statement statement = Statement.createInsertStatement(tableInfo);
        String sql = statement.sql;
        return 0;
    }


    public static String createParameterPlaceHolder(int num) {
        return Stream.iterate("?", p -> p).limit(num).collect(Collectors.joining(","));
    }
}
