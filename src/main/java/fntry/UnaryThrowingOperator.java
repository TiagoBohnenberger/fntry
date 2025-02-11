package fntry;

/**
 *
 * Represents an operation on a single operand that produces a result of the
 * same type as its operand and could result in an exception.
 *
 * @see ThrowingFunction
 * @param <T> the type of the operand and result of the operator
 * @param <E> exception type

 */
@FunctionalInterface
public interface UnaryThrowingOperator<T, E extends Throwable>
        extends ThrowingFunction<T, T, E> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the throwing function
     * @return
     * @throws E
     */
    @Override
    T apply(T t) throws E;

    /**
     * Returns a unary throwing operator that always returns its input argument.
     *
     * @param <T> the type of the input and output of the operator
     * @return a unary operator that always returns its input argument
     */
    static <T, E extends Throwable> UnaryThrowingOperator<T, E> identity() {
        return t -> t;
    }
}