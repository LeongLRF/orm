package core.inerface;

import java.util.List;

/**
 * @author Leong
 * 更新操作类
 */
public interface IUpdateQuery<T> {

    IDbConnection getConnection();

    String getUpdateSql();

    List<Object> getParams();

    List<IStatement> getWheres();

    Class<T> getCls();

    IUpdateQuery<T> set(String column, Object newValue);

    IUpdateQuery<T> where(String column, Object value);

    int execute();
}
