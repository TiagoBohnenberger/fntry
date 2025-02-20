package io.github.tiagobohnenberger.fntry.util;

public class Functions {

    public static <T> T toStringExceptionally(T obj) {
        throw new RuntimeException();
    }

    public static void logError(Throwable throwable) {
        System.err.println(throwable.getLocalizedMessage());
    }

    public static String toUpperCaseExceptionally(String string) throws Throwable {
        throw new Throwable();
    }

    public static <T> T withException(T obj) {
        throw new RuntimeException();
    }

    public static void IOCloseExceptionally() throws Throwable {
        throw new Throwable();
    }

    public static String safeToUpperCase(String string) {
        if (string == null) {
            return "NULL";
        }
        return string.toUpperCase();
    }

    public static <T> T println(T obj) {
        System.out.println(obj);
        return obj;
    }
}