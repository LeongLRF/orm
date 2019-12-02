package core;

import config.Configuration;
import core.inerface.IDbConnection;
import core.inerface.ISelectQuery;
import core.inerface.IStatement;
import fj.P;
import fj.P3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.EntityUtil;
import util.Model;
import util.StringPool;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
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
    private Configuration configuration;


    public DbConnection(Connection connection, Configuration configuration) {
        this.connection = connection;
        this.configuration = configuration;
        logger.info("Init DbConnection with default model");
    }

    public DbConnection(Configuration configuration) {
        this.dataSource = configuration.dataSource;
        this.configuration = configuration;
    }

    public DbConnection(DataSource dataSource, Configuration configuration) throws SQLException {
        this.dataSource = dataSource;
        this.configuration = configuration;
        this.connection = dataSource.getConnection();
        logger.info("Init DbConnection With Configuration");
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


    private Object execute(IStatement statement, boolean isAuto) {
        return connectionOp((c, p) -> {
            try {
                int genFlag = isAuto ? java.sql.Statement.RETURN_GENERATED_KEYS : java.sql.Statement.NO_GENERATED_KEYS;
                p = statement.createPreparedStatement(c, genFlag);
                int row = p.executeUpdate();
                ResultSet rs = p.getGeneratedKeys();
                Object key = 0;
                if (rs.next()) {
                    key = rs.getObject(row);
                }
                return key;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        });
    }

    @SuppressWarnings("all")
    @Override
    public <T> List<T> genExecute(P3<Class<?>, String, List<Object>> p3) {
        long start = System.currentTimeMillis();
        return (List<T>) connectionOp((c, p) -> {
            try {
                p = c.prepareStatement(p3._2());
                setParams(p, p3._3());
                List<T> list = (List<T>) EntityUtil.resultSetToEntity(p3._1(), fetchResultSet(p.executeQuery()));
                logger.info("Execute SQL : " + p3._2());
                logger.info("Params : " + p3._3().toString());
                return list;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    private int executeUpdate(IStatement statement) {
        long start = System.currentTimeMillis();
        return (int) connectionOp((c, p) -> {
            try {
                p = statement.createPreparedStatement(c, null);
                return p.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                logger.info("Cost : " + (System.currentTimeMillis() - start) + "ms");
            }
            return 0;
        });

    }

    @Override
    public <T> List<T> sqlQuery(Class<T> cls, String sql, Object... values) {
        return genExecute(P.p(cls, sql, Arrays.asList(values)));
    }

    @Override
    public <T> T getById(Class<T> cls, Serializable id) {
        IStatement statement = Statement.createSelectStatement(cls, id);
        List<T> list = genExecute(makeSql(statement, cls));
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    public <T> List<T> getByIds(Class<T> cls, Collection<?> ids) {
        SelectQuery<T> selectQuery = new SelectQuery<>(this, cls);
        selectQuery.in(selectQuery.getTableInfo().getPrimaryKey().getName(), ids);
        return genExecute(selectQuery.makeSql());
    }

    @Override
    public <T> int insert(T entity) {
        long start = System.currentTimeMillis();
        IStatement statement = Statement.createInsertStatement(entity);
        Object index = execute(statement, statement.isAuto());
        EntityUtil.setId(entity, index);
        long end = System.currentTimeMillis();
        logger.info("Cost : " + (end - start) + "ms");
        return Integer.parseInt(index.toString());
    }

    @Override
    public <T> int insert(List<T> entities) {
        logger.info("open transaction");
        openTransaction(() -> {
            entities.forEach(this::insert);
            return null;
        });
        return entities.size();
    }

    @Override
    public void openTransaction(Supplier<?> f) {
        long start = System.currentTimeMillis();
        connectionOp((c, p) -> {
            try {
                logger.info("open transaction");
                c.setAutoCommit(onTransaction);
                f.get();
                c.commit();
                logger.info("commit cost: " + (System.currentTimeMillis() - start) + "ms");
            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    c.rollback();
                    logger.info("Transaction rollback");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return null;
        });
    }

    @Override
    public <T> void updateById(Class<T> cls, Serializable id, Consumer<T> updates) {
        IStatement statement = Statement.createUpdateStatement(cls, updates, id);
        executeUpdate(statement);
    }

    @Override
    public <T> int update(T entity) {
        Object id = EntityUtil.getId(entity);
        IStatement statement = Statement.createUpdateStatement(entity, id);
        return executeUpdate(statement);
    }

    @Override
    public <T> int update(List<T> entities) {
        if (entities.isEmpty()) {
            return 0;
        }
        openTransaction(() -> {
            entities.forEach(this::update);
            return null;
        });
        return entities.size();
    }

    @Override
    public <T> int deleteById(Class<T> cls, Serializable id) {
        IStatement statement = Statement.createDeleteStatement(cls, id);
        return executeUpdate(statement);
    }

    @Override
    public <T> int delete(T entity) {
        Serializable id = (Serializable) EntityUtil.getId(entity);
        return deleteById(entity.getClass(), id);
    }

    @Override
    public <T> int deleteByIds(Class<T> cls, List<Object> ids) {
        IStatement statement = Statement.createDeleteStatement(cls, ids);
        return executeUpdate(statement);
    }

    private static P3<Class<?>, String, List<Object>> makeSql(IStatement statement, Class<?> cls) {
        String sql = statement.getSql();
        List<Object> params = statement.getParams();
        return P.p(cls, sql, params);
    }




    static String createParameterPlaceHolder(int num) {
        return "(" + Stream.iterate("?", p -> p).limit(num).collect(Collectors.joining(",")) + ")";
    }

    static void setParams(PreparedStatement statement, List<Object> values) {
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

    private static List<Map<String, Object>> fetchResultSet(ResultSet rs) {
        ResultSetMetaData meta;
        try {
            meta = rs.getMetaData();
            List<String> fields = new ArrayList<>();
            int bound = meta.getColumnCount();
            for (int i = 0; i < bound; i++) {
                fields.add(meta.getColumnName(i + 1));
            }
            List<Map<String, Object>> re = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> e = new HashMap<>(16);
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

    private boolean debugModel(Configuration configuration) {
        return configuration != null && configuration.isDebug();
    }

    public static <T> T createEntity(Class<T> cls) {
        try {
            return cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object connectionOp(BiFunction<Connection, PreparedStatement, Object> action) {
        long start = System.currentTimeMillis();
        try (Connection connection1 = con();
             PreparedStatement preparedStatement = null) {
            return action.apply(connection1, preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            logger.info("Cost : " + (System.currentTimeMillis() - start) + "ms");
        }
        return null;
    }

    private Connection con() {
        if (configuration.model == Model.POOL_MODEL && dataSource != null) {
            try {
                return dataSource.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            return connection;
        }
        return null;
    }
}
