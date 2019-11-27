package test;


import config.Configuration;
import core.CachedDbConnection;
import core.inerface.IFilter;
import core.inerface.ISelectQuery;
import redis.clients.jedis.JedisPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Test {
    public static void main(String[] args) {
        Connection connection = null;
        JedisPool jedisPool = new JedisPool("localhost",6379);
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://59.110.171.118:3306/test?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC";
            String username = "root";
            String password = "Liang45623+1628";
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        Configuration config = new Configuration() {{
            debug = true;
        }};
        CachedDbConnection db = new CachedDbConnection(connection,config,jedisPool);
        User users = db.getById(User.class,21);
        System.out.println(users);
    }

    static class Filter implements IFilter<User> {

        @Override
        public ISelectQuery<User> apply(ISelectQuery<User> q) {
            return q.whereEq("name", "Leong")
                    .whereEq("trueName", "梁荣锋");
        }
    }
}
