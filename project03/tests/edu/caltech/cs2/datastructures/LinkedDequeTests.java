package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.helpers.Inspection;
import edu.caltech.cs2.helpers.NodeChecker;
import edu.caltech.cs2.helpers.Reflection;
import edu.caltech.cs2.helpers.RuntimeInstrumentation;
import edu.caltech.cs2.interfaces.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.ArrayDeque;
import java.util.function.Consumer;
import java.util.function.Function;

import static edu.caltech.cs2.project03.Project03TestOrdering.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Tag("C")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LinkedDequeTests implements IDequeTests, IStackTests, IQueueTests {
  private static String LINKED_DEQUE_SOURCE = "src/edu/caltech/cs2/datastructures/LinkedDeque.java";

  private Constructor linkedDequeConstructor = Reflection.getConstructor(LinkedDeque.class);

  public ICollection<Object> newCollection() {
    return Reflection.newInstance(linkedDequeConstructor);
  }

  public IDeque<Object> newDeque() {
    return Reflection.newInstance(linkedDequeConstructor);
  }

  public IStack<Object> newStack() {
    return Reflection.newInstance(linkedDequeConstructor);
  }

  public IQueue<Object> newQueue() {
    return Reflection.newInstance(linkedDequeConstructor);
  }

  public IQueue<Object> newQueue(int size) {
    return newQueue();
  }

  // LINKEDDEQUE-SPECIFIC TESTS ----------------------------------------

  @Order(classSpecificTestLevel)
  @DisplayName("Does not use or import disallowed classes")
  @Test
  public void testForInvalidClasses() {
    List<String> regexps = List.of("java\\.util\\.(?!Iterator)", "java\\.lang\\.reflect", "java\\.io");
    Inspection.assertNoImportsOf(LINKED_DEQUE_SOURCE, regexps);
    Inspection.assertNoUsageOf(LINKED_DEQUE_SOURCE, regexps);
  }

  @Order(classSpecificTestLevel)
  @DisplayName("There are no static fields")
  @Test
  public void testConstantFields() {
    Reflection.assertFieldsEqualTo(LinkedDeque.class, "static", 0);
  }

  @Order(classSpecificTestLevel)
  @DisplayName("The overall number of fields is small")
  @Test
  public void testSmallNumberOfFields() {
    Reflection.assertFieldsLessThan(LinkedDeque.class, "private", 4);
  }

  @Order(classSpecificTestLevel)
  @DisplayName("There are no public fields")
  @Test
  public void testNoPublicFields() {
    Reflection.assertNoPublicFields(LinkedDeque.class);
  }

  @Order(classSpecificTestLevel)
  @DisplayName("There are no protected fields")
  @Test
  public void testNoProtectedFields() {
    Reflection.assertNoProtectedFields(LinkedDeque.class);
  }

  @Order(classSpecificTestLevel)
  @DisplayName("All fields in LinkedDeque have modifiers")
  @Test
  public void testFieldModifiers() {
    Reflection.assertFieldModifiers(LinkedDeque.class);
  }

  @Order(classSpecificTestLevel)
  @DisplayName("The public interface is correct")
  @Test
  public void testPublicInterface() {
    Reflection.assertPublicInterface(LinkedDeque.class, List.of("addFront", "addBack", "removeFront", "removeBack",
        "enqueue", "dequeue", "push", "pop", "peek", "peekFront", "peekBack", "iterator", "size", "toString"));
  }

  @Order(classSpecificTestLevel)
  @DisplayName("Uses this(...) notation in all but one constructor")
  @Test
  public void testForThisConstructors() {
    Inspection.assertConstructorHygiene(LINKED_DEQUE_SOURCE);
  }

  @Order(classSpecificTestLevel)
  @DisplayName("Check that LinkedDeque uses a node class")
  @Test
  public void testLinkedNode() {
    Class[] classes = LinkedDeque.class.getDeclaredClasses();
    for (Class clazz : classes) {
      if (Iterator.class.isAssignableFrom(clazz)) {
        continue;
      }
      NodeChecker.isNode(clazz, true);
    }
  }

  // TOSTRING TESTS ---------------------------------------------------

  @Order(toStringTestLevel)
  @DisplayName("toString is correctly overridden")
  @Test
  public void testToStringOverride() {
    Reflection.assertMethodCorrectlyOverridden(LinkedDeque.class, "toString");
  }

  @Order(toStringTestLevel)
  @DisplayName("toString() matches java.util.ArrayDeque")
  @ParameterizedTest(name = "Test toString() on [{arguments}]")
  @ValueSource(strings = { "0, 1, 2, 3", "5, 4, 3, 2, 1", "8, 3, 5, 7, 4, 3, 12, 12, 1" })
  public void testToString(String inputs) {
    java.util.ArrayDeque<String> reference = new java.util.ArrayDeque<String>();
    LinkedDeque<String> me = new LinkedDeque<>();
    for (String value : inputs.trim().split(", ")) {
      assertEquals(reference.toString(), me.toString(), "toString outputs should be the same");
      reference.addLast(value);
      me.addBack(value);
    }
  }

  // TIME COMPLEXITY TESTS ------------------------------------------------

  @Order(complexityTestLevel)
  @DisplayName("addFront() and removeFront() take constant time")
  @Timeout(value = 10, unit = SECONDS)
  @Test
  public void testFrontDequeOperationComplexity() {
    Function<Integer, IDeque<Integer>> provide = (Integer numElements) -> {
      IDeque<Integer> q = new LinkedDeque<>();
      for (int i = 0; i < numElements; i++) {
        q.addFront(i);
      }
      return q;
    };
    Consumer<IDeque<Integer>> addFront = (IDeque<Integer> q) -> q.addFront(0);
    Consumer<IDeque<Integer>> removeFront = (IDeque<Integer> q) -> q.removeFront();

    RuntimeInstrumentation.assertAtMost("addFront", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, addFront,
        8);
    RuntimeInstrumentation.assertAtMost("removeFront", RuntimeInstrumentation.ComplexityType.CONSTANT, provide,
        removeFront, 8);
  }

  @Order(complexityTestLevel)
  @DisplayName("addBack() and removeBack() take constant time")
  @Timeout(value = 10, unit = SECONDS)
  @Test
  public void testBackDequeOperationComplexity() {
    Function<Integer, IDeque<Integer>> provide = (Integer numElements) -> {
      IDeque<Integer> q = new LinkedDeque<>();
      for (int i = 0; i < numElements; i++) {
        q.addBack(i);
      }
      return q;
    };
    Consumer<IDeque<Integer>> addBack = (IDeque<Integer> q) -> q.addBack(0);
    Consumer<IDeque<Integer>> removeBack = (IDeque<Integer> q) -> q.removeBack();

    RuntimeInstrumentation.assertAtMost("addBack", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, addBack, 8);
    RuntimeInstrumentation.assertAtMost("removeBack", RuntimeInstrumentation.ComplexityType.CONSTANT, provide,
        removeBack, 8);
  }

  @Order(complexityTestLevel)
  @DisplayName("enqueue() and dequeue() take constant time")
  @Timeout(value = 10, unit = SECONDS)
  @Test
  public void testQueueOperationComplexity() {
    Function<Integer, IQueue<Integer>> provide = (Integer numElements) -> {
      IQueue<Integer> q = new LinkedDeque<>();
      for (int i = 0; i < numElements; i++) {
        q.enqueue(i);
      }
      return q;
    };
    Consumer<IQueue<Integer>> enqueue = (IQueue<Integer> q) -> q.enqueue(0);
    Consumer<IQueue<Integer>> dequeue = (IQueue<Integer> q) -> q.dequeue();

    RuntimeInstrumentation.assertAtMost("enqueue", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, enqueue, 8);
    RuntimeInstrumentation.assertAtMost("dequeue", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, dequeue, 8);
  }

  @Order(complexityTestLevel)
  @DisplayName("push() and pop() take constant time")
  @Timeout(value = 10, unit = SECONDS)
  @Test
  public void testStackOperationComplexity() {
    Function<Integer, IStack<Integer>> provide = (Integer numElements) -> {
      IStack<Integer> q = new LinkedDeque<>();
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
      IStack<Integer> q = new LinkedDeque<>();
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
  @Test
  public void testPeekFrontComplexity() {
    Function<Integer, IDeque<Integer>> provide = (Integer numElements) -> {
      IDeque<Integer> q = new LinkedDeque<>();
      for (int i = 0; i < numElements; i++) {
        q.addFront(i);
      }
      return q;
    };
    Consumer<IDeque<Integer>> peekFront = (IDeque<Integer> q) -> q.peekFront();

    RuntimeInstrumentation.assertAtMost("peekFront", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, peekFront,
        8);
  }

  @Order(complexityTestLevel)
  @DisplayName("peekBack() takes constant time")
  @Timeout(value = 10, unit = SECONDS)
  @Test
  public void testPeekBackComplexity() {
    Function<Integer, IDeque<Integer>> provide = (Integer numElements) -> {
      IDeque<Integer> q = new LinkedDeque<>();
      for (int i = 0; i < numElements; i++) {
        q.addBack(i);
      }
      return q;
    };
    Consumer<IDeque<Integer>> peekBack = (IDeque<Integer> q) -> q.peekBack();

    RuntimeInstrumentation.assertAtMost("peekBack", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, peekBack,
        8);
  }

  // "LINKED-NESS" TESTS ------------------------------------------------

  @Order(dequeTestLevel)
  @DisplayName("Cycle detection for addFront(...), addBack(...), removeFront(...), and removeBack(...)")
  @ParameterizedTest(name = "Test cycles - {1} random numbers with seed = {0}")
  @CsvSource({ "69, 2000", "20, 3000" })
  public void checkForCycles(int seed, int size) {
    Random r = new Random(seed);
    Deque<Object> reference = new ArrayDeque<>();
    IDeque<Object> impl = new LinkedDeque<>();
    // Test that first peek is null
    assertNull(impl.peekFront(), "empty peek should return null");
    // Randomly add / remove elements to the front / back
    for (int i = 0; i < size; i++) {
      int num = r.nextInt();
      if (num % 2 == 0) {
        reference.addLast(num);
        impl.addBack(num);
      } else {
        reference.addFirst(num);
        impl.addFront(num);
      }
      if (reference.size() > 1 && impl.size() > 1) {
        if (num % 5 == 0) {
          reference.removeFirst();
          impl.removeFront();
        } else if (num % 7 == 0) {
          reference.removeLast();
          impl.removeBack();
        }
      }
      // After each operation, check whether cycles have formed
      NodeChecker.cycleDetection(impl, true);
      // Sanity checks, though these aren't super necessary
      assertEquals(reference.size(), impl.size(), "size()s are not equal");
      assertEquals(reference.toString(), impl.toString(), "toStrings()s are not equal");
    }
  }

  @Order(dequeTestLevel)
  @DisplayName("Check reverses for addFront(...), addBack(...), removeFront(...), and removeBack(...)")
  @ParameterizedTest(name = "Test reverse - {1} random numbers with seed = {0}")
  @CsvSource({ "31, 2000", "64, 3000" })
  public void checkReverses(int seed, int size) {
    Random r = new Random(seed);
    Deque<Object> reference = new ArrayDeque<>();
    IDeque<Object> impl = new LinkedDeque<>();
    // Test that first peek is null
    assertNull(impl.peekFront(), "empty peek should return null");
    // Randomly add / remove elements to the front / back
    for (int i = 0; i < size; i++) {
      int num = r.nextInt();
      if (num % 2 == 0) {
        reference.addLast(num);
        impl.addBack(num);
      } else {
        reference.addFirst(num);
        impl.addFront(num);
      }
      if (reference.size() > 1 && impl.size() > 1) {
        if (num % 5 == 0) {
          reference.removeFirst();
          impl.removeFront();
        } else if (num % 7 == 0) {
          reference.removeLast();
          impl.removeBack();
        }
      }
      // Check that forwards and backwards iteration are sane
      NodeChecker.checkReverse(impl);
      assertEquals(reference.size(), impl.size(), "size()s are not equal");
      assertEquals(reference.toString(), impl.toString(), "toStrings()s are not equal");
    }
  }

}