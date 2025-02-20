package fntry;

import java.util.Optional;

/**
 * Represents an attempt at some operation.
 * <p>
 * Adapts the java's try block into a functional style, avoiding "hadouken code" when code requires multiple operations
 * subject to exceptions, or when it's needed to execute simple inline operations that must not break the code.
 * </p>
 * So, here's a simple example:
 * <pre>
 * {@code
 *
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
 * }
 * </pre>
 * ...could be written on this way:
 * <pre>
 *
 * {@code
 *  Try.of(() -> someString.trim()).orElse("");
 *  Try.of(() -> someOtherString.trim()).orElse("");
 * }
 * </pre>
 * <p>
 * making it way more readable.
 *
 * @param <T> type of the result
 * @param <E> checked exception
 */
@FunctionalInterface
public interface Try<T, E extends Throwable> {

    /**
     * Applies the try and returns the result
     *
     * @return the try operation result type
     * @throws E type that can be thrown
     */
    T apply() throws E;

    /**
     * Try to apply a function and wrap the underlying checked exception into an unchecked exception
     *
     * @param function a try function
     * @param <T>      the type of the function
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
     * Receives a simple operation and try to apply it
     *
     * @param supplier the simple operation
     * @param <E>      type that can be thrown
     * @return {@link Result} of {@link Void}
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
     * <p>Receives a {@link Try} operation and return an {@link Optional} for the result.</p>
     * <p>If the try failed for because of any exception, return an empty optional.</p>
     *
     * @param function the try operation
     * @param <T>      type of try
     * @return optional
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