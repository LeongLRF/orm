package core;

import java.sql.Connection;

/**
 * @author Leong
 */
public interface IDbConnection {

    Connection getConnection();

    <T>ISelectQuery<T> form(Class<T> cls);
}
