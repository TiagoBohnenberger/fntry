package fntry;

/**
 * <p>
 * Represents a step in a chain of operations. A step can consume, map and apply (operation with same result type) on
 * the chain.
 * </p>
 * <p>
 * Furthermore, if the provided operation fails a step return itself with the exception updated or a new one if the
 * operation is success until a final operation to get the result is called.
 * </p>
 *
 * @param <T> type of the step
 */
public interface Step<T> extends Result<T> {

    static <T> Step<T> with(T initValue) {
        return new StepImpl<>(initValue);
    }

    static Result<Void> empty() {
        return StepImpl.empty();
    }

    static <T> Step<T> failed(Throwable e) {
        return StepImpl.failed(e);
    }

    /**
     * Consumes the type of the step.
     *
     * @param consumer the consumer operation
     * @param <E>      type of the exception
     * @return the step
     */
    <E extends Throwable> Step<T> consume(ThrowingConsumer<T, E> consumer);

    /**
     * Map the type of the step to another one.
     *
     * @param function the function of the mapper operation
     * @param <E>      type of the exception
     * @return the step
     */
    <U, E extends Throwable> Result<U> map(ThrowingFunction<T, ? extends U, E> function);

    /**
     * Apply an operation that returns the same Step of type
     *
     * @param function the operation
     * @return the step
     */
    Step<T> apply(UnaryThrowingOperator<T, ? extends Throwable> function);

    /**
     *
     * @return the result
     */
    Result<T> getResult();
}