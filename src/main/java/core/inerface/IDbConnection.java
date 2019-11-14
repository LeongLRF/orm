package core.inerface;

import annotation.Id;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

/**
 * @author Leong
 */
public interface IDbConnection {

    Connection getConnection();

    <T>ISelectQuery<T> form(Class<T> cls);

    ResultSet execute(ISelectQuery selectQuery);

    <T>List<T> sqlQuery(Class<T> cls,String sql,Object ...values);

    <T> T getById(Class<T> cls,Serializable id);

    <T>List<T> getByIds(Class<T> cls ,List<? extends Serializable> ids);


}
