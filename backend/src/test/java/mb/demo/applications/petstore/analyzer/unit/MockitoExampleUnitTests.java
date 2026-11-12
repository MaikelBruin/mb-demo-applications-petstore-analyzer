package mb.demo.applications.petstore.analyzer.unit;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Very basic Mockito examples that only use classes from the JDK, so there is nothing to know about the application
 * to follow them. Mockito.mock/when/verify are used in their long form on purpose; in real tests you would
 * statically import them.
 */
@SuppressWarnings("unchecked")
public class MockitoExampleUnitTests {

    // ---------------------------------------------------------------- mocking and stubbing

    /**
     * A mock is a fake object that does nothing by itself. Every method returns the default value of its return type:
     * null for objects, 0 for numbers, false for booleans and an empty collection for collections.
     */
    @Test
    void mock_returnsDefaultValuesWhenNothingIsStubbed() {
        List<String> list = Mockito.mock(List.class);

        assertThat(list.size()).isZero();
        assertThat(list.isEmpty()).isFalse();
        assertThat(list.get(0)).isNull();
        assertThat(list.subList(0, 1)).isEmpty();
    }

    /**
     * Stubbing: tell the mock what to return when a method is called with specific arguments.
     * Verification: check the amount of invocations of the mock for specific arguments
     */
    @Test
    void stubbing_returnsTheConfiguredValueForTheConfiguredArgument() {
        List<String> list = Mockito.mock(List.class);
        Mockito.when(list.get(0)).thenReturn("first");

        assertThat(list.get(0)).isEqualTo("first");
        assertThat(list.get(1)).isNull(); // not stubbed for argument 1

        Mockito.when(list.get(1)).thenReturn("second");
        assertThat(list.get(1)).isEqualTo("second");

        Mockito.verify(list, Mockito.times(3)).get(Mockito.anyInt());
        Mockito.verify(list, Mockito.times(1)).get(0);
        Mockito.verify(list, Mockito.times(2)).get(1);
    }

