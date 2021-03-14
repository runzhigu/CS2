package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.helpers.Inspection;
import edu.caltech.cs2.helpers.Reflection;
import edu.caltech.cs2.helpers.RuntimeInstrumentation;
import edu.caltech.cs2.interfaces.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static edu.caltech.cs2.project03.Project03TestOrdering.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("C")
public class ArrayDequeTests implements IDequeTests, IStackTests, IQueueTests {
  private static String ARRAY_DEQUE_SOURCE ="src/edu/caltech/cs2/datastructures/ArrayDeque.java";

  private Constructor arrayDequeConstructor = Reflection.getConstructor(ArrayDeque.class);

  public ICollection<Object> newCollection() {
    return Reflection.newInstance(arrayDequeConstructor);
  }

  public IDeque<Object> newDeque() {
    return Reflection.newInstance(arrayDequeConstructor);
  }

  public IStack<Object> newStack() {
    return Reflection.newInstance(arrayDequeConstructor);
  }

  public IQueue<Object> newQueue() {
      return Reflection.newInstance(arrayDequeConstructor);
  }

  public IQueue<Object> newQueue(int size) {
    return newQueue();
  }

  // ARRAYDEQUE-SPECIFIC TESTS ----------------------------------------

  @Order(classSpecificTestLevel)
  @DisplayName("Does not use or import disallowed classes")
  @Test
  public void testForInvalidClasses() {
    List<String> regexps = List.of("java\\.util\\.(?!Iterator)", "java\\.lang\\.reflect", "java\\.io");
    Inspection.assertNoImportsOf(ARRAY_DEQUE_SOURCE, regexps);
    Inspection.assertNoUsageOf(ARRAY_DEQUE_SOURCE, regexps);
  }

  @Order(classSpecificTestLevel)
  @DisplayName("There is an integer default capacity static field and an integer default grow factor static field")
  @Test
  public void testConstantFields() {
    Reflection.assertFieldsEqualTo(ArrayDeque.class, "static", 2);
    Stream<Field> fields = Reflection.getFields(ArrayDeque.class);
    fields.filter(Reflection.hasModifier("static")).forEach((field) -> {
      Reflection.checkFieldModifiers(field, List.of("private", "static", "final"));
      assertEquals(int.class, field.getType(), "static fields must be of type int");
    });
  }

  @Order(classSpecificTestLevel)
  @DisplayName("The overall number of fields is small")
  @Test
  public void testSmallNumberOfFields() {
    Reflection.assertFieldsLessThan(ArrayDeque.class, "private", 5);
  }

  @Order(classSpecificTestLevel)
  @DisplayName("There are no public fields")
  @Test
  public void testNoPublicFields() {
    Reflection.assertNoPublicFields(ArrayDeque.class);
  }

  @Order(classSpecificTestLevel)
  @DisplayName("There are no protected fields")
  @Test
  public void testNoProtectedFields() {
    Reflection.assertNoProtectedFields(ArrayDeque.class);
  }

  @Order(classSpecificTestLevel)
  @DisplayName("All fields in ArrayDeque have modifiers")
  @Test
  public void testFieldModifiers() {
    Reflection.assertFieldModifiers(ArrayDeque.class);
  }

  @Order(classSpecificTestLevel)
  @DisplayName("The public interface is correct")
  @Test
  public void testPublicInterface() {
    Reflection.assertPublicInterface(ArrayDeque.class, List.of(
            "addFront",
            "addBack",
            "removeFront",
            "removeBack",
            "enqueue",
            "dequeue",
            "push",
            "pop",
            "peek",
            "peekFront",
            "peekBack",
            "iterator",
            "size",
            "toString"
    ));
  }

  @Order(classSpecificTestLevel)
  @DisplayName("Uses this(...) notation in all but one constructor")
  @Test
  public void testForThisConstructors() {
    Inspection.assertConstructorHygiene(ARRAY_DEQUE_SOURCE);
  }

  // ARRAYDEQUE TESTS ---------------------------------------------------

