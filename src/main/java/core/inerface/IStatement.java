package core.inerface;

import com.sun.xml.internal.ws.developer.Serialization;

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
     */
    PreparedStatement createPreparedStatement(Connection connection,Integer flag);

}
