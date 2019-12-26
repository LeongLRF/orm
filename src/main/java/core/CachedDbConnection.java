package core;

import com.alibaba.fastjson.JSON;
import config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import util.EntityUtil;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Leong
 * 基于redis的二级缓存
 * key为tableName:id
 */
public final class CachedDbConnection extends DbConnection {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JedisPool jedisPool;

    public CachedDbConnection(Connection connection, Configuration config, JedisPool jedisPool) {
        super(connection, config);
        this.jedisPool = jedisPool;
    }
    public CachedDbConnection(Configuration configuration) throws SQLException {
        super(configuration.dataSource,configuration);
        this.jedisPool = configuration.jedisPool;
    }

    public CachedDbConnection(DataSource dataSource, Configuration configuration,JedisPool jedisPool) throws SQLException {
        super(dataSource, configuration);
        this.jedisPool = jedisPool;
    }

    @Override
    public <T> T getById(Class<T> cls, Serializable id) {
        return getCache(cls, id);
    }

    @Override
    public <T> List<T> getByIds(Class<T> cls, Collection<?> ids) {
        return getCaches(cls, ids);
    }

    @Override
    public <T> int insert(T entity) {
        int result = super.insert(entity);
        setValue(entity);
        return result;
    }

    @Override
    public <T> int insert(List<T> entities) {
        int result = super.insert(entities);
        setValue(entities);
        return result;
    }


    @Override
    public <T> void updateById(Class<T> cls, Serializable id, Consumer<T> updates) {
        del(cls, id);
        super.updateById(cls, id, updates);
    }

    @Override
    public <T> int update(T entity) {
        del(entity);
        return super.update(entity);
    }

    @Override
    public <T> int update(List<T> entities) {
        del(entities);
        return super.update(entities);
    }

    @Override
    public <T> int deleteById(Class<T> cls, Serializable id) {
        del(cls, id);
        return super.deleteById(cls, id);
    }

    @Override
    public <T> int delete(T entity) {
        del(entity);
        return super.delete(entity);
    }

    @Override
    public <T> int deleteByIds(Class<T> cls, List<Object> ids) {
        del(cls, ids);
        return super.deleteByIds(cls, ids);
    }

    private <T> T getCache(Class<T> cls, Object key) {
        long start = System.currentTimeMillis();
        TableInfo tableInfo = EntityUtil.getTableInfo(cls);
        if (!tableInfo.isCache()) {
            return super.getById(cls, (Serializable) key);
        }
        return op(j -> {
            String value = j.get(redisKey(prefix(tableInfo), key));
            if (value != null) {
                logger.info("get data from redis,cost :" + (System.currentTimeMillis() - start) + "ms");
                return JSON.parseObject(value, cls);
            } else {
                T t = super.getById(cls, (Serializable) key);
                setValue(JSON.toJSONString(t), redisKey(prefix(tableInfo), key), tableInfo.getExpireTime());
                return t;
            }

        });
    }

    private <T> List<T> getCaches(Class<T> cls, Collection<?> keys) {
        TableInfo tableInfo = EntityUtil.getTableInfo(cls);
        if (!tableInfo.isCache()) {
            return super.getByIds(cls, keys);
        }
        return keys.stream().map(it -> getCache(cls, it)).collect(Collectors.toList());
    }

    private String redisKey(String prefix, Object key) {
        return prefix + ":" + key;
    }

    private String[] redisKeys(TableInfo tableInfo, List<Object> keys) {
        String prefix = prefix(tableInfo);
        return keys.stream().map(it -> redisKey(prefix, it)).toArray(String[]::new);
    }

    private String prefix(TableInfo tableInfo) {
        return tableInfo.getTableName();
    }

    private void setValue(String value, String key, int seconds) {
        op(j -> j.setex(key, seconds, value));
    }

    private <T> void setValue(T entity) {
        Class<?> cls = entity.getClass();
        TableInfo tableInfo = EntityUtil.getTableInfo(cls);
        if (tableInfo.isCache()) {
            String key = redisKey(prefix(tableInfo), EntityUtil.getId(entity));
            setValue(JSON.toJSONString(entity), key, tableInfo.getExpireTime());
        }
    }

    private <T> void setValue(List<T> entities) {
        entities.forEach(this::setValue);
    }

    private <T> void del(T entity) {
        Class<?> cls = entity.getClass();
        Object id = EntityUtil.getId(entity);
        del(cls, (Serializable) id);
    }

    private <T> void del(Class<T> cls, Serializable id) {
        TableInfo tableInfo = EntityUtil.getTableInfo(cls);
        String key = redisKey(prefix(tableInfo), id);
        del(key);
    }

    private <T> void del(Class<T> cls, List<Object> ids) {
        ids.forEach(it -> del(cls, (Serializable) it));
    }

    private <T> void del(List<T> entities) {
        entities.forEach(this::del);
    }

    private void del(String key) {
        op(j -> j.del(key));
    }

    private <T> T op(Function<Jedis, T> op) {
        try (Jedis j = jedisPool.getResource()) {
            return op.apply(j);
        }
    }
}
