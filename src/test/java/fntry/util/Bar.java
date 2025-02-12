package fntry.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Condition;
import org.assertj.core.condition.NestableCondition;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class Bar {
    private final int field;

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Bar)) return false;

        Bar bar = (Bar) o;
        return field == bar.field;
    }

    @Override
    public int hashCode() {
        return field;
    }

    @Override
    public String toString() {
        return "Bar{" +
                "field=" + field +
                '}';
    }

    public Bar setValue(int newValue) {
        return new Bar(newValue);
    }

    @SafeVarargs
    public static <T extends Bar> Condition<T> bar(Condition<T>... conditions) {
        return NestableCondition.nestable("Bar", conditions);
    }

    public static Condition<Bar> fieldValueIs(int value) {
        return new Condition<>(bar -> bar.getField() == value, "field: " + value);
    }
}