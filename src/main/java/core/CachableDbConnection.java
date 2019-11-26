package core;

import com.alibaba.fastjson.JSON;
import config.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import util.EntityUtil;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CachableDbConnection extends DbConnection {

    private final JedisPool jedisPool;

    public CachableDbConnection(DataSource dataSource, Configuration configuration, JedisPool jedisPool) throws SQLException {
        super(dataSource, configuration);
        this.jedisPool = jedisPool;
    }

    @Override
    public <T> T getById(Class<T> cls, Serializable id) {
        return getCache(cls, id);
    }

    @Override
    public <T> List<T> getByIds(Class<T> cls, List<Object> ids) {
        return getCaches(cls, ids);
    }

    @Override
    public <T> int insert(T entity) {
        setValue(entity);
        return super.insert(entity);
    }

    @Override
    public <T> int insert(List<T> entities) {
        setValue(entities);
        return super.insert(entities);
    }


    @Override
    public <T> void updateById(Class<T> cls, Serializable id, Consumer<T> updates) {
        del(cls,id);
        super.updateById(cls, id, updates);
    }

    @Override
    public <T> int update(T entity) {
        del(entity);
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

    private void setValue(String value, String key, int seconds) {
        op(j -> j.setex(key, seconds, value));
    }

    private <T> void setValue(T entity) {
        Class<?> cls = entity.getClass();
        TableInfo tableInfo = EntityUtil.getTableInfo(cls);
        String key = redisKey(prefix(tableInfo), EntityUtil.getId(entity));
        setValue(JSON.toJSONString(entity), key, tableInfo.getExpireTime());
    }

    private <T> void setValue(List<T> entities) {
        entities.forEach(this::setValue);
    }
    private <T> void del(T entity){
        Class<?> cls = entity.getClass();
        Object id = EntityUtil.getId(entity);
        del(cls, (Serializable) id);
    }

    private <T> void del(Class<T> cls, Serializable id) {
        TableInfo tableInfo = EntityUtil.getTableInfo(cls);
        String key = redisKey(prefix(tableInfo), id);
        del(key);
    }
    private <T> void del(List<T> entities){

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
