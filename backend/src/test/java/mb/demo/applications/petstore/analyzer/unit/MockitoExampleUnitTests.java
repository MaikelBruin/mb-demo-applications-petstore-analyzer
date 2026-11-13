package mb.demo.applications.petstore.analyzer.unit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


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
        List<String> mockedList = Mockito.mock(List.class);

        Assertions.assertThat(mockedList.size()).isZero();
        Assertions.assertThat(mockedList.isEmpty()).isFalse();
        Assertions.assertThat(mockedList.get(0)).isNull();
        Assertions.assertThat(mockedList.subList(0, 3)).isEmpty();
    }

    @Test
    void mock_doesNotExecuteActualMethods() {
        List<String> realList = new ArrayList<>();
        realList.add("foo");
        realList.add("bar");
        Assertions.assertThat(realList.size()).isEqualTo(2);

        List<String> mockedList = Mockito.mock(List.class);
        mockedList.add("foo");
        mockedList.add("bar");
        Assertions.assertThat(mockedList.size()).isZero();
    }

    /**
     * Stubbing: tell the mock what to return when a method is called with specific arguments.
     * Verification: check the amount of invocations of the mock for specific arguments
     */
    @Test
    void stubbing_returnsTheConfiguredValueForTheConfiguredArgument() {
        List<String> list = Mockito.mock(List.class);
        Mockito.when(list.get(0)).thenReturn("first");

        Assertions.assertThat(list.get(0)).isEqualTo("first");
        Assertions.assertThat(list.get(1)).isNull(); // not stubbed for argument 1

        Mockito.when(list.get(1)).thenReturn("second");
        Assertions.assertThat(list.get(1)).isEqualTo("second");

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

        Assertions.assertThat(iterator.hasNext()).isTrue();
        Assertions.assertThat(iterator.next()).isEqualTo("a");
        Assertions.assertThat(iterator.hasNext()).isTrue();
        Assertions.assertThat(iterator.next()).isEqualTo("b");
        Assertions.assertThat(iterator.hasNext()).isFalse();
        Assertions.assertThatThrownBy(iterator::next).isInstanceOf(NoSuchElementException.class);
    }

    /**
     * Mocks can also throw exceptions, which is a great way to trigger situations that are hard to create for real.
     */
    @Test
    void stubbing_aMockCanThrowAnException() {
        List<String> list = Mockito.mock(List.class);
        Mockito.when(list.get(5)).thenThrow(new IndexOutOfBoundsException("no such element"));

        Assertions.assertThatThrownBy(() -> list.get(5))
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

        Assertions.assertThatThrownBy(list::clear).isInstanceOf(UnsupportedOperationException.class);
    }

    /**
     * An answer calculates the return value from the arguments of the call.
     */
    @Test
    void stubbing_anAnswerCanUseTheArgumentsOfTheCall() {
        List<String> list = Mockito.mock(List.class);
        Mockito.when(list.get(Mockito.anyInt())).thenAnswer(invocation -> "item-" + invocation.getArgument(0));

        Assertions.assertThat(list.get(3)).isEqualTo("item-3");
        Assertions.assertThat(list.get(7)).isEqualTo("item-7");
    }

    // ---------------------------------------------------------------- argument matchers

    /**
     * Argument matchers stub (or verify) a call for a whole group of arguments instead of one exact value.
     */
    @Test
    void argumentMatchers_matchAGroupOfArguments() {
        Map<String, Integer> map = Mockito.mock(Map.class);
        Mockito.when(map.get(Mockito.anyString())).thenReturn(42);

        Assertions.assertThat(map.get("x")).isEqualTo(42);
        Assertions.assertThat(map.get("anything else")).isEqualTo(42);
    }

    /**
     * Rule: when one argument uses a matcher, all arguments must use a matcher. Use eq() to match an exact value.
     */
    @Test
    void argumentMatchers_mustBeUsedForAllArgumentsOfACall() {
        Map<String, Integer> map = Mockito.mock(Map.class);
        Mockito.when(map.getOrDefault(Mockito.eq("key"), Mockito.anyInt())).thenReturn(1);

        Assertions.assertThat(map.getOrDefault("key", 0)).isEqualTo(1);
        Assertions.assertThat(map.getOrDefault("other", 0)).isNull(); // "other" does not match eq("key")
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
        Assertions.assertThat(captor.getAllValues()).containsExactly("one", "two");
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

        Assertions.assertThat(spy).containsExactly("one", "two"); // real behaviour
        Assertions.assertThat(spy.size()).isEqualTo(2);
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

        Assertions.assertThat(spy.size()).isEqualTo(100); // stubbed
        Assertions.assertThat(spy.get(0)).isEqualTo("one"); // still real
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

        Assertions.assertThat(mock.size()).isZero(); // the add() call did nothing
        Assertions.assertThat(spy.size()).isEqualTo(1); // the add() call really added the element
        Assertions.assertThat(Mockito.mockingDetails(mock).isSpy()).isFalse();
        Assertions.assertThat(Mockito.mockingDetails(spy).isSpy()).isTrue();
    }
}
