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

    public int model = Model.DEFAULT_MODEL;

    public boolean enableCache = true;

    public JedisPool jedisPool;

    public static class Bulider{
        private Configuration configuration = new Configuration();

        public Bulider showSql(boolean showSql){
            configuration.setShowSql(showSql);
            return this;
        }
        public Bulider dataSource(DataSource dataSource){
            configuration.setDataSource(dataSource);
            return this;
        }
        public Bulider cost(boolean cost){
            configuration.setShowCost(cost);
            return this;
        }
        public Bulider debug(boolean debug){
            configuration.setDebug(debug);
            return this;
        }
        public Bulider jedisPool(JedisPool jedisPool){
            configuration.setJedisPool(jedisPool);
            return this;
        }
        public Configuration bulid(){
            return configuration;
        }

    }
}
