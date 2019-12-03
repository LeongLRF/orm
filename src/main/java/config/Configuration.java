package config;

import lombok.Data;
import redis.clients.jedis.JedisPool;
import util.Model;

import javax.sql.DataSource;
/**
 * @author Leong
 * 连接配置类
 */
@Data
public class Configuration {

    public DataSource dataSource;

    public boolean showSql = false;

    public boolean showCost = false;

    public boolean debug = false;

    public boolean showResult = false;

    public int model = Model.POOL_MODEL;

    public boolean enableCache = true;

    public JedisPool jedisPool;

}
