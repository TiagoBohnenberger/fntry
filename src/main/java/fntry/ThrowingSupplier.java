package fntry;

/**
 * A {@link java.util.function.Supplier Supplier} that could throw a checked exception.
 * <pre>
 *  {@code
 *      Try.with(Suppliers::newObject)
 *          .map(ThrowingFunction.identity());
 *  }
 * </pre>
 *
 * @param <E> type of the possible Throwable
 * @param <T> supplied type
 */
@FunctionalInterface
public interface ThrowingSupplier<T, E extends Throwable> {

    /**
     * Gets a result.
     *
     * @return the result type
     * @throws E any {@link Throwable}
     */
    T get() throws E;
}