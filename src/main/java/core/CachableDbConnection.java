package core;

import annotation.Table;
import com.alibaba.fastjson.JSON;
import config.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import util.EntityUtil;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CachableDbConnection extends DbConnection {

    private final JedisPool jedisPool;

    public CachableDbConnection(DataSource dataSource, Configuration configuration, JedisPool jedisPool) throws SQLException {
        super(dataSource, configuration);
        this.jedisPool = jedisPool;
    }

    @Override
    public <T> T getById(Class<T> cls, Serializable id) {
        return super.getById(cls, id);
    }

    @Override
    public <T> List<T> getByIds(Class<T> cls, List<Object> ids) {
        return super.getByIds(cls, ids);
    }

    @Override
    public <T> int insert(T entity) {
        return super.insert(entity);
    }

    @Override
    public <T> int insert(List<T> entities) {
        return super.insert(entities);
    }

    @Override
    public void openTransaction(Supplier<?> f) {
        super.openTransaction(f);
    }

    @Override
    public <T> void updateById(Class<T> cls, Serializable id, Consumer<T> updates) {
        super.updateById(cls, id, updates);
    }

    @Override
    public <T> int update(T entity) {
        return super.update(entity);
    }

    @Override
    public <T> int update(List<T> entities) {
        return super.update(entities);
    }

    @Override
    public <T> int deleteById(Class<T> cls, Serializable id) {
        return super.deleteById(cls, id);
    }

    @Override
    public <T> int delete(T entity) {
        return super.delete(entity);
    }

    @Override
    public <T> int deleteByIds(Class<T> cls, List<Object> ids) {
        return super.deleteByIds(cls, ids);
    }

    private <T> T getCache(Class<T> cls, Object key) {
        TableInfo tableInfo = EntityUtil.getTableInfo(cls);
        if (!tableInfo.isCache()) {
            return super.getById(cls, (Serializable) key);
        }
        return op(j -> {
            String value = j.get(redisKey(prefix(tableInfo), key));
            return JSON.parseObject(value, cls);
        });
    }

    private <T> List<T> getCaches(Class<T> cls, List<Object> keys) {
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

    private void setValue(String value, String key,int seconds) {
        op(j -> j.setex(key,seconds,value));
    }

    private <T> T op(Function<Jedis, T> op) {
        try (Jedis j = jedisPool.getResource()) {
            return op.apply(j);
        }
    }
}