  @Order(implSpecificTestLevel)
  @DisplayName("The default capacity of the array is 10")
  @Test
  public void testArrayDequeDefaultInitialCapacity() throws IllegalAccessException {
    ArrayDeque<Integer> impl = new ArrayDeque<>();

    // Reflect and get the backing array
    // It's actually an Object[] since that's how it (should!) be initialized internally
    // Casting it doesn't change the type of the field.
    // It's fine since there should only be one array.
    Field arr = Reflection.getFieldByType(ArrayDeque.class, Object[].class);
    arr.setAccessible(true);
    Object[] backingArray = (Object[]) arr.get(impl);

    assertEquals(10, backingArray.length, "Default initial capacity is not 10");
  }

  @Order(implSpecificTestLevel)
  @DisplayName("enqueue should always succeed")
  @Test
  public void testThatArrayDequeEnqueueAlwaysSucceeds() {
    ArrayDeque<Integer> impl = new ArrayDeque<>();
    for (int i = 0; i < 100; i ++) {
      assertTrue(impl.enqueue(i), "enqueue() should always succeed for ArrayDeque");
    }
  }

  @Order(implSpecificTestLevel)
  @DisplayName("push should always succeed")
  @Test
  public void testThatArrayDequePushAlwaysSucceeds() {
    ArrayDeque<Integer> impl = new ArrayDeque<>();
    for (int i = 0; i < 100; i ++) {
      assertTrue(impl.push(i), "push() should always succeed for ArrayDeque");
    }
  }

  // TOSTRING TESTS ---------------------------------------------------

  @Order(toStringTestLevel)
  @DisplayName("toString is correctly overridden")
  @Test
  public void testToStringOverride() {
    Reflection.assertMethodCorrectlyOverridden(ArrayDeque.class, "toString");
  }

  @Order(toStringTestLevel)
  @DisplayName("toString() matches java.util.ArrayDeque")
  @ParameterizedTest(name = "Test toString() on [{arguments}]")
  @ValueSource(strings = {
          "0, 1, 2, 3", "5, 4, 3, 2, 1", "8, 3, 5, 7, 4, 3, 12, 12, 1"
  })
  public void testToString(String inputs) {
    java.util.ArrayDeque<String> reference = new java.util.ArrayDeque<String>();
    edu.caltech.cs2.datastructures.ArrayDeque<String> me = new edu.caltech.cs2.datastructures.ArrayDeque<>();
    for (String value : inputs.trim().split(", ")) {
      assertEquals(reference.toString(), me.toString(), "toString outputs should be the same");
      reference.addLast(value);
      me.addBack(value);
    }
  }

  // TIME COMPLEXITY TESTS ------------------------------------------------

  @Order(complexityTestLevel)
  @DisplayName("addFront() and removeFront() take linear time")
  @Timeout(value = 20, unit = SECONDS)
  @Test()
  public void testFrontDequeOperationComplexity() {
    Function<Integer, IDeque<Integer>> provide = (Integer numElements) -> {
      IDeque<Integer> q = new ArrayDeque<>();
      for (int i = 0; i < numElements; i++) {
        q.addFront(i);
      }
      return q;
    };
    Consumer<IDeque<Integer>> addFront = (IDeque<Integer> q) -> q.addFront(0);
    Consumer<IDeque<Integer>> removeFront = (IDeque<Integer> q) -> q.removeFront();

    RuntimeInstrumentation.assertAtMost("addFront", RuntimeInstrumentation.ComplexityType.LINEAR, provide, addFront, 8);
    RuntimeInstrumentation.assertAtMost("removeFront", RuntimeInstrumentation.ComplexityType.LINEAR, provide, removeFront, 8);
  }

  @Order(complexityTestLevel)
  @DisplayName("addBack() and removeBack() take constant time")
  @Timeout(value = 20, unit = SECONDS)
  @Test
  public void testBackDequeOperationComplexity() {
    Function<Integer, IDeque<Integer>> provide = (Integer numElements) -> {
      IDeque<Integer> q = new ArrayDeque<>();
      for (int i = 0; i < numElements; i++) {
        q.addBack(i);
      }
      return q;
    };
    Consumer<IDeque<Integer>> addBack = (IDeque<Integer> q) -> q.addBack(0);
    Consumer<IDeque<Integer>> removeBack = (IDeque<Integer> q) -> q.removeBack();

    RuntimeInstrumentation.assertAtMost("addBack", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, addBack, 8);
    RuntimeInstrumentation.assertAtMost("removeBack", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, removeBack, 8);
  }

