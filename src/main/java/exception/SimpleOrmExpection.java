package exception;

/**
 * @author Leong
 * 专用异常
 */
public class SimpleOrmExpection extends RuntimeException {

    public SimpleOrmExpection(String message) {
        super(message);
    }
}
