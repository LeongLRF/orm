package test;

import annotation.Column;
import annotation.Id;
import annotation.Table;
import lombok.Data;

@Data
@Table(name ="user")
public class User {

    @Id("id")
    long id;

    @Column(name = "name")
    String name;

    @Column(name = "trueName")
    String trueName;
}
