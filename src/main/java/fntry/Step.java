package fntry;

import jakarta.annotation.Nullable;

 /**
 * <p>
 * Represents a step in a chain of operations. A step can consume,
 * map and apply (operation with same result type) on
 * the chain.
 * </p>
 * <p>
 * Furthermore, if the provided operation fails a step return itself with the exception updated
 * or a new one if the operation is success until a final operation to get the result is called.
 * </p>
 *
 * @param <T> type of the step
 */
public interface Step<T> {

     /**
      * Consumes the type of the step.
      *
      * @param consumer the consumer operation
      * @param <E> type of the exception
      * @return the step
      */
    <E extends Throwable> Step<T> consume(ThrowingConsumer<T, E> consumer);

     /**
      * Map the type of the step to another one.
      *
      * @param function the function of the mapper operation
      * @param <E> type of the exception
      * @return the step
      */
    <U, E extends Throwable> Step<U> map(ThrowingFunction<T, ? extends U, E> function);

     /**
      * Apply an operation that returns the same Step of type
      *
      * @param function the operation
      * @return the step
      */
    Step<T> apply(UnaryThrowingOperator<T, ? extends Throwable> function);

    /**
     * @return the previous value supplied for an operation chain, as a fallback strategy
     */
    @Nullable
    T orElsePrevious();

     /**
      * @return other if the step failed
      */
    T orElse(T other);

     /**
      * Encapsulates the result in an abstract type.
      *
      * @return the result
      */
    Result<T> getResult();

     /**
      * Receives a {@link ThrowingSupplier} and delegates to
      * {@link Step#orElse(Object) orElse(T)}
      *
      * @param other the supplier function
      * @return the fallback object's type
      */
    default T orElse(ThrowingSupplier<T, ? extends Throwable> other) {
        try {
            return Step.this.orElse(other.get());
        } catch (Throwable e) {
            return StepImpl.failed(new StepImpl<>(e));
        }
    }
}