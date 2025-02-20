package io.github.tiagobohnenberger.fntry;

import java.util.function.Consumer;
import java.util.function.Supplier;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * A representation of a fallback strategy to be executed when a {@linkplain Result result} fails.
 *
 * @param <T> the type of the fallback result value
 */
public interface FallbackStrategy<T> {

    /**
     * @param other to provide in case the {@code Result} fails.
     * @return other result value if this fails
     */
    T orElse(T other);

    /**
     * Receives and run a {@linkplain Consumer consumer} as fallback operation when a {@code Result} fails.
     * If the {@code fallbackConsumer} throws any exception, the exception is wrapper rethrown
     * wrapped into a {@code RuntimeException}.
     *
     * @param fallbackConsumer the consuming operation when this fails
     * @throws RuntimeException with the cause if any error happens.
     */
    void orElse(ThrowingConsumer<T, ? extends Throwable> fallbackConsumer);

    /**
     * A fallback operation in case of a previously failed result.
     *
     * @param fallbackConsumer the fallback consumer
     * @return the type of the result or null if the {@code fallbackConsumer} throws any exception
     * or the underlying {@linkplain Result} itself produces a {@code null} value
     */
    @Nullable
    T orThen(UnaryThrowingOperator<T, ? extends Throwable> fallbackConsumer);

    /**
     * @param fallbackOperation a simple function to run if the
     *                          underlying {@linkplain Result result} fails
     */
    void orSimply(SimpleFunction fallbackOperation);

    /**
     * Receives a {@link Supplier} and delegates to {@link Step#orElse(Object) orElse(T)}
     *
     * @param otherSupplier the supplier function
     * @return the fallback object's type
     * @throws RuntimeException with the cause encapsulated when the provided supplier is null or
     *                          throws an exception
     */
    default T orElseGet(@Nonnull Supplier<T> otherSupplier) {
        return Try.lifted(() -> this.orElse(otherSupplier.get()));
    }

    /**
     * A {@linkplain Consumer consumer} for any {@code E extends Throwable}
     * related to the underlying {@linkplain Result result} if it fails.
     *
     * @param fallbackThrowingConsumer the consuming operation for an exception type
     * @param <E>                      the type of the exception
     */
    <E extends Throwable> void otherwise(Consumer<E> fallbackThrowingConsumer);

}