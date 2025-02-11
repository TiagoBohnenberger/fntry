package fntry.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

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
}