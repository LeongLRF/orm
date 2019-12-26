package core.support;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Leong 分页
 */
public class Page<T> {
    private final long total;
    private final int pageNum;
    private final int pageSize;
    public final List<T> data;

    public Page(long total, int pageNum, int pageSize, List<T> data) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.data = data;
    }

    /**
     * 可以通过mapList关联其他表
     *
     * @param action 关联操作
     */
    public Page<T> mapList(Consumer<List<T>> action) {
        action.accept(data);
        return this;
    }
}
