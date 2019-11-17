package config;

import lombok.Data;
import util.Model;

import javax.sql.DataSource;
/**
 * @author Leong
 * 连接配置类
 */
@Data
public class Configuration {

    public DataSource dataSource;

    public boolean showSql = false;

    public boolean showCost = false;

    public boolean debug = false;

    public int model = Model.DEFAULT_MODEL;

    public boolean enableCache = true;
}