  @Order(complexityTestLevel)
  @DisplayName("enqueue() and dequeue() take linear time")
  @Timeout(value = 20, unit = SECONDS)
  @Test
  public void testQueueOperationComplexity() {
    Function<Integer, IQueue<Integer>> provide = (Integer numElements) -> {
      IQueue<Integer> q = new ArrayDeque<>();
      for (int i = 0; i < numElements; i++) {
        q.enqueue(i);
      }
      return q;
    };
    Consumer<IQueue<Integer>> enqueue = (IQueue<Integer> q) -> q.enqueue(0);
    Consumer<IQueue<Integer>> dequeue = (IQueue<Integer> q) -> q.dequeue();

    RuntimeInstrumentation.assertAtMost("enqueue", RuntimeInstrumentation.ComplexityType.LINEAR, provide, enqueue, 8);
    RuntimeInstrumentation.assertAtMost("dequeue", RuntimeInstrumentation.ComplexityType.LINEAR, provide, dequeue, 8);
  }

  @Order(complexityTestLevel)
  @DisplayName("push() and pop() take constant time")
  @Timeout(value = 10, unit = SECONDS)
  @Test
  public void testStackOperationComplexity() {
    Function<Integer, IStack<Integer>> provide = (Integer numElements) -> {
      IStack<Integer> q = new ArrayDeque<>();
      for (int i = 0; i < numElements; i++) {
        q.push(i);
      }
      return q;
    };
    Consumer<IStack<Integer>> push = (IStack<Integer> q) -> q.push(0);
    Consumer<IStack<Integer>> pop = (IStack<Integer> q) -> q.pop();

    RuntimeInstrumentation.assertAtMost("push", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, push, 8);
    RuntimeInstrumentation.assertAtMost("pop", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, pop, 8);
  }

  @Order(complexityTestLevel)
  @DisplayName("peek() takes constant time")
  @Timeout(value = 10, unit = SECONDS)
  @Test
  public void testPeekComplexity() {
    Function<Integer, IStack<Integer>> provide = (Integer numElements) -> {
      IStack<Integer> q = new ArrayDeque<>();
      for (int i = 0; i < numElements; i++) {
        q.push(i);
      }
      return q;
    };
    Consumer<IStack<Integer>> peek = (IStack<Integer> q) -> q.peek();

    RuntimeInstrumentation.assertAtMost("peek", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, peek, 8);
  }

  @Order(complexityTestLevel)
  @DisplayName("peekFront() takes constant time")
  @Timeout(value = 10, unit = SECONDS)
  @Test()
  public void testPeekFrontComplexity() {
    Function<Integer, IDeque<Integer>> provide = (Integer numElements) -> {
      IDeque<Integer> q = new ArrayDeque<>();
      for (int i = 0; i < numElements; i++) {
        q.addFront(i);
      }
      return q;
    };
    Consumer<IDeque<Integer>> peekFront = (IDeque<Integer> q) -> q.peekFront();

    RuntimeInstrumentation.assertAtMost("peekFront", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, peekFront, 8);
  }

  @Order(complexityTestLevel)
  @DisplayName("peekBack() takes constant time")
  @Timeout(value = 10, unit = SECONDS)
  @Test
  public void testPeekBackComplexity() {
    Function<Integer, IDeque<Integer>> provide = (Integer numElements) -> {
      IDeque<Integer> q = new ArrayDeque<>();
      for (int i = 0; i < numElements; i++) {
        q.addBack(i);
      }
      return q;
    };
    Consumer<IDeque<Integer>> peekBack = (IDeque<Integer> q) -> q.peekBack();

    RuntimeInstrumentation.assertAtMost("peekBack", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, peekBack, 8);
  }

}