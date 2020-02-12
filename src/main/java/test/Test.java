package test;


import config.Configuration;
import core.CachedDbConnection;
import core.DbConnection;
import core.inerface.IDbConnection;
import redis.clients.jedis.JedisPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Test {
    public static void main(String[] args) {
        Connection connection = null;
        JedisPool jedisPool = new JedisPool("59.110.171.118", 6379);
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
        IDbConnection db = new DbConnection(connection, config);
        User user = new User();
        user.setTrueName("fengfeng");
        long start = System.currentTimeMillis();
        db.form(User.class).toList();
        System.out.println(System.currentTimeMillis() - start + "ms");
    }
}
