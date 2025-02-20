package fntry;

/**
 * A {@link java.util.function.Consumer Consumer} that could throw a checked exception.
 * <pre class="code"><code class="java">
 *     Try.with(Suppliers::userNotes)
 *          .map(Consumers::forNotes);
 *  </code></pre>
 *
 * @param <T> the type to consume
 * @param <E> them throwable type
 */
@FunctionalInterface
public interface ThrowingConsumer<T, E extends Throwable> {

    /**
     * Performs this operation on the given argument.
     *
     * @param obj the input argument
     * @throws E type that can be thrown
     */
    void accept(T obj) throws E;
}