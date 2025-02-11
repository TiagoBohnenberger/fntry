package fntry;

/**
 * Simple function to use in <i>lambda</i> operations that doesn't receive any argument neither return any value and
 * should never throw an exception.
 * <pre>
 *  {@code
 *      Try.of(someVal::dangerousOperation)
 *          .orElse(safeFallback::go)
 *  }
 * </pre>
 *
 * @see ThrowingSimpleFunction
 */
@FunctionalInterface
public interface SimpleFunction {

    /**
     * Applies a simple functions
     */
    void apply();
}