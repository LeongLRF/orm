package core.support;

import java.sql.Timestamp;

public interface TimeStampEntity {

    void setInsertedAt(Timestamp insertedAt);

    Timestamp getInsertedAt();
}
