package core;

import config.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.function.Function;

public class CachableDbConnection extends DbConnection {

    private final JedisPool jedisPool;

    public CachableDbConnection(DataSource dataSource, Configuration configuration,JedisPool jedisPool) throws SQLException {
        super(dataSource, configuration);
        this.jedisPool = jedisPool;
    }

    private String getKey(){
        return null;
    }

    private void op(Function<Jedis,?> op){
        try(Jedis j = jedisPool.getResource()){
            op.apply(j);
        }
    }
}
