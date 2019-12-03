package test;

import annotation.Column;
import annotation.Id;
import annotation.Table;
import lombok.Data;
import core.support.JdbcType;

@Data
@Table(value = "user",cache = true,expireTime = 600)
public class User {

    @Id("id")
    Long id;


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
