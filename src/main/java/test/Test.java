package test;


import core.DbConnection;
import core.Statement;
import core.TableInfo;
import core.inerface.IStatement;
import util.EntityUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        Connection connection = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC";
            String username = "root";
            String password = "123456";
            connection = DriverManager.getConnection(url,username,password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        DbConnection db = new DbConnection(connection);
        User user = new User();
        user.setTrueName("555");
        db.insert(user);
        System.out.println(user);



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
