package core;

import config.Configuration;
import core.inerface.DbFactory;
import core.inerface.IDbConnection;

import java.sql.SQLException;

public class DefaultDbFactory implements DbFactory {

    public static IDbConnection getDb(Configuration config) {
        if (config.jedisPool!=null) {
            try {
                return new CachedDbConnection(config);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            return new DbConnection(config);
        }
        return null;
    }
}
