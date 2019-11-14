package config;

import lombok.Data;

import javax.sql.DataSource;
/**
 * @author Leong
 * 连接配置类
 */
@Data
public class Configuration {

    public DataSource dataSource;
}
