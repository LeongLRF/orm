package test;


import core.DbConnection;
import core.Statement;
import core.TableInfo;
import core.inerface.IStatement;
import util.EntityUtil;

import java.util.List;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {

    }

    public static Class getClass(Object entity){
        return entity.getClass();
    }

    public static <T> int insert(T entity) {
        Class<?> cls = entity.getClass();
        TableInfo tableInfo = EntityUtil.getTableInfo(cls);
        Statement statement = Statement.createInsertStatement(tableInfo,entity);
        System.out.println(statement.getSql());
        return 0;
    }
}
