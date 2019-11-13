package test;


import core.TableInfo;
import util.EntityUtil;

public class Test {
    public static void main(String[] args) {
        TableInfo<User> tableInfo = EntityUtil.getTableInfo(User.class);
        System.out.println(tableInfo);
    }
}
