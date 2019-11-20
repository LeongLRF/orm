package util;

import config.Configuration;
import org.slf4j.Logger;

public class DbLogger {

    public Configuration configuration;
    public Class<?> cls;
    Logger logger;

    public DbLogger(Configuration configuration,Class<?> cls){
        this.configuration = configuration;
        this.cls = cls;
    }
}
