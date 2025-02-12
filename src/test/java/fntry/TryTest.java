package fntry;

import java.util.Optional;

import fntry.util.Bar;
import fntry.util.Consumers;
import fntry.util.DisplayNameGenerators;
import fntry.util.Exceptions;
import fntry.util.Foo;
import fntry.util.Functions;
import fntry.util.Suppliers;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.MockedStatic;

import static fntry.util.Bar.bar;
import static fntry.util.Bar.fieldValueIs;
import static fntry.util.Foo.dummyValueIs;
import static fntry.util.Foo.foo;
import static fntry.util.Foo.success;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@DisplayNameGeneration(DisplayNameGenerators.ReplaceCamelCase.class)
class TryTest {

    @Test
    void givenLifted_shouldThrowRuntimeException_whenErrorOnGivenTry() {
        assertThrowsExactly(RuntimeException.class,
                () -> Try.lifted(Suppliers::newFooExceptionally));
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
                    .orElse(Consumers::println);

            consumersMockedStatic.verify(() -> Consumers.println(any()), atMostOnce());
        }
    }

    @Test
    void givenTryOf_shouldExecuteFallbackOperation_whenAnyExceptionOccurs() {
        try (MockedStatic<Functions> functionsMockedStatic = mockStatic(Functions.class)) {
            Try.of(Exceptions::throwException)
                    .orSimply(() -> Functions.log("ERROR"));

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
    void givenTryWith_shouldExecuteIntermediaryConsumerOperation_whenNoErrorOccurs() {
        try (MockedStatic<Consumers> consumersMockedStatic
                     = mockStatic(Consumers.class, Answers.CALLS_REAL_METHODS)) {

            Try.with("Hello World")
                    .consume(Consumers::printAsAnsiArt)
                    .otherwise(Consumers::logError);

            consumersMockedStatic.verify(() -> Consumers.printAsAnsiArt(anyString()));
            consumersMockedStatic.verify(() -> Consumers.logError(any(Throwable.class)), never());
        }
    }

    @Test
    void givenTryWith_shouldExecuteFallbackConsumingOperation_whenAnyErrorOccurs() {
        try (MockedStatic<Consumers> consumersMockedStatic
                     = mockStatic(Consumers.class, Answers.CALLS_REAL_METHODS)) {

            Try.with("Hello World")
                    .consume(Consumers::printAsAnsiArtExceptionally)
                    .otherwise(Consumers::logError);

            consumersMockedStatic.verify(() -> Consumers.logError(any(Throwable.class)));
        }
    }

    @Test
    void givenTryWith_shouldExecuteFallbackConsumer_whenAnyExceptionOccurs() {
        try (MockedStatic<Consumers> consumersMockedStatic
                     = mockStatic(Consumers.class, Answers.CALLS_REAL_METHODS)) {

            String aString = "Hello World";

            Try.with(aString)
                    .consume(Consumers::printAsAnsiArtExceptionally)
                    .orElse(Consumers::println);

            consumersMockedStatic.verify(() -> Consumers.println(eq("Hello World")));
        }
    }

    @Test
    void givenTryWith_shouldReturnFailure_whenProvidedFunctionIsNull() {
        Result<Foo> result = Try.with((ThrowingSupplier<Foo, ? extends Exception>) null)
                .consume(Consumers::println)
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
                .consume(Consumers::exceptionally)
                .orElse(new Foo(2));

        assertThat(foo).is(
                foo(
                  dummyValueIs(2)));

        // @formatter:on
    }

    @Test
    void givenTryWithNonNullInitialValue_shouldReturnOtherValueAsFallbackStrategy_whenAnyExceptionOccurOnIntermediaryOperation() {
        // @formatter:off
        Foo foo = Try.with(() -> new Foo(1))
                .consume(Consumers::exceptionally)
                .orElse(new Foo(2));

        assertThat(foo).is(
                foo(
                  dummyValueIs(2)));

        // @formatter:on
    }

    @Test
    void givenTryWithNonNullInitialValue_shouldReturnIntermediaryOperationResultValue_whenNoErrorOccurs() {
        Foo foo1 = Suppliers.newFoo();
        Foo foo2 = Try.with(foo1).apply(Foo::copy).orElse(foo1);

        assertThat(foo2).isNotSameAs(foo1);

        String test = Try.with("test").apply(aString -> aString + " final").orElse("test");

        assertThat(test).isEqualTo("test final");
    }

    @Test
    void givenNonNullInitialValue_TryOfShouldReturnFinalValue_whenNoErrorOccurs() {
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
        Foo foo1 = new Foo(2);
        Foo foo2 = Try.with(foo1)
                .apply(UnaryThrowingOperator.identity())
                .consume(Consumers::println)
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
                .apply(foo -> new Foo(2))
                .consume(Consumers::exceptionally)
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
                .orElse(new Bar(10));

        assertThat(_bar).is(
                bar(
                  fieldValueIs(2)));

        Bar _bar2 = Try.with(new Foo(1))
                .map(foo -> foo.toBar(2))
                .orElseGet(Bar::new);

        assertThat(_bar2).is(
                bar(
                  fieldValueIs(2)));
        // @formatter:on
    }

    @Test
    void givenMultipleIntermediateOperations_shouldReturnFallbackValue_whenAnErrorOnAnyStepOccurs() {
        // @formatter:off
        Foo _foo = new Foo(1);

        Bar _bar = Try.with(_foo)
                .apply(UnaryThrowingOperator.identity())
                .apply(foo -> new Foo(10))
                .map(Foo::toBarExceptionally)
                .orElseGet(() -> new Bar(300));

        assertThat(_bar).is(
                bar(
                  fieldValueIs(300)));
        // @formatter:on
    }

    @Test
    void givenMultipleIntermediateOperations_shouldRunFallbackOperation_whenAnErrorOnAnyStepOccurs() {
        // @formatter:off
        try (MockedStatic<Consumers> consumersMockedStatic
                     = mockStatic(Consumers.class, Answers.CALLS_REAL_METHODS);
             MockedStatic<Functions> functionsMockedStatic
                     =mockStatic(Functions.class, Answers.CALLS_REAL_METHODS)
        ) {
            Foo fooSpy = spy(new Foo(2));

            Try.with(fooSpy)
                .apply(Functions::toStringWithException)
                .consume(Consumers::exceptionally)
                .apply(Foo::copy)
                .apply(_foo -> new Foo(100))
                .orElse(Consumers::println);

            verify(fooSpy, never()).copy();

            functionsMockedStatic.verify(() -> Functions.toStringWithException(eq(new Foo(2))));
            consumersMockedStatic.verify(() -> Consumers.println(eq(new Foo(2))));

        }
        // @formatter:on
    }


    // ---- private methods

    private static Condition<? super Result<?>> failure() {
        return new Condition<>(Result::isFailed, "Failure");
    }
}