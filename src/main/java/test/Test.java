package test;


import config.Configuration;
import core.DbConnection;
import core.Statement;
import core.TableInfo;
import core.inerface.IFilter;
import core.inerface.ISelectQuery;
import core.inerface.IStatement;
import util.EntityUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        Connection connection = null;

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
        DbConnection db = new DbConnection(connection, config);
        UserTest userTest = new UserTest();
        db.insert(userTest);

    }

    static class Filter implements IFilter<User> {

        @Override
        public ISelectQuery<User> apply(ISelectQuery<User> q) {
            return q.whereEq("name", "Leong")
                    .whereEq("trueName", "梁荣锋");
        }
    }
}
