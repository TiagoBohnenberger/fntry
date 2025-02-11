package fntry.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Suppliers {

    public static Foo newFooWithException() throws Throwable {
        throw new Throwable();
    }

    public static Foo newFooWithExceptionOK() throws Throwable {
        return Suppliers.newFoo();
    }

    public static Foo newFoo() {
        return new Foo();
    }
}