    /**
     * A method can return a different value on every call. The last value is repeated from then on.
     */
    @Test
    void stubbing_consecutiveCallsCanReturnDifferentValues() {
        Iterator<String> iterator = Mockito.mock(Iterator.class);
        Mockito.when(iterator.hasNext()).thenReturn(true, true, false);
        Mockito.when(iterator.next()).thenReturn("a").thenReturn("b").thenThrow(new NoSuchElementException());

        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).isEqualTo("a");
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).isEqualTo("b");
        assertThat(iterator.hasNext()).isFalse();
        assertThatThrownBy(iterator::next).isInstanceOf(NoSuchElementException.class);
    }

    /**
     * Mocks can also throw exceptions, which is a great way to trigger situations that are hard to create for real.
     */
    @Test
    void stubbing_aMockCanThrowAnException() {
        List<String> list = Mockito.mock(List.class);
        Mockito.when(list.get(5)).thenThrow(new IndexOutOfBoundsException("no such element"));

        assertThatThrownBy(() -> list.get(5))
                .isInstanceOf(IndexOutOfBoundsException.class)
                .hasMessage("no such element");
    }

    /**
     * For methods that return void you cannot use when(...), use the do...() variant instead.
     */
    @Test
    void stubbing_voidMethodsAreStubbedWithDoThrow() {
        List<String> list = Mockito.mock(List.class);
        Mockito.doThrow(new UnsupportedOperationException("read only")).when(list).clear();

        assertThatThrownBy(list::clear).isInstanceOf(UnsupportedOperationException.class);
    }

    /**
     * An answer calculates the return value from the arguments of the call.
     */
    @Test
    void stubbing_anAnswerCanUseTheArgumentsOfTheCall() {
        List<String> list = Mockito.mock(List.class);
        Mockito.when(list.get(Mockito.anyInt())).thenAnswer(invocation -> "item-" + invocation.getArgument(0));

        assertThat(list.get(3)).isEqualTo("item-3");
        assertThat(list.get(7)).isEqualTo("item-7");
    }

    // ---------------------------------------------------------------- argument matchers

    /**
     * Argument matchers stub (or verify) a call for a whole group of arguments instead of one exact value.
     */
    @Test
    void argumentMatchers_matchAGroupOfArguments() {
        Map<String, Integer> map = Mockito.mock(Map.class);
        Mockito.when(map.get(Mockito.anyString())).thenReturn(42);

        assertThat(map.get("x")).isEqualTo(42);
        assertThat(map.get("anything else")).isEqualTo(42);
    }

    /**
     * Rule: when one argument uses a matcher, all arguments must use a matcher. Use eq() to match an exact value.
     */
    @Test
    void argumentMatchers_mustBeUsedForAllArgumentsOfACall() {
        Map<String, Integer> map = Mockito.mock(Map.class);
        Mockito.when(map.getOrDefault(Mockito.eq("key"), Mockito.anyInt())).thenReturn(1);

        assertThat(map.getOrDefault("key", 0)).isEqualTo(1);
        assertThat(map.getOrDefault("other", 0)).isNull(); // "other" does not match eq("key")
    }

    // ---------------------------------------------------------------- verifying

    /**
     * Verifying checks which calls were made on a mock. By default exactly one call is expected.
     */
    @Test
    void verify_checksThatAMethodWasCalled() {
        List<String> list = Mockito.mock(List.class);

        list.add("one");

        Mockito.verify(list).add("one");
    }

    @Test
    void verify_checksTheNumberOfCalls() {
        List<String> list = Mockito.mock(List.class);

        list.add("one");
        list.add("one");
        list.add("two");

        Mockito.verify(list, Mockito.times(2)).add("one");
        Mockito.verify(list, Mockito.times(1)).add("two");
        Mockito.verify(list, Mockito.never()).add("three");
        Mockito.verify(list, Mockito.atLeastOnce()).add(Mockito.anyString());
        Mockito.verify(list, Mockito.times(3)).add(Mockito.anyString());
    }

    @Test
    void verify_checksThatNothingElseHappenedOnTheMock() {
        List<String> list = Mockito.mock(List.class);
        list.add("one");

        Mockito.verify(list).add("one");
        Mockito.verifyNoMoreInteractions(list);
    }

    /**
     * An argument captor catches the argument of a call, so you can assert on it afterwards.
     */
    @Test
    void argumentCaptor_catchesTheArgumentsThatWerePassed() {
        List<String> list = Mockito.mock(List.class);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        list.add("one");
        list.add("two");

        Mockito.verify(list, Mockito.times(2)).add(captor.capture());
        assertThat(captor.getAllValues()).containsExactly("one", "two");
    }

    // ---------------------------------------------------------------- spies

    /**
     * A spy wraps a real object: methods do what they normally do, unless you stub them. Calls are recorded just
     * like on a mock, so they can be verified.
     */
    @Test
    void spy_callsTheRealMethodsAndRecordsTheCalls() {
        List<String> spy = Mockito.spy(new ArrayList<>());

        spy.add("one");
        spy.add("two");

        assertThat(spy).containsExactly("one", "two"); // real behaviour
        assertThat(spy.size()).isEqualTo(2);
        Mockito.verify(spy).add("one");
    }

    /**
     * A spy can be partially stubbed. Use doReturn(...).when(spy) instead of when(spy...): the latter calls the real
     * method while stubbing, which would throw an IndexOutOfBoundsException here because the list is empty.
     */
    @Test
    void spy_canBePartiallyStubbed() {
        List<String> spy = Mockito.spy(new ArrayList<>());
        spy.add("one");

        Mockito.doReturn(100).when(spy).size();

        assertThat(spy.size()).isEqualTo(100); // stubbed
        assertThat(spy.get(0)).isEqualTo("one"); // still real
    }

    /**
     * The difference between a mock and a spy in one test.
     */
    @Test
    void mockVersusSpy_aMockForgetsEverythingAndASpyKeepsTheRealBehaviour() {
        List<String> mock = Mockito.mock(List.class);
        List<String> spy = Mockito.spy(new ArrayList<>());

        mock.add("one");
        spy.add("one");

        assertThat(mock.size()).isZero(); // the add() call did nothing
        assertThat(spy.size()).isEqualTo(1); // the add() call really added the element
        assertThat(Mockito.mockingDetails(mock).isSpy()).isFalse();
        assertThat(Mockito.mockingDetails(spy).isSpy()).isTrue();
    }
}
