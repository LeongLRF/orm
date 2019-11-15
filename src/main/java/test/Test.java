package test;


import core.DbConnection;
import core.Statement;
import core.TableInfo;
import core.inerface.IStatement;
import util.EntityUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        Connection connection = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://59.110.171.118:3306/test?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC";
            String username = "root";
            String password = "Liang45623+1628";
            connection = DriverManager.getConnection(url,username,password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        DbConnection db = new DbConnection(connection);
        User user = new User();

        List<User> users = db.form(User.class).in("id", Arrays.asList(1,2,3)).toList();
        System.out.println(users);
    }

    public static Class getClass(Object entity){
        return entity.getClass();
    }

    public static <T> int insert(T entity) {
        Class<?> cls = entity.getClass();
        TableInfo tableInfo = EntityUtil.getTableInfo(cls);
        Statement statement = Statement.createInsertStatement(entity);
        System.out.println(statement.getSql());
        return 0;
    }
}
