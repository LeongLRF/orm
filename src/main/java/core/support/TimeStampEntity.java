package core.support;

import java.sql.Timestamp;

public interface TimeStampEntity {

    void setInsertedAt(Timestamp insertedAt);

    Timestamp getInsertedAt();

    void setUpdatedAt(Timestamp updatedAtAt);

    Timestamp getUpdatedAt();
}
