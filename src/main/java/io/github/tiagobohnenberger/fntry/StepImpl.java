package io.github.tiagobohnenberger.fntry;

import jakarta.annotation.Nullable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

@With(AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class StepImpl<T> implements Step<T> {
    private static final StepImpl<?> EMPTY = new StepImpl<>();

    @Nullable
    private T result;
    @Nullable
    private Throwable throwable;
    private boolean failed;

    private StepImpl(@Nullable T result, @Nullable Throwable throwable) {
        this.result = result;

        if (throwable != null) {
            this.failed = true;
            this.throwable = throwable;
        }
    }

    StepImpl(@Nullable T result) {
        this(result, null);
    }

    StepImpl(@Nullable Throwable throwable) {
        this(null, throwable);
    }

    @SuppressWarnings("unchecked")
    public static <T> StepImpl<T> empty() {
        return (StepImpl<T>) EMPTY;
    }

    @SuppressWarnings("unchecked")
    private static <T, ANY_RESULT extends Result<T>> ANY_RESULT failed(StepImpl<?> step) {
        step.failed = true;
        return (ANY_RESULT) step;
    }

    static <T> Step<T> failed(Throwable e) {
        return failed(new StepImpl<>(e));
    }

    @Override
    public Step<T> apply(UnaryThrowingOperator<T, ?> function) {
        if (failed) {
            return this;
        }

        try {
            return this.withResult(function.apply(result));
        } catch (Throwable e) {
            return failed(withThrowable(e));
        }
    }

    @Override
    public <E extends Throwable> Step<T> consume(ThrowingConsumer<T, E> consumer) {
        if (this.failed) {
            return this;
        }

        try {
            consumer.accept(result);
        } catch (Throwable e) {
            return failed(withThrowable(e));
        }
        return this;
    }

    @Override
    public <U, E extends Throwable> Result<U> map(ThrowingFunction<T, ? extends U, E> function) {
        if (failed) {
            return failed(withResult(null));
        }

        try {
            return new StepImpl<>(function.apply(result));
        } catch (Throwable e) {
            return failed(withResult(null).withThrowable(e));
        }
    }

    @Override
    public T orElse(T other) {
        if (this.failed) {
            return other;
        }
        return this.result;
    }

    public Result<T> getResult() {
        return this;
    }

    @Override
    public boolean isFailed() {
        return failed;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <E extends Throwable> E getException() {
        return (E) this.throwable;
    }

    @Nullable
    @Override
    public T get() {
        return result;
    }
}