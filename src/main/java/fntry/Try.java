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
    static <T> T lifted(Try<? extends T, ?> function) {
        try {
            return function.apply();
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
    static <E extends Throwable> Result<Void> of(ThrowingSimpleFunction<E> supplier) {
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
    static <T> Optional<T> of(Try<? extends T, ? extends Throwable> function) {
        try {
            return Optional.ofNullable(function.apply());
        } catch (Throwable e) {
            return Optional.empty();
        }
    }

    /**
     * <p>
     * Initiates an operation chain with a given value.
     * </p>
     *
     * @param initValue the initial value for the operation chain
     * @param <T>       the type of the provided value
     * @return a {@link Step} containing the initial value
     */
    static <T> Step<T> with(T initValue) {
        return new StepImpl<>(initValue);
    }

    /**
     * <p>
     * Initiates an operation chain with a {@link ThrowingSupplier supplier} of value.
     * </p>
     *
     * @param initValueSupplier value supplier
     * @param <T>               the type of the provided value
     * @param <E>               type that can be thrown
     * @return a {@link Step} containing the initial value
     */
    static <T, E extends Throwable> Step<T> with(ThrowingSupplier<T, E> initValueSupplier) {
        T value;
        try {
            value = initValueSupplier.get();
        } catch (Throwable e) {
            return new StepImpl<>(e);
        }
        return with(value);
    }
}