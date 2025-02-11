package fntry;

import jakarta.annotation.Nullable;

public class StepImpl<T> implements Step<T> {
    private static final StepImpl<?> EMPTY = new StepImpl<>(null);

    @Nullable
    final T stepResult;
    @Nullable
    Throwable throwable;
    boolean failed;

    private StepImpl(@Nullable T stepResult, @Nullable Throwable throwable) {
        this.stepResult = stepResult;

        if (throwable != null) {
            this.failed = true;
            this.throwable = throwable;
        }
    }

    StepImpl(@Nullable T stepResult) {
        this(stepResult, null);
    }

    StepImpl(@Nullable Throwable throwable) {
        this(null, throwable);
    }

    @SuppressWarnings("unchecked")
    static <T> StepImpl<T> empty() {
        return (StepImpl<T>) EMPTY;
    }

    @Override
    public Step<T> apply(UnaryThrowingOperator<T, ?> function) {
        if (failed) {
            return failed(this);
        }

        try {
            return new StepImpl<>(function.apply(stepResult));
        } catch (Throwable e) {
            this.throwable = e;
            return failed(this);
        }
    }


    @Override
    public <E extends Throwable> Step<T> consume(ThrowingConsumer<T, E> consumer) {
        if (failed) {
            return failed(this);
        }

        try {
            consumer.accept(stepResult);
        } catch (Throwable e) {
            this.throwable = e;
            return failed(this);
        }
        return this;
    }

    @Override
    public <U, E extends Throwable> Step<U> map(ThrowingFunction<T, ? extends U, E> function) {
        if (failed) {
            return failed(this);
        }

        try {
            return new StepImpl<>(function.apply(stepResult));
        } catch (Throwable e) {
            this.throwable = e;
            return failed(this);
        }
    }


    @Nullable
    @Override
    public T orElsePrevious() {
        return this.stepResult;
    }

    @Override
    public T orElse(T other) {
        if (this.failed) {
            return other;
        }
        return this.stepResult;
    }

    @Override
    public Result<T> getResult() {
        return new ResultImpl<>(this);
    }

    @SuppressWarnings("unchecked")
    static <ANY_STEP> ANY_STEP failed(StepImpl<?> step) {
        step.failed = true;
        return (ANY_STEP) step;
    }
}