package io.github.tiagobohnenberger.fntry;

import java.util.Optional;
import java.util.function.Consumer;
import jakarta.annotation.Nullable;

/**
 * Represents a result of some operation.
 *
 * <p>This operation can be either an intermediate {@linkplain Step step}
 * or a final one in a sequence of chained operations.
 *
 * @param <T> the result type.
 */
public interface Result<T> extends FallbackStrategy<T> {

    /**
     * @return if an operation has failed due to any exception that might occur.
     */
    boolean isFailed();

    /**
     * @param <E> the type o the error
     * @return The exception responsible for this failing {@code Result}, if that
     * is the case.
     * It might be {@code null}
     */
    @Nullable
    <E extends Throwable> E getException();

    /**
     * @return the result of the operation (might be {@code null})
     */
    @Nullable
    T get();

    /**
     * @return an {@code Optional<T>} of the result
     */
    default Optional<T> asOptional() {
        return Optional.ofNullable(this.get());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default void orElse(ThrowingConsumer<T, ? extends Throwable> fallbackConsumer) {
        if (this.isFailed()) {
            Try.lifted(() -> {
                fallbackConsumer.accept(this.get());
                return null;
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default <E extends Throwable> void otherwise(Consumer<E> fallbackThrowingConsumer) {
        if (this.isFailed()) {
            fallbackThrowingConsumer.accept(this.getException());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default void orSimply(SimpleFunction fallbackOperation) {
        if (this.isFailed()) {
            fallbackOperation.apply();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    default T orThen(UnaryThrowingOperator<T, ? extends Throwable> fallbackConsumer) {
        T result = this.get();
        if (this.isFailed()) {
            try {
                return fallbackConsumer.apply(result);
            } catch (Throwable e) {
                return null;
            }
        }
        return result;
    }
}