package core.inerface;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

/**
 * @author Leong
 */
public interface IStatement {

    /**
     * 获取sql片段
     * @return sql片段
     */
    String getSql();

    void setSql(String sql);
    /**
     * 获取sql片段参数
     * @return params
     */
    List<Object> getParams();

    void setParams(List<Object> params);

    /**
     * 是否自增
     * @return 是否自增
     */
    boolean isAuto();

    /**
     * 创建 preparedStatement
     * @param connection 数据库连接
     * @param flag 是否返回自增数据
     * @return preparedStatement
     */
    PreparedStatement createPreparedStatement(Connection connection,Integer flag);

}
