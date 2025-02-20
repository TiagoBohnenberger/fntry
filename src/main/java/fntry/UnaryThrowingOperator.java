package fntry;

/**
 * Represents an operation on a single operand that produces a result of
 * the same type as its operand and could result
 * in an exception.
 *
 * @param <T> the type of the operand and result of the operator
 * @param <E> exception type
 * @see ThrowingFunction
 */
@FunctionalInterface
public interface UnaryThrowingOperator<T, E extends Throwable>
        extends ThrowingFunction<T, T, E> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the throwing function
     * @return the {@code Try} type result
     * @throws E the type or subtype of {@code Throwable} that can be thrown
     */
    @Override
    T apply(T t) throws E;

    /**
     * Returns a unary throwing operator that always returns its input argument.
     *
     * @param <T> the type of the input and output for the operator
     * @return a unary operator that always returns its input argument
     */
    static <T> UnaryThrowingOperator<T, ? extends Throwable> identity() {
        return t -> t;
    }
}