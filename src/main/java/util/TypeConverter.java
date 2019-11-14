package util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TypeConverter {
    // int, double, float, boolean,
    //Integer, Double, Float, Boolean
    // String, BigDecimal, BigInteger, Timestamp, Date

    // types = words $ filter(/=',') "Integer, Float, Long, String, Boolean, Double, Timestamp, Date, BigDecimal, BigInteger"
    // Prelude Control.Monad Data.Function> liftM2 (,) types types & filter (uncurry (/=)) & filter (\(_, x) -> x /= "String") & map (\(a, b) -> "case \""++a++"_"++b++"\": return (("++a++")object);") & unwords & putStrLn
    public enum Converters {

    }

    public static boolean isAllowType(Class cls) {
        return allowTypes.contains(cls.getSimpleName());
    }

    public final static Set<String> allowTypes = Stream.concat(Stream.of(Integer.class, Double.class, Float.class, Boolean.class, Long.class,
            String.class, BigDecimal.class, Timestamp.class, Date.class).map(Class::getSimpleName),
            Stream.of("int", "double", "float", "boolean", "long"))
            .collect(Collectors.toSet());

    public static <T> T decode(Object object, TypeReference<T> to, String dbType) {
        if ("json".equals(dbType)) {
            if (object instanceof String) {
                return JSON.parseObject((String) object, to);
            } else {
                return (T) object;
            }
        } else {
            return (T) convert(object, (Class) to.getType());
        }
    }

    public static Object encode(Object object, String dbType) {
        if ("json".equals(dbType)) {
            return JSON.toJSONString(object);
        } else {
            return object;
        }
    }

    public static Object convert(Object object, Class to) {
        if (object == null) {
            return null;
        }
        to = toBoxedClass(to);
        String fromName = object.getClass().getSimpleName(), toName = to.getSimpleName();
        if (fromName.equals(toName)) {
            return object;
        }
        if (toName.equals("String")) {
            return object.toString();
        }
        switch (fromName + "_" + toName) {
            case "Integer_Float":
                return ((Integer) object).floatValue();
            case "Integer_Long":
                return ((Integer) object).longValue();
            case "Integer_Boolean":
                return !object.equals(0);
            case "Integer_Double":
                return ((Integer) object).doubleValue();
            case "Integer_BigDecimal":
                return new BigDecimal((Integer) object);
            case "Float_Integer":
                return ((Float) object).intValue();
            case "Float_Long":
                return ((Float) object).longValue();
            case "Float_Double":
                return ((Float) object).doubleValue();
            case "Float_BigDecimal":
                return new BigDecimal((Float) object);
            case "Long_Integer":
                return ((Long) object).intValue();
            case "Long_Float":
                return ((Long) object).floatValue();
            case "Long_Boolean":
                return !object.equals(0L);
            case "Long_Double":
                return ((Long) object).doubleValue();
            case "Long_BigDecimal":
                return new BigDecimal((Long) object);
            case "String_Integer":
                return Integer.parseInt((String) object);
            case "String_Float":
                return Float.parseFloat((String) object);
            case "String_Long":
                return Long.parseLong((String) object);
            case "String_Boolean":
                return Boolean.parseBoolean((String) object);
            case "String_Double":
                return Double.parseDouble((String) object);
            case "String_Timestamp":
                return parseTime((String) object);
            case "String_Date":
                return parseDate((String) object);
            case "String_BigDecimal":
                return new BigDecimal((String) object);
            case "Boolean_Integer":
                return ((Boolean) object) ? 1 : 0;
            case "Boolean_Float":
                return ((Boolean) object) ? 1f : 0f;
            case "Boolean_Long":
                return ((Boolean) object) ? 1L : 0L;
            case "Boolean_Double":
                return ((Boolean) object) ? 1d : 0d;
            case "Boolean_BigDecimal":
                return ((Boolean) object) ? new BigDecimal(1) : new BigDecimal(0);
            case "Double_Integer":
                return ((Double) object).intValue();
            case "Double_Float":
                return ((Double) object).floatValue();
            case "Double_Long":
                return ((Double) object).longValue();
            case "Double_BigDecimal":
                return new BigDecimal((Double) object);
            case "BigDecimal_Integer":
                return ((BigDecimal) object).intValue();
            case "BigDecimal_Float":
                return ((BigDecimal) object).floatValue();
            case "BigDecimal_Long":
                return ((BigDecimal) object).longValue();
            case "BigDecimal_Double":
                return ((BigDecimal) object).doubleValue();
            case "Integer_BigInteger":
                return new BigInteger(object.toString());
            case "Float_BigInteger":
                return new BigInteger(object.toString());
            case "Long_BigInteger":
                return new BigInteger(object.toString());
            case "String_BigInteger":
                return new BigInteger(object.toString());
            case "Boolean_BigInteger":
                return ((Boolean) object) ? BigInteger.ONE : BigInteger.ZERO;
            case "Double_BigInteger":
                return new BigInteger(object.toString());
            case "BigInteger_Integer":
                return ((BigInteger) object).intValue();
            case "BigInteger_Float":
                return ((BigInteger) object).floatValue();
            case "BigInteger_Long":
                return ((BigInteger) object).longValue();
            case "BigInteger_Double":
                return ((BigInteger) object).doubleValue();
            default:
                throw new ClassCastException("cannot cast " + fromName + " to " + toName);
        }
    }

    public static Class toBoxedClass(Class cls) {
        if (cls.isPrimitive()) {
            return toBoxedClass(cls.getSimpleName()).orElse(cls);
        }
        return cls;
    }

    public static Optional<Class> toBoxedClass(String name) {
        switch (name) {
            case "int":
                return Optional.of(Integer.class);
            case "double":
                return Optional.of(Double.class);
            case "float":
                return Optional.of(Float.class);
            case "boolean":
                return Optional.of(Boolean.class);
            case "long":
                return Optional.of(Long.class);
            default:
                return Optional.empty();
        }
    }

    public static Timestamp parseTime(String string) {
        for (String format : new String[]{
                "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss.S", "yyyy/MM/dd"
        }) {
            if (string.length() == format.length()) {
                try {
                    long time = new SimpleDateFormat(format).parse(string).getTime();
                    return new Timestamp(time);
                } catch (ParseException ignored) {
                }
            }
        }
        throw new ClassCastException("cannot cast [" + string + "] to Timestamp");
    }

    public static Date parseDate(String string) {
        Timestamp timestamp = parseTime(string);
        return new Date(timestamp.getTime());
    }
}
