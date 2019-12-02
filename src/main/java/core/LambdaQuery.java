package core;

import core.inerface.IDbConnection;
import core.inerface.ILambdaQuery;
import core.inerface.IStatement;
import core.support.SFunction;
import core.support.TableInfoCache;
import fj.P;
import fj.P3;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Leong
 */
public class LambdaQuery<T> implements ILambdaQuery<T> {

    private Class<T> cls;
    private IDbConnection db;
    private TableInfo tableInfo;
    private String selectSql = "";
    private String selects = "*";
    private List<IStatement> wheres = new ArrayList<>(16);
    private List<Object> prams = new ArrayList<>(16);

    public LambdaQuery(Class<T> cls, IDbConnection db, TableInfo tableInfo) {
        this.cls = cls;
        this.db = db;
        this.tableInfo = tableInfo;
        freshSql();
    }

    private void freshSql() {
        selectSql = "SELECT " + selects + " FROM " + tableInfo.getTableName();
    }

    @Override
    public ILambdaQuery<T> whereEq(SFunction<T, Object> column, Object value) {
        IStatement statement = new Statement();
        statement.setSql(TableInfoCache.convertToFieldName(column) + " = ?");
        statement.getParams().add(value);
        wheres.add(statement);
        return this;
    }

    @Override
    public ILambdaQuery<T> in(SFunction<T, Object> column, List<Object> values) {
        return null;
    }

    @Override
    public List<T> toList() {
        return db.genExecute(makeSql());
    }

    P3<Class<?>, String, List<Object>> makeSql() {
        if (!wheres.isEmpty()) {
            freshSql();
            this.selectSql = selectSql + " WHERE " + wheres.stream().map(IStatement::getSql).collect(Collectors.joining(","));
            prams.addAll(wheres.stream().flatMap(it -> it.getParams().stream()).collect(Collectors.toList()));
        }
        return P.p(cls, this.selectSql, this.prams);
    }

    @Override
    public T one() {
        return null;
    }
}
