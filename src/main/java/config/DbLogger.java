package config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Leong
 * @date 2019/11/15 19:55
 */
public class DbLogger {

    public static Logger getLogger(Class<?> cls){
        return LoggerFactory.getLogger(cls);
    }
}
