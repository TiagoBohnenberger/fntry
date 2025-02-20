package io.github.tiagobohnenberger.fntry;

/**
 * Simple function to use in <i>lambda</i> operations that doesn't receive any argument neither return any value and
 * should never throw an exception.
 * <blockquote><pre>
 *      Try.of(someVal::dangerousOperation)
 *          .orElse(safeFallback::go)
 * </pre></blockquote>
 *
 * <p>This functional interface's method is {@link #apply()} and has the following signature:
 * <blockquote><pre>
 *     void apply();
 * </pre></blockquote>
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