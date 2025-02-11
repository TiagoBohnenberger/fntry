package fntry.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Consumers {

    public static void toStringg(Object obj) {
        System.out.println(obj.toString());
    }

    public static <T> void withException(T o) throws Throwable {
        throw new Exception();
    }
}