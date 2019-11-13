package core;

import com.sun.istack.internal.Nullable;

import java.io.Serializable;
import java.util.List;

/**
 * @author Leong
 */
public interface ISelectQuery<T> {


    String getSql();

    Class<T> getCls();

    ISelectQuery<T> where(String column, Object value);

    ISelectQuery<T> in(String column, List<? extends Serializable> ids);

    ISelectQuery<T> whereEq(String column, Object value);

    ISelectQuery<T> inSql(String column,String sql,@Nullable Object ...values);

    List<T> toList();

    T one();
}
