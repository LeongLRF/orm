package core.support;

import java.sql.Timestamp;

/**
 * @author Leong
 * 实现改接口的类 插入到数据库时会自动给insertedAt赋值 更新时会
 * 自动给 updatedAt赋值
 */
public interface TimeStampEntity {

    void setInsertedAt(Timestamp insertedAt);

    Timestamp getInsertedAt();

    void setUpdatedAt(Timestamp updatedAtAt);

    Timestamp getUpdatedAt();
}
