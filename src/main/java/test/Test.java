package test;


import core.Statement;
import core.TableInfo;
import core.inerface.IStatement;
import util.EntityUtil;

import java.util.List;

public class Test {
    public static void main(String[] args) {
//        TableInfo<User> tableInfo = EntityUtil.getTableInfo(User.class);
//        IStatement statement = Statement.createInsertStatement(tableInfo);
//        System.out.println( statement.getSql());
        User user = new User();
        user.setName("123");
        user.setTrueName("123");
        user.setAge(22);
        List<Object> values = EntityUtil.getValues(user);
        System.out.println(values);
    }

    public static Class getClass(Object entity){
        return entity.getClass();
    }
}
