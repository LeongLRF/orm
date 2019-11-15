package core;

import core.inerface.IDbConnection;
import core.inerface.ISelectQuery;
import core.inerface.IStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.EntityUtil;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Leong
 */
public class DbConnection implements IDbConnection {

    private final Logger logger = LoggerFactory.getLogger(DbConnection.class);

    private Connection connection;
    private boolean onTransaction = false;
    private DataSource dataSource;

    public DbConnection(Connection connection) {
        this.connection = connection;
        logger.info("init DbConnection with default model");
    }

    public DbConnection(DataSource dataSource) throws SQLException {
        this.dataSource = dataSource;
        this.connection = dataSource.getConnection();
        logger.info("init DbConnection with DataSource : " + dataSource.getClass().getName());
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
    public DataSource getDataSource() {
        return this.dataSource;
    }

    @Override
    public <T> ISelectQuery<T> form(Class<T> cls) {
        return new SelectQuery<>(this, cls);
    }

    @Override
    public <T> List<T> execute(ISelectQuery<T> selectQuery) {
        logger.info("Execute Select With Sql : "+selectQuery.getSql());
        logger.info("Params : " +selectQuery.getParams().toString());
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery.getSql());
            setParams(preparedStatement,selectQuery.getParams());
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Map<String,Object>> result = fetchResultSet(resultSet);
            return EntityUtil.resultSetToEntity(selectQuery.getCls(),result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean execute(IStatement statement, boolean isAuto) {
        int genflag = java.sql.Statement.NO_GENERATED_KEYS;
        if (isAuto) {
            genflag = java.sql.Statement.RETURN_GENERATED_KEYS;
        }
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(statement.getSql(), genflag);
            setParams(preparedStatement, statement.getParams());
            logger.info("Execute sql : " + statement.getSql());
            logger.info("Params : " + statement.getParams().toString());
            return preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public <T> List<T> execute(IStatement statement,Class<T> cls){
        try {
            long start = System.currentTimeMillis();
            logger.info("Execute Select With Sql : "+statement.getSql());
            logger.info("Params : "+statement.getParams().toString());
            PreparedStatement preparedStatement = connection.prepareStatement(statement.getSql());
            setParams(preparedStatement,statement.getParams());
            ResultSet rs = preparedStatement.executeQuery();
            List<Map<String,Object>> result = fetchResultSet(rs);
            long end = System.currentTimeMillis();
            logger.info("Cost : " + (end-start)+"ms");
            return EntityUtil.resultSetToEntity(cls,result);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public <T> List<T> execute(String sql, Object ... values){
        return null;
    }

    @Override
    public <T> List<T> sqlQuery(Class<T> cls, String sql, Object... values) {
        return null;
    }

    @Override
    public <T> T getById(Class<T> cls, Serializable id) {
        Statement statement = Statement.createSelectStatement(cls,id);
        List<T> list = execute(statement,cls);
        if (list.isEmpty()){
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    public <T> List<T> getByIds(Class<T> cls, List<? extends Serializable> ids) {
        return null;
    }

    @Override
    public <T> int insert(T entity) {
        Statement statement = Statement.createInsertStatement(entity);
        if (execute(statement, statement.auto)) {
            return 1;
        }
        return 0;
    }


    static String createParameterPlaceHolder(int num) {
        return "(" + Stream.iterate("?", p -> p).limit(num).collect(Collectors.joining(",")) + ")";
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

    public static List<Map<String, Object>> fetchResultSet(ResultSet rs) {
        ResultSetMetaData meta;
        try {
            meta = rs.getMetaData();
            List<String> fields = new ArrayList<>();
            int count = 0;
            int bound = meta.getColumnCount();
            for (int i = 0; i < bound; i++) {
                fields.add(meta.getColumnName(i + 1));
            }
            List<Map<String, Object>> re = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> e = new HashMap<>();
                for (int i = 0; i < bound; i++) {
                    e.put(fields.get(i), rs.getObject(i + 1));
                }
                re.add(e);
            }
            return re;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
