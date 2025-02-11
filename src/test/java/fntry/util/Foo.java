package fntry.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Foo {

    private int dummyValue;

    public Foo(Foo foo) {
        this.dummyValue = foo.dummyValue;
    }

    public Foo copy() {
        return new Foo(this);
    }

    public Bar toBar(int value) {
        return new Bar(value);
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Foo)) return false;

        Foo foo = (Foo) o;
        return dummyValue == foo.dummyValue;
    }

    @Override
    public int hashCode() {
        return dummyValue;
    }

    @Override
    public String toString() {
        return "Foo{" +
                "dummyValue=" + dummyValue +
                '}';
    }
}