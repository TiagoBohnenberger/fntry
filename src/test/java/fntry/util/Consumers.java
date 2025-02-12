package fntry.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Consumers {

    public static void println(Object obj) {
        System.out.println(obj);
    }

    public static <T> void exceptionally(T o) throws Throwable {
        throw new Exception();
    }

    public static <T> void printAsAnsiArtExceptionally(T value) throws Exception {
        throw new Exception();
    }

    public static <T> void printAsAnsiArt(T value) {
    }

    public static void logError(Throwable throwable) {
    }
}