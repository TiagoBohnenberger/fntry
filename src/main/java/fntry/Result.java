package fntry;

import java.util.Optional;
import java.util.function.Consumer;
import jakarta.annotation.Nullable;

/**
 * Represents a result of a {@link Try try} operation.
 *
 * @param <T> the result type.
 */
public interface Result<T> extends FallbackStrategy<T> {

    /**
     * @return if an operation failed
     */
    boolean isFailed();

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