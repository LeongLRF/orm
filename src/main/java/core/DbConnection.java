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

import javax.sql.DataSource;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
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
    private Map<String,List<Object>> cache;

    public DbConnection(Connection connection) {
        this.connection = connection;
        logger.info("init DbConnection with default model");
    }
    public DbConnection(Connection connection,Configuration configuration) {
        this.connection = connection;
        this.configuration = configuration;
        if (configuration.enableCache){
            cache = new HashMap<>(50);
        }
        logger.info("init DbConnection with default model");
    }

    public DbConnection(DataSource dataSource) throws SQLException {
        this.dataSource = dataSource;
        this.connection = dataSource.getConnection();
        logger.info("init DbConnection with DataSource : " + dataSource.getClass().getName());
    }

    public DbConnection(DataSource dataSource,Configuration configuration) throws SQLException {
        this.dataSource = dataSource;
        this.configuration = configuration;
        this.connection = dataSource.getConnection();
        if (configuration.enableCache){
            cache = new HashMap<>(50);
        }
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
        int genflag = java.sql.Statement.NO_GENERATED_KEYS;
        if (isAuto) {
            genflag = java.sql.Statement.RETURN_GENERATED_KEYS;
        }
        PreparedStatement preparedStatement;
        if (debugModel(configuration)){
            logger.info("Execute sql : " + statement.getSql());
            logger.info("Params : " + statement.getParams().toString());
        }
        try {
            preparedStatement = connection.prepareStatement(statement.getSql(), genflag);
            setParams(preparedStatement, statement.getParams());
            int row = preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            Object key = 0;
            if (rs.next()){
                key = rs.getObject(row);
            }
            return key;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    @SuppressWarnings("all")
    @Override
    public <T> List<T> gen_execute(P3<Class<T>, String, List<Object>> p3){
        if (cache!=null&&cache.get(p3._2())!=null){
            logger.info("get data from cache");
            long start = System.currentTimeMillis();
            List<T> list = (List<T>) cache.get(p3._2());
            long end = System.currentTimeMillis();
            logger.info("Cost : "+(end-start)+"ms");
            return list;
        }
        PreparedStatement preparedStatement = null;
        try {
            long start = System.currentTimeMillis();
            if (configuration.model== Model.POOL_MODEL){
                connection = dataSource.getConnection();
            }
             preparedStatement = connection.prepareStatement(p3._2());
            setParams(preparedStatement,p3._3());
            List<Map<String,Object>> result = fetchResultSet(preparedStatement.executeQuery());
            List<T> list = EntityUtil.resultSetToEntity(p3._1(),result);
            if (cache!=null) {
                cache.put(p3._2(), (List<Object>) list);
            }
            long end = System.currentTimeMillis();
            logger.info("Execute SQL : "+ p3._2());
            logger.info("Params : "+p3._3().toString());
            logger.info("Cost : "+(end - start)+"ms");
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            release(connection,preparedStatement);
        }
        return null;
    }

    private void release(Connection connection, PreparedStatement preparedStatement){
        if (preparedStatement!=null){
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            preparedStatement = null;
        }
        if (connection!=null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            connection = null;
        }
    }

    @Override
    public <T> List<T> sqlQuery(Class<T> cls, String sql, Object... values) {
        return gen_execute(P.p(cls,sql,Arrays.asList(values)));
    }

    @Override
    public <T> T getById(Class<T> cls, Serializable id) {
        Statement statement = Statement.createSelectStatement(cls,id);
        List<T> list = gen_execute(makeSql(statement,cls));
        if (list.isEmpty()){
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    public <T> List<T> getByIds(Class<T> cls, List<Object> ids) {
        SelectQuery<T> selectQuery = new SelectQuery<>(this,cls);
        selectQuery.in(selectQuery.getTableInfo().getPrimaryKey().getName(),ids);
        return gen_execute(makeSql(selectQuery));
    }

    @Override
    public <T> int insert(T entity) {
        long start = System.currentTimeMillis();
        Statement statement = Statement.createInsertStatement(entity);
        Object index = execute(statement, statement.auto);
        if (statement.auto) {
            EntityUtil.setId(entity,index);
        }
        long end = System.currentTimeMillis();
        logger.info("Cost : "+(end-start)+"ms");
        String s = index.toString();
        return Integer.valueOf(s);
    }

    @Override
    public <T> int insert(List<T> entities) {
        logger.info("open transaction");
        openTransaction((connection) -> entities.forEach(this::insert));
        return 0;
    }

    @Override
    public void openTransaction(Consumer<Connection> f) {
        try {
            long start = System.currentTimeMillis();
            connection.setAutoCommit(onTransaction);
            f.accept(connection);
            logger.info("commit");
            connection.commit();
            long end = System.currentTimeMillis();
            logger.info("Cost : " + (end -start)+"ms");
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public <T> void updateById(Class<T> cls, Serializable id, Consumer<T> updates) {

    }

    private static <T> P3<Class<T>,String,List<Object>> makeSql(IStatement statement, Class<T> cls){
        String sql = statement.getSql();
        List<Object> params = statement.getParams();
        return P.p(cls,sql,params);
    }

    static <T> P3<Class<T>,String,List<Object>> makeSql(ISelectQuery<T> selectQuery){
        selectQuery.makeSql(selectQuery.getWheres());
        String sql = selectQuery.getSql();
        List<Object> params = selectQuery.getParams();
        Class<T> cls = selectQuery.getCls();
        return P.p(cls,sql,params);
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
    private boolean debugModel(Configuration configuration){
        return configuration!=null && configuration.isDebug();
    }
    public static <T> T createEntity(Class<T> cls) {
        try {
            return cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
