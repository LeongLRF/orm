package core;

import core.inerface.IDbConnection;
import core.inerface.IStatement;
import core.inerface.IUpdateQuery;
import lombok.Data;
import util.EntityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Leong
 * 更新操作类具体实现
 */
@Data
public class UpdateQuery<T> implements IUpdateQuery<T> {

    public IDbConnection connection;
    public String updateSql;
    public List<Object> params = new ArrayList<>();
    public List<IStatement> set = new ArrayList<>();
    public List<IStatement> wheres = new ArrayList<>();
    public Class<T> cls;
    public TableInfo tableInfo;

    public UpdateQuery(IDbConnection connection, Class<T> cls) {
        this.connection = connection;
        this.cls = cls;
        this.tableInfo = EntityUtil.getTableInfo(cls);
        this.updateSql = "UPDATE " + tableInfo.getTableName();
    }

    @Override
    public IUpdateQuery<T> set(String column, Object newValue) {
        IStatement statement = new Statement();
        statement.setSql(column + " = ?");
        statement.getParams().add(newValue);
        this.set.add(statement);
        return this;
    }

    @Override
    public IUpdateQuery<T> where(String column, Object value) {
        IStatement statement = new Statement();
        statement.setSql(column + " = ?");
        statement.getParams().add(value);
        wheres.add(statement);
        return this;
    }

    @Override
    public int execute() {
        makeSql();
        return (int) connection.executeUpdate(this.updateSql, params.toArray());
    }

    @Override
    public void makeSql() {
        String where;
        String set;
        if (!this.set.isEmpty()) {
            set = " SET " + this.set.stream().map(IStatement::getSql).collect(Collectors.joining(","));
            params.addAll(this.set.stream().flatMap(it -> it.getParams().stream()).collect(Collectors.toList()));
        } else {
            throw new IllegalArgumentException("请输入更新参数");
        }
        if (this.wheres.isEmpty()) {
            throw new RuntimeException("不支持全表更新");
        } else {
            where = " WHERE " + this.wheres.stream().map(IStatement::getSql).collect(Collectors.joining(" AND"));
            params.addAll(this.wheres.stream().flatMap(it -> it.getParams().stream()).collect(Collectors.toList()));
        }
        this.updateSql = this.updateSql + set + where;
    }

}
