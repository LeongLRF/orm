package core;

/**
 * @author Leong
 */
public interface IDbConnection {

    <T>ISelectQuery form(Class<T> cls);
}
