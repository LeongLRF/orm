package core;

public interface SqlQuery<T> {

    int insert(T entity);
    int update(T entity);
}
