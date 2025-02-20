package io.github.tiagobohnenberger.fntry.util;

public class Exceptions {

    public static <T> T throwRuntimeException(T obj) {
        throw new RuntimeException();
    }

    public static void throwException() throws Throwable {
        throw new Throwable();
    }
}