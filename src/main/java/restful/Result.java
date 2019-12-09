package restful;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Leong
 * RestFul API
 */
@SuppressWarnings("all")
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {

    public boolean success;
    public T data;
    public String code;
    public String message;

    public static <T> Result<T> ok(String message, T data) {
        return new Result<>(true, data, "", message);
    }

    public static <T> Result<T> ok() {
        return ok("ok", null);
    }

    public static <T> Result<T> ok(T data) {
        return ok("ok", data);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(false, null, "9999", message);
    }

    public static <T> Result<T> error(String message, String code) {
        return new Result<>(false, null, code, message);
    }

    public static <T> Result<T> maybe(T data, String errorMsg) {
        if (data == null) {
            return error(errorMsg);
        } else {
            return ok(data);
        }
    }

    public static <T> Result<T> maybe(T data) {
        return maybe(data, "数据不存在");
    }

    public static <T> Result<T> maybe(Optional<T> data, String errorMsg) {
        return data.map(Result::ok).orElseGet(() -> error(errorMsg));
    }

    public <R> Result<R> andThen(Function<T, Result<R>> f) {
        if (success) {
            return f.apply(data);
        } else {
            return error(message, code);
        }
    }

    public <R> Result<R> andThenMaybe(Function<T, R> f) {
        return andThen(p -> maybe(f.apply(p)));
    }

    public Result<T> andThenCheck(Predicate<T> f, String errorMsg) {
        if (success) {
            if (f.test(this.data)) {
                return ok(data);
            } else {
                return error(errorMsg);
            }
        } else {
            return this;
        }
    }

    public Result<T> andThenCheck(boolean test, String errorMsg) {
        if (success) {
            if (test) {
                return ok(data);
            } else {
                return error(errorMsg);
            }
        } else {
            return this;
        }
    }

    public <R> Result<T> mapSelf(Function<T, R> f) {
        if (success) {
            f.apply(data);
            return this;
        } else {
            return error(message, code);
        }
    }

    public Result<T> accept(Consumer<T> f) {
        if (success) {
            f.accept(data);
            return this;
        } else {
            return error(message, code);
        }

    }
}
