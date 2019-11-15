package core.inerface;

import annotation.Id;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

/**
 * @author Leong
 */
public interface IDbConnection {

    Connection getConnection();

    DataSource getDataSource();

    <T>ISelectQuery<T> form(Class<T> cls);

    <T> List<T> execute(ISelectQuery<T> selectQuery);

    <T>List<T> sqlQuery(Class<T> cls,String sql,Object ...values);

    <T> T getById(Class<T> cls,Serializable id);

    <T>List<T> getByIds(Class<T> cls ,List<? extends Serializable> ids);

    <T>int insert(T entity);

}
