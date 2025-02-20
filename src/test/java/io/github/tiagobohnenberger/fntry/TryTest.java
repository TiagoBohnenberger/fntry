package io.github.tiagobohnenberger.fntry;

import java.util.Optional;

import io.github.tiagobohnenberger.fntry.util.Bar;
import io.github.tiagobohnenberger.fntry.util.Consumers;
import io.github.tiagobohnenberger.fntry.util.DisplayNameGenerators;
import io.github.tiagobohnenberger.fntry.util.Exceptions;
import io.github.tiagobohnenberger.fntry.util.Foo;
import io.github.tiagobohnenberger.fntry.util.Functions;
import io.github.tiagobohnenberger.fntry.util.Suppliers;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.MockedStatic;

import static io.github.tiagobohnenberger.fntry.util.Bar.bar;
import static io.github.tiagobohnenberger.fntry.util.Bar.fieldValueIs;
import static io.github.tiagobohnenberger.fntry.util.Foo.dummyValueIs;
import static io.github.tiagobohnenberger.fntry.util.Foo.foo;
import static io.github.tiagobohnenberger.fntry.util.Foo.success;
import static io.github.tiagobohnenberger.fntry.util.Functions.logError;
import static io.github.tiagobohnenberger.fntry.util.Functions.toUpperCaseExceptionally;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
        assertDoesNotThrow(() -> Try.lifted(Suppliers::throwingNewFoo));
    }

    @Test
    void shouldNotThrowAnyException_whenAnyErrorOccurs() {
        assertDoesNotThrow(() -> Try.just(Functions::IOCloseExceptionally));

        assertDoesNotThrow(() -> Try.get(Suppliers::newFooExceptionally));
    }

    @Test
    void givenTryGet_shouldReturnOptionalEmpty_whenAnyExceptionOccurs() {
        Optional<Foo> optional = Try.get(Suppliers::newFooExceptionally);

        assertThat(optional).isEmpty();
    }

    @Test
    void givenTryGet_shouldReturnOptionalEmpty_whenProvidedTryOperationIsNull() {
        Optional<?> optional = Try.get(null);
        assertThat(optional).isEmpty();
    }

    @Test
    void givenTryGet_shouldReturnFallbackValue_whenAnyExceptionOccurs() {
        Foo result = Try.get(Suppliers::newFooExceptionally)
                .orElse(new Foo(20));

        assertThat(result).is(foo(dummyValueIs(20)));
    }

    @Test
    void givenTryGet_shouldReturnNonEmptyOptional_whenNoException() {
        Optional<String> optional = Try.get("a"::toUpperCase);

        assertThat(optional).get().isEqualTo("A");
    }

    @Test
    void givenTryJust_resultShouldBeFailure_whenAnyExceptionOccurs() {
        Result<Void> result = Try.just(Functions::IOCloseExceptionally);

        assertThat(result).is(failure());
    }

    @Test
    void givenTryJust_shouldExecuteFallbackConsumer_whenAnyExceptionOccurs() {
        try (MockedStatic<Consumers> consumersMockedStatic
                     = mockStatic(Consumers.class, Answers.CALLS_REAL_METHODS)) {

            String aString = "Hello World";

            Try.just(() -> Consumers.printAsAnsiArtExceptionally(aString))
                    .orSimply(() -> Consumers.println(aString));

            consumersMockedStatic.verify(() -> Consumers.println(eq("Hello World")));
        }
    }

    @Test
    void givenTryJust_shouldExecuteFallbackOperation_whenAnyExceptionOccurs() {
        try (MockedStatic<Functions> functionsMockedStatic =
                     mockStatic(Functions.class, Answers.CALLS_REAL_METHODS)) {

            Try.just(Exceptions::throwException)
                    .otherwise(Functions::logError);

            functionsMockedStatic.verify(() -> logError(any(Throwable.class)));
        }
    }

    @Test
    void givenTryJust_shouldReturnFailure_whenProvidedFunctionIsNull() {
        Result<Void> result = Try.just(null);

        assertThat(result)
                .is(failure())
                .extracting(Result::getException).isExactlyInstanceOf(NullPointerException.class);
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
    void givenTryOf_shouldReturnFailure_whenProvidedFunctionIsNull() {
        Result<Foo> result = Try.of((Try<Foo, ?>) null)
                .consume(Consumers::println);

        assertThat(result).is(failure())
                .extracting(Result::get).isNull();
    }

    @Test
    void givenTryOf_resultValueShouldBeNull_whenAnyExceptionOnMap() {
        Result<Bar> result = Try.of(Foo::new)
                .consume(Consumers::println)
                .map(Foo::toBarExceptionally);

        assertThat(result).is(failure())
                .extracting(Result::get).isNull();
    }

    @Test
    void givenTryOf_resultValueShouldBeEmptyOptional_whenAnyExceptionOnMap() {
        Optional<Bar> result = Try.of(Foo::new)
                .consume(Consumers::println)
                .map(Foo::toBarExceptionally)
                .asOptional();

        assertThat(result).isEmpty();
    }

    @Test
    void givenTryOf_shouldRunFallbackMethod_whenAnyExceptionOnMap() {
        Bar result = Try.of(Foo::new)
                .consume(Consumers::println)
                .map(Foo::toBarExceptionally)
                .orElse(new Bar(10));

        assertThat(result).is(bar(fieldValueIs(10)));
    }

    @Test
    void givenTryOf_shouldGetFinalValue_whenNoExceptionOnMap() {
        Bar result = Try.of(() -> new Foo(1))
                .consume(Consumers::println)
                .map(Foo::toBarSameValue)
                .orElse(new Bar(10));

        assertThat(result).is(bar(fieldValueIs(1)));
    }

    @Test
    void givenNonNullInitialValue_TryOfShouldBeSuccess_whenNoExceptionOccurs() {
        Result<Foo> result = Try.of(Suppliers::newFoo)
                .apply(UnaryThrowingOperator.identity()).getResult();

        assertThat(result).is(success())
                .extracting(Result::get).isNotNull()
                .isExactlyInstanceOf(Foo.class);
    }

    @Test
    void givenNonNullInitialValue_TryOfResultShouldBeFailureAndValueSameAsInitial_whenAnyExceptionOccurs() {
        Result<Foo> result = Try.of(() -> new Foo(1))
                .apply(Functions::toStringExceptionally).getResult();

        assertThat(result).is(failure())
                .extracting(Result::get)
                .extracting(Foo::getDummyValue).isEqualTo(1);
    }

    @Test
    void givenErrorOnInitialValueSupplier_shouldReturnOptionalEmpty_whenGettingTheResultAsOptional() {
        Optional<Foo> result = Try.of(Suppliers::newFooExceptionally)
                .apply(Foo::copy)
                .asOptional();

        assertThat(result).isEmpty();
    }

    @Test
    void givenNonNullInitialValue_shouldReturnNonEmptyOptional_whenGettingTheResultAsOptional() {
        Optional<Bar> result = Try.of(() -> new Foo(2))
                .map(foo -> Functions.withException(foo.toBar(2)))
                .asOptional();

        assertThat(result).isEmpty();
    }

    @Test
    void givenNonNullInitialValue_TryOfShouldReturnOtherValueAsFallbackStrategy_whenAnyExceptionOccurOnIntermediaryOperation() {
        // @formatter:off
        Foo foo = Try.of(() -> new Foo(1))
                .consume(Consumers::exceptionally)
                .orElse(new Foo(2));

        assertThat(foo).is(
                foo(
                  dummyValueIs(2)));

        // @formatter:on
    }

    @Test
    @SuppressWarnings({"ConstantValue"})
    void givenExceptionOnInitialOperation_TryOfShouldReturnTheFallbackOperationResult() {
        String aString = null;

        String result = Try.of(() -> toUpperCaseExceptionally(aString))
                .orThen(Functions::safeToUpperCase);

        assertThat(result).isEqualTo("NULL");
    }

    @Test
    void givenExceptionOnInitialOperation_TryOfWillReturnNullIfTheFallbackOperationAlsoFails() {
        String aString = "a string";

        String result = Try.of(() -> Functions.withException(aString))
                .orThen(Functions::toStringExceptionally);

        assertThat(result).isNull();
    }

    @Test
    void givenNonNullInitialValue_TryOfShouldReturnIntermediaryOperationResultValue_whenNoErrorOccurs() {
        Foo foo1 = Suppliers.newFoo();
        Foo foo2 = Try.with(foo1).apply(Foo::copy).orElse(foo1);

        assertThat(foo2).isNotSameAs(foo1);

        String test = Try.with("test").apply(aString -> aString + " final").orElse("NULL");

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
                .apply(Functions::println)
                .consume(Consumers::exceptionally)
                .apply(Foo::copy)
                .apply(_foo -> new Foo(100))
                .orElse(Consumers::println);

            verify(fooSpy, never()).copy();

            functionsMockedStatic.verify(() -> Functions.println(eq(new Foo(2))));
            consumersMockedStatic.verify(() -> Consumers.println(eq(new Foo(2))));

        }
        // @formatter:on
    }


    // ---- private methods

    private static Condition<? super Result<?>> failure() {
        return new Condition<>(Result::isFailed, "Failure");
    }
}