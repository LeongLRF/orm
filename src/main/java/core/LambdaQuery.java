package core;

import core.inerface.IDbConnection;
import core.inerface.ILambdaQuery;
import core.inerface.IStatement;
import core.support.SFunction;
import core.support.TableInfoCache;
import fj.P;
import fj.P3;

import java.util.ArrayList;
import java.util.Arrays;
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

    LambdaQuery(Class<T> cls, IDbConnection db, TableInfo tableInfo) {
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
    public ILambdaQuery<T> where(String sql, Object... values) {
        IStatement statement = new Statement();
        statement.setSql(sql);
        statement.setParams(Arrays.asList(values));
        wheres.add(statement);
        return this;
    }

    @Override
    public ILambdaQuery<T> in(SFunction<T, Object> column, List<Object> values) {
        IStatement statement = new Statement();
        statement.setSql(TableInfoCache.convertToFieldName(column) + " in " + DbConnection.createParameterPlaceHolder(values.size()));
        statement.getParams().addAll(values);
        wheres.add(statement);
        return this;
    }

    @Override
    public ILambdaQuery<T> between(SFunction<T, Object> column, Object value, Object value2) {
        IStatement statement = new Statement();
        statement.setSql(TableInfoCache.convertToFieldName(column) + " BETWEEN ? AND ?");
        statement.getParams().add(value);
        statement.getParams().add(value2);
        wheres.add(statement);
        return this;
    }

    @Override
    public List<T> toList() {
        return db.genExecute(makeSql());
    }

    private P3<Class<?>, String, List<Object>> makeSql() {
        if (!wheres.isEmpty()) {
            freshSql();
            this.selectSql = selectSql + " WHERE " + wheres.stream().map(IStatement::getSql).collect(Collectors.joining(","));
            prams.addAll(wheres.stream().flatMap(it -> it.getParams().stream()).collect(Collectors.toList()));
        }
        return P.p(cls, this.selectSql, this.prams);
    }

    @Override
    public Object avg(String column) {
        selects = " AVG(" + column + ") ";
        P3<Class<?>, String, List<Object>> p3 = makeSql();
        return db.normalQuery(p3._2(), p3._3().toArray());
    }

    @Override
    public Object max(String column) {
        selects = " MAX(" + column + ") ";
        P3<Class<?>, String, List<Object>> p3 = makeSql();
        return db.normalQuery(p3._2(), p3._3().toArray());
    }

    @Override
    public Object min(String column) {
        selects = " MIN(" + column + ") ";
        P3<Class<?>, String, List<Object>> p3 = makeSql();
        return db.normalQuery(p3._2(), p3._3().toArray());
    }

    @Override
    public Object sum(String column) {
        selects = " SUM(" + column + ") ";
        P3<Class<?>, String, List<Object>> p3 = makeSql();
        return db.normalQuery(p3._2(), p3._3().toArray());
    }
}
