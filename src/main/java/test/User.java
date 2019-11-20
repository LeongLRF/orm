package test;

import annotation.Column;
import annotation.Id;
import annotation.Table;
import lombok.Data;
import util.JdbcType;

@Data
@Table(name = "user")
public class User {

    @Id("id")
    Long id;


    @Column(name = "name")
    String name;

    @Column(name = "trueName")
    String trueName;

    @Column(name = "age")
    Integer age;

    @Column(name = "user", jdbcType = JdbcType.JSON)
    User user;

    String faker;

}
