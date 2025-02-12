package fntry.util;

public class Functions {

    public static <T> T toStringWithException(T obj) {
        throw new RuntimeException();
    }

    public static void log(String message) {
        System.out.println(message);
    }

    public static void printAnsiArt(String s) {
    }
}