package fntry;

import java.util.function.Consumer;
import jakarta.annotation.Nullable;

/**
 * Represents a result of a {@link Try try} operation.
 *
 * @param <T> the result type.
 */
public interface Result<T> {

    /**
     * @return if an operation failed
     */
    boolean isFailure();

    /**
     * @return the exception if the result is failure (maybe null).
     */
    @Nullable
    <E extends Throwable> E getException();

    /**
     * @return the result of the operation (maybe null).
     */
    @Nullable
    T get();

    default void orElse(SimpleFunction fallbackOperation) {
        if (isFailure()) {
            fallbackOperation.apply();
        }
    }

    @SuppressWarnings("unchecked")
    default <E extends Throwable> void orElse(Consumer<E> fallbackOperation) {
        if (isFailure()) {
            fallbackOperation.accept((E) getException());
        }
    }
}