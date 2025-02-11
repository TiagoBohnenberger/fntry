package fntry;

import java.util.Optional;
import java.util.function.Predicate;

import fntry.util.Bar;
import fntry.util.Consumers;
import fntry.util.DisplayNameGenerators;
import fntry.util.Exceptions;
import fntry.util.Foo;
import fntry.util.Functions;
import fntry.util.Suppliers;
import org.assertj.core.api.Condition;
import org.assertj.core.condition.NestableCondition;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mockStatic;

@DisplayNameGeneration(DisplayNameGenerators.ReplaceCamelCase.class)
class TryTest {

    @Test
    void givenLifted_shouldThrowRuntimeException_whenErrorOnGivenTry() {
        assertThrowsExactly(RuntimeException.class,
                () -> Try.lifted(Suppliers::newFooWithException));
    }

    @Test
    void givenLifted_shouldChangeCheckToUncheckOperation() {
        assertDoesNotThrow(() -> Try.lifted(Suppliers::newFooWithExceptionOK));
    }

    @Test
    void givenTryOf_shouldNotThrowAnyException_whenAnyErrorOccurs() {
        assertDoesNotThrow(() -> Try.of(Exceptions::throwException));

        assertDoesNotThrow(() -> Try.of(
                (Try<?, ? extends Throwable>) () -> Exceptions.throwRuntimeException(null)));
    }

    @Test
    void givenTryOf_shouldReturnOptionalEmpty_whenAnyExceptionOccurs() {
        Optional<?> optional = Try.of(() -> Exceptions.throwRuntimeException(null));

        assertThat(optional).isEmpty();
    }

    @Test
    void givenTryOf_shouldReturnExecuteFallbackOperation_whenAnyExceptionOccurs() {
        String result = Try.of(() -> Exceptions.throwRuntimeException("null"))
                .orElse("A");

        assertThat(result).isEqualTo("A");
    }

    @Test
    void givenTryOf_shouldReturnNonEmptyOptional_whenNoException() {
        Optional<?> optional = Try.of(() -> "a".toUpperCase());

        assertThat(optional)
                .isNotEmpty()
                .get().isEqualTo("A");
    }

    @Test
    void givenTryOf_resultShouldBeFailure_whenAnyExceptionOccurs() {
        Result<Void> result = Try.of(Exceptions::throwException);

        assertThat(result).is(failure());
    }

    @Test
    void givenTryOf_shouldExecuteFallbackConsumer_whenAnyExceptionOccurs() {
        try (MockedStatic<Consumers> consumersMockedStatic = mockStatic(Consumers.class)) {
            Try.of(Exceptions::throwException)
                    .orElse(Consumers::toStringg);

            consumersMockedStatic.verify(() -> Consumers.toStringg(any()), atMostOnce());
        }
    }

    @Test
    void givenTryOf_shouldExecuteFallbackOperation_whenAnyExceptionOccurs() {
        try (MockedStatic<Functions> functionsMockedStatic = mockStatic(Functions.class)) {
            Try.of(Exceptions::throwException)
                    .orElse(() -> Functions.log("ERROR"));

            functionsMockedStatic.verify(() -> Functions.log(anyString()), atMostOnce());
        }
    }

    @Test
    void givenTryOf_shouldReturnFailure_whenProvidedFunctionIsNull() {
        Result<Void> result = Try.of(
                (ThrowingSimpleFunction<Throwable>) null);

        assertThat(result)
                .is(failure())
                .extracting(Result::getException).isExactlyInstanceOf(NullPointerException.class);

        Optional<?> optional = Try.of((Try<?, ?>) null);

        assertThat(optional).isEmpty();
    }

    @Test
    void givenTryWith_shouldReturnFailure_whenProvidedFunctionIsNull() {
        Result<Foo> result = Try.with((ThrowingSupplier<Foo, ? extends Exception>) null)
                .consume(Consumers::toStringg)
                .getResult();

        assertThat(result).is(failure())
                .extracting(Result::get).isNull();
    }

    @Test
    void givenTryWithNonNullInitialValue_resultShouldBeSuccess_whenNoExceptionOccurs() {
        Result<Foo> result = Try.with(Suppliers::newFoo)
                .apply(UnaryThrowingOperator.identity())
                .getResult();

        assertThat(result).is(success())
                .extracting(Result::get).isNotNull()
                .isExactlyInstanceOf(Foo.class);
    }

    @Test
    void givenTryWithNonNullInitialValue_resultShouldBeFailureAndInitialValueShouldNotBeNull_whenAnyExceptionOccurs() {
        Result<Foo> result = Try.with(Suppliers::newFoo)
                .apply(Functions::toStringWithException)
                .getResult();

        assertThat(result).is(failure())
                .extracting(Result::get).isNotNull()
                .isExactlyInstanceOf(Foo.class);

    }

    @Test
    void givenTryWithNonNullInitialValue_shouldReturnPreviousValueAsFallbackStrategy_whenAnyExceptionOccurOnIntermediaryOperation() {
        // @formatter:off
        Foo foo = Try.with(() -> new Foo(1))
                .consume(Consumers::withException)
                .orElsePrevious();

        assertThat(foo).is(
                foo(
                  dummyValueIs(1)));

        // @formatter:on
    }

    @Test
    void givenTryWithNonNullInitialValue_shouldReturnOtherValueAsFallbackStrategy_whenAnyExceptionOccurOnIntermediaryOperation() {
        // @formatter:off
        Foo foo = Try.with(() -> new Foo(1))
                .consume(Consumers::withException)
                .orElse(new Foo(2));

        assertThat(foo).is(
                foo(
                  dummyValueIs(2)));

        // @formatter:on
    }

    @Test
    void givenTryWithNonNullInitialValue_shouldReturnIntermediaryOperationResultValue_whenNoErrorOccurs() {
        Foo foo1 = Suppliers.newFoo();
        Foo foo2 = Try.with(foo1).apply(Foo::copy).orElsePrevious();

        assertThat(foo2)
                .usingEquals((foo_1, foo_2) -> foo_1 == foo_2)
                .isNotEqualTo(foo1);

        String test = Try.with("test").apply(aString -> aString + " final").orElsePrevious();

        assertThat(test).isEqualTo("test final");
    }

    @Test
    void givenTryWithNonNullInitialValue_shouldReturnFinalValue_whenNoErrorOccurs() {
        // @formatter:off
        Foo foo1 = new Foo(1);
        Foo foo2 = Try.with(foo1).apply(UnaryThrowingOperator.identity()).orElse(new Foo(10));

        assertThat(foo2).is(
                foo(
                  dummyValueIs(1)));
        // @formatter:on
    }

    @Test
    void givenMultipleIntermediateOperations_shouldReturnFinalValue_whenNoErrorOccurs() {
        // @formatter:off
        Foo foo1 = new Foo(1);
        Foo foo2 = Try.with(foo1)
                .apply(UnaryThrowingOperator.identity())
                .consume(Consumers::toStringg)
                .apply(foo -> new Foo(2))
                .consume(Consumers::toStringg)
                .orElse(new Foo(10));

        assertThat(foo2).is(
                foo(
                  dummyValueIs(2)));
        // @formatter:on
    }

    @Test
    void givenMultipleIntermediateOperations_shouldReturnFallbackValue_whenErrorOnTheLastStep() {
        // @formatter:off
        Foo foo1 = new Foo(1);
        Foo foo2 = Try.with(foo1)
                .apply(UnaryThrowingOperator.identity())
                .consume(Consumers::toStringg)
                .apply(foo -> new Foo(2))
                .consume(Consumers::withException)
                .orElse(new Foo(10));

        assertThat(foo2).is(
                foo(
                  dummyValueIs(10)));
        // @formatter:on
    }

    @Test
    void givenMultipleMapIntermediateOperations_shouldReturnFinalValue_whenNoErrorOccurs() {
        // @formatter:off
        Bar _bar = Try.with(new Foo(1))
                .map(foo -> foo.toBar(2))
                .consume(Consumers::toStringg)
                .map(bar -> bar.setValue(100))
                .orElse(new Bar(10));

        assertThat(_bar).is(
                bar(
                  fieldValueIs(100)));

        Bar _bar2 = Try.with(new Foo(1))
                .map(foo -> foo.toBar(2))
                .consume(Consumers::toStringg)
                .map(bar -> bar.setValue(100))
                .orElse(Bar::new);

        assertThat(_bar2).is(
                bar(
                  fieldValueIs(100)));
        // @formatter:on
    }

    @Test
    void givenMultipleMapIntermediateOperations_shouldReturnFinalValue_whenNoErrorOccurs_v2() {
        // @formatter:off
        Bar _bar = Try.with(new Foo(1))
                .map(foo -> foo.toBar(2))
                .consume(Consumers::toStringg)
                .map(bar -> bar.setValue(100))
                .orElsePrevious();

        assertThat(_bar).is(
                bar(
                  fieldValueIs(100)));
        // @formatter:on
    }

    @Test
    void givenMultipleIntermediateOperations_shouldReturnPreviousValue_whenAnyErrorOccurs() {
        // @formatter:off
        Foo _foo = new Foo(1);

        Bar _bar = Try.with(_foo)
                .apply(UnaryThrowingOperator.identity())
                .apply(foo -> new Foo(10))
                .consume(Consumers::toStringg)
                .apply(foo -> new Foo(2))
                .map(foo -> new Bar(10))
                .apply(bar -> bar.setValue(100))
                .consume(Consumers::withException)
                .orElsePrevious();

        assertThat(_bar).is(
                bar(
                  fieldValueIs(100)));
        // @formatter:on
    }

    // -- private methods

    private static Condition<? super Result<Foo>> success() {
        Predicate<? super Result<Foo>> success = (Result::isFailure);
        success = success.negate();

        return new Condition<>(success, "Success");
    }

    private static Condition<? super Result<?>> failure() {
        return new Condition<>(Result::isFailure, "Failure");
    }

    private static Condition<Foo> dummyValueIs(int value) {
        return new Condition<>(foo -> foo.getDummyValue() == value, "dummyValue: " + value);
    }

    @SafeVarargs
    private static <T extends Foo> Condition<T> foo(Condition<T>... conditions) {
        return NestableCondition.nestable("Foo", conditions);
    }

    @SafeVarargs
    private static <T extends Bar> Condition<T> bar(Condition<T>... conditions) {
        return NestableCondition.nestable("Bar", conditions);
    }

    private static Condition<Bar> fieldValueIs(int value) {
        return new Condition<>(bar -> bar.getField() == value, "field: " + value);
    }
}