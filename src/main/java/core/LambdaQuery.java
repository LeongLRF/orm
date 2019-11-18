package core;

import core.inerface.ILambdaQuery;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
/**
 * @author Leong
 */
public class LambdaQuery<T> implements ILambdaQuery<T> {

    private SelectQuery<T> selectQuery;
    TableInfo tableInfo;
    Map<String,ColumnInfo> map;

    public LambdaQuery(SelectQuery<T> selectQuery) {
        this.selectQuery = selectQuery;
        this.tableInfo = this.selectQuery.getTableInfo();
        this.map = this.tableInfo.getColumns();
    }

    @Override
    public ILambdaQuery<T> whereEq(Function<T, Object> column, Object value) {
        return selectQuery.whereEq(map.get(column).getName(),value);
    }
    @Override
    public ILambdaQuery<T> in(Function<T, Object> column, List<Object> values) {
        return null;
    }

    @Override
    public List<T> toList() {
        return null;
    }

    @Override
    public T one() {
        return null;
    }
}
