package core.inerface;

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

    /**
     * 获取sql片段参数
     * @return params
     */
    List<Object> getParams();

    /**
     * 是否自增
     * @return 是否自增
     */
    boolean isAuto();

}
