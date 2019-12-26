package exception;

import lombok.experimental.UtilityClass;
import util.EntityUtil;

@UtilityClass
public class ExceptionHelper {

    public static void throwExpection(boolean test,String message){
        if (!test){
            throw new SimpleOrmExpection(message);
        }
    }
}
