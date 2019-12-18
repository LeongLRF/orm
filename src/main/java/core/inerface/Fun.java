package core.inerface;

/**
 * @author Leong
 * sql 函数
 */
public interface Fun {

    /**
     * AVG函数 例如：select avg(xxx) from table where xxx
     *
     * @param column 字段
     * @return 平均值
     */
    Object avg(String column);

    /**
     * MAX 函数 例如：select max(xxx) from table where xxx
     *
     * @param column 字段
     * @return 最大值
     */
    Object max(String column);

    /**
     * MIN 函数 例如：select min(xxx) from table where xxx
     *
     * @param column 字段
     * @return 最小值
     */
    Object min(String column);
}
