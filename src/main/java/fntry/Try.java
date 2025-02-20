package fntry;

import java.util.Optional;

/**
 * Represents an attempt at some operation.
 *
 * <p>Adapts the java's try block into a functional style, avoiding "hadouken code"
 * when code requires multiple operations subject to exceptions, or when it's
 * necessary to execute simple inline operations that must not break the code.
 *
 * <p>So, here's a simple example:
 * <blockquote><pre>
 * // a code like this...
 *  try {
 *      someString.trim();
 *  } catch (NullPointerException e) {
 *      someString = "";
 *  }
 *  try {
 *      someOtherString.trim();
 *  } catch (NullPointerException e) {
 *      someOtherString = "";
 *  }
 * </pre></blockquote><p>
 * ...could be written in this way:
 * <blockquote><pre>
 *      Try.of(someString::trim()).orElse("");
 *      Try.of(someOtherString::trim()).orElse("");
 * </pre></blockquote><p>
 * making it way more readable.
 *
 * <p>A try operation can also return a value that is the type {@code <T>} of this {@code Try} instance.
 *
 * <p>This functional interface's method is {@link #apply()} and has the following signature:
 * <blockquote><pre>
 *     T apply() throws E;
 * </pre></blockquote>
 *
 * @param <T> type of the result
 * @param <E> checked exception
 */
@FunctionalInterface
public interface Try<T, E extends Throwable> {

    /**
     * Runs a try and returns the result.
     *
     * @return the try operation result type
     * @throws E type that can be thrown by the try operation
     */
    T apply() throws E;

    /**
     * Try to apply a function and wrap the underlying checked exception into an unchecked exception.
     *
     * @param aTry a try function
     * @param <T>  the type of the {@code Try}
     * @return the lifted value
     * @throws RuntimeException when any throwable occur
     */
    static <T> T lifted(Try<? extends T, ?> aTry) {
        try {
            return aTry.apply();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Receives a {@link ThrowingSimpleFunction} operation and try to apply it.
     *
     * @param supplier the simple operation
     * @param <E>      type that can be thrown
     * @return {@link Result} of {@link Void}, representing a result with no
     * contained value
     */
    static <E extends Throwable> Result<Void> just(ThrowingSimpleFunction<E> supplier) {
        try {
            supplier.apply();
            return Step.empty();
        } catch (Throwable e) {
            return Step.failed(e);
        }
    }

    /**
     * Receives a {@linkplain Try} instance and return an {@link Optional} for the result.
     *
     * <p>If the try failed because of any exception, return an empty {@code optional}.
     *
     * @param aTry the try instance
     * @param <T>  type of try
     * @param <E>  type of the exception that the try can throw
     * @return {@code Optional<T>}
     */
    static <T, E extends Throwable> Optional<T> get(Try<T, ? extends E> aTry) {
        try {
            return Optional.ofNullable(aTry.apply());
        } catch (Throwable e) {
            return Optional.empty();
        }
    }

    /**
     * Initiates an <a href="{@docRoot}/fntry/Step.html#operation-chain-summary">operation chain</a>
     * with {@linkplain Try a try}.
     *
     * @param aTry the try operation
     * @param <T>  the type of the provided value
     * @return a {@linkplain Step} containing the initial value (that might be {@code null})
     */
    static <T> Step<T> of(Try<T, ? extends Throwable> aTry) {
        T value;
        try {
            value = aTry.apply();
        } catch (Throwable e) {
            return Step.failed(e);
        }
        return with(value);
    }

    /**
     * Initiates an operation chain with a given value.
     *
     * @param initValue the initial value for the operation chain
     * @param <T>       the type of the provided value
     * @return a {@link Step} containing the initial value
     */
    static <T> Step<T> with(T initValue) {
        return Step.with(initValue);
    }
}