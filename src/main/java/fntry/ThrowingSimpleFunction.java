package fntry;

/**
 * Simple function to use in <i>lambda</i> operations that doesn't receive any argument neither return any value. Can
 * throw an exception.
 * <pre>
 *  {@code
 *      Try.of(someObj::dangerousOperation)
 *          .orElse((ex) -> log.error("Failed!", ex)
 *  }
 * </pre>
 *
 * @param <E> type of the possible Throwable
 * @see SimpleFunction
 */
@FunctionalInterface
public interface ThrowingSimpleFunction<E extends Throwable> {

    /**
     * Applies a simple functions
     *
     * @throws E the type that could be thrown
     */
    void apply() throws E;
}