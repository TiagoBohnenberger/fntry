package fntry;

import jakarta.annotation.Nullable;

class ResultImpl<T> implements Result<T> {

    private final StepImpl<T> execution;
    private final ResultType type;

    enum ResultType {
        SUCCESS,
        FAILURE;
    }

    static <T> Result<T> empty() {
        return new ResultImpl<>(StepImpl.empty());
    }

    ResultImpl(Throwable throwable) {
        this(new StepImpl<>(throwable));
    }

    ResultImpl(StepImpl<T> execution) {
        this.execution = execution;
        if (execution.failed) {
            this.type = ResultType.FAILURE;
        } else {
            this.type = ResultType.SUCCESS;
        }
    }

    @Override
    public boolean isFailure() {
        return this.type == ResultType.FAILURE;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <E extends Throwable> E getException() {
        return (E) this.execution.throwable;
    }

    @Nullable
    @Override
    public T get() {
        return this.execution.stepResult;
    }
}