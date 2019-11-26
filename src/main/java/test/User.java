package test;

import annotation.Column;
import annotation.Id;
import annotation.Table;
import lombok.Data;
import util.JdbcType;

@Data
@Table(value = "user",cache = true,expireTime = 600)
public class User {

    @Id("id")
    Long id;


    @Column("name")
    String name;

    @Column("trueName")
    String trueName;

    @Column("age")
    Integer age;

    @Column(value = "user", jdbcType = JdbcType.JSON)
    User user;

    String faker;

}
