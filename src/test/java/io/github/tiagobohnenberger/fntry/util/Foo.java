package io.github.tiagobohnenberger.fntry.util;

import java.util.function.Predicate;

import io.github.tiagobohnenberger.fntry.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.assertj.core.api.Condition;
import org.assertj.core.condition.NestableCondition;

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

    public static Condition<? super Result<Foo>> success() {
        Predicate<? super Result<Foo>> success = (Result::isFailed);
        success = success.negate();

        return new Condition<>(success, "Success");
    }

    public static Condition<Foo> dummyValueIs(int value) {
        return new Condition<>(foo -> foo.getDummyValue() == value, "dummyValue: " + value);
    }

    @SafeVarargs
    public static <T extends Foo> Condition<T> foo(Condition<T>... conditions) {
        return NestableCondition.nestable("Foo", conditions);
    }

    public Bar toBarExceptionally() {
        throw new RuntimeException();
    }

    public Bar toBarSameValue() {
        return new Bar(this.dummyValue);
    }
}