package fntry.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Suppliers {

    public static Foo newFooExceptionally() throws Throwable {
        throw new Throwable();
    }

    @SuppressWarnings("RedundantThrows")
    public static <T> T throwingNewFoo() throws Throwable {
        return null;
    }

    public static Foo newFoo() {
        return new Foo();
    }
}