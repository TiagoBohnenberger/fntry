package io.github.tiagobohnenberger.fntry;

import java.util.function.Predicate;

/**
 * Represents a step in a chain of operations.
 *
 * <p>A step can consume, map and/or <strong>apply</strong> (an operation with the
 * same result type) on the chain.
 *
 * <p id="operation-chain-summary">
 * An operation chain, in this context, is just like a sequence
 * of {@linkplain Step steps} that apply
 * some operations and can produce some result.</p>
 *
 * <p>Furthermore, if the provided operation fails, a new {@code Step} is returned
 * with the exception cause updated.
 *
 * <p>However, it returns a new {@code Step} if the
 * operation succeeds until a final operation is called to retrieve the result.
 *
 * <p><strong>Note:</strong> If the step-invoked method is
 * {@link #map(ThrowingFunction) map}, the return is {@code Result<T>}, and if the
 * operation fails, the value of this {@code Result<T>} will be null.
 *
 * @param <T> type of the step
 */
public interface Step<T> extends Result<T> {

    static <T> Step<T> with(T initValue) {
        return new StepImpl<>(initValue);
    }

    static Result<Void> empty() {
        return StepImpl.empty();
    }

    static <T> Step<T> failed(Throwable e) {
        return StepImpl.failed(e);
    }

    /**
     * Consumes the type of the step.
     *
     * @param consumer the consumer operation
     * @param <E>      type of the exception
     * @return the step
     */
    <E extends Throwable> Step<T> consume(ThrowingConsumer<T, E> consumer);

    /**
     * Maps the type of the step and returns a {@code Result<U>}, where can be
     * applied a {@linkplain FallbackStrategy fallbackStrategy}.
     *
     * @param function the function of the mapper operation
     * @param <E>      type of the exception that the function can throw
     * @param <U>      the type of the result
     * @return the {@code Result<U>}
     */
    <U, E extends Throwable> Result<U> map(ThrowingFunction<T, ? extends U, E> function);

    /**
     * Applies an operation that returns the same type of the {@code Step<T>}
     *
     * @param function the operation
     * @return the step
     */
    Step<T> apply(UnaryThrowingOperator<T, ? extends Throwable> function);

    /**
     * Encapsulates the result in an abstract {@code Result<T>} type.
     *
     * @return the {@code Result<T>}
     */
    Result<T> getResult();

    /**
     * If the result value is present in this {@code Step<T>} and
     * matches the given predicate, return a {@code Step<T>} containing the filtered value.
     *
     * @param predicate the predicate to apply to the value, if present
     * @return a {@code Step<T>} containing the matched value
     */
    Step<T> filter(Predicate<T> predicate);
}