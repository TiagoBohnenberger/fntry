package fntry;

/**
 * Encapsulates any operation that receive an argument and could result on an exception.
 *
 * @see UnaryThrowingOperator
 * @param <T> type of the argument
 * @param <R> type of the operation's result
 * @param <E> possible exception's type
 */
@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Throwable> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the throwing function
     * @return the function result
     * @throws E any {@link Throwable}
     */
    R apply(T t) throws E;
}