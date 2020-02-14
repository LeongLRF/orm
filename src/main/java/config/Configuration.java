package config;

import core.DefaultDbFactory;
import core.inerface.IDbConnection;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPool;
import util.Model;

import javax.sql.DataSource;

/**
 * @author Leong
 * 连接配置类
 */
@Data
@Slf4j
public class Configuration {

    public Configuration() {
        log.info("Init SimpleOrm");
    }

    public DataSource dataSource;

    public boolean showSql = false;

    public boolean showCost = false;

    public boolean debug = false;

    public boolean showResult = false;

    public int model = Model.POOL_MODEL;

    public boolean enableCache = true;


}
