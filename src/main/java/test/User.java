package test;

import annotation.Column;
import annotation.Id;
import annotation.Table;
import core.support.TimeStampEntity;
import lombok.Data;
import core.support.JdbcType;

import java.sql.Timestamp;

@Data
@Table(value = "user", cache = true, expireTime = 600)
public class User implements TimeStampEntity {

    @Id("id")
    Long id;

    @Column("inserted_at")
    Timestamp insertedAt;

    @Column("name")
    String name;

    @Column("true_name")
    String trueName;

    @Column("age")
    Integer age;

    @Column(value = "user", jdbcType = JdbcType.JSON)
    User user;

    String faker;

}
