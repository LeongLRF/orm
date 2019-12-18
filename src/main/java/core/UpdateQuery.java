package core;

import core.inerface.IDbConnection;
import core.inerface.IStatement;
import core.inerface.IUpdateQuery;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leong
 * 更新操作类具体实现
 */
@Data
public class UpdateQuery<T> implements IUpdateQuery<T> {

    public IDbConnection connection;
    public String updateSql;
    public List<Object> params = new ArrayList<>();
    public List<IStatement> wheres = new ArrayList<>();
    public Class<T> cls;
    public TableInfo tableInfo;

    @Override
    public IUpdateQuery<T> set(String column, Object newValue) {
        return null;
    }

    @Override
    public IUpdateQuery<T> where(String column, Object value) {
        return null;
    }

    @Override
    public int execute() {
        return 0;
    }
}
