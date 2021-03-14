package edu.caltech.cs2.sorts;

import edu.caltech.cs2.helpers.Inspection;
import edu.caltech.cs2.interfaces.IPriorityQueue;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TopKSortTests {
    private static String STRING_SOURCE = "src/edu/caltech/cs2/sorts/TopKSort.java";

    @Order(0)
    @DisplayName("Does not use or import disallowed classes")
    @Test
    @Tag("C")
    public void testForInvalidClasses() {
        List<String> regexps = List.of("java.util", "java.lang.reflect", "java.io");
        Inspection.assertNoImportsOf(STRING_SOURCE, regexps);
        Inspection.assertNoUsageOf(STRING_SOURCE, regexps);
    }

    @Order(0)
    @DisplayName("Does not use or import disallowed classes from java.util")
    @Test
    @Tag("C")
    public void testForInvalidClassesUtil() {
        List<String> allowed = List.of("Iterator");
        Inspection.assertNoImportsOfExcept(STRING_SOURCE, "java\\.util", allowed);

        List<String> bannedUsages = List.of("java\\.util\\.(?!" + String.join("|", allowed) + ")");
        Inspection.assertNoUsageOf(STRING_SOURCE, bannedUsages);
    }

    @Test
    @Tag("C")
    @Order(1)
    @DisplayName("Sorting an empty array does not fail")
    public void testEmptyArray() {
        IPriorityQueue.PQElement<Integer>[] array = new IPriorityQueue.PQElement[0];
        TopKSort.sort(array, 1);
        assertArrayEquals(new Integer[0], array);
    }

    @Test
    @Tag("C")
    @Order(2)
    @DisplayName("K < 0 throws an exception")
    public void testNegativeK() {
        IPriorityQueue.PQElement<Integer>[] array = new IPriorityQueue.PQElement[0];
        assertThrows(IllegalArgumentException.class, () -> {
            TopKSort.sort(array, -1);
        });
    }

    @Test
    @Tag("C")
    @Order(3)
    @DisplayName("K = 0 does not change the array")
    public void testZeroK() {
        IPriorityQueue.PQElement<Integer>[] array = new IPriorityQueue.PQElement[]{
                new IPriorityQueue.PQElement<>(1, 1), new IPriorityQueue.PQElement<>(2, 2),
                new IPriorityQueue.PQElement<>(3, 3), new IPriorityQueue.PQElement<>(4, 4),
                new IPriorityQueue.PQElement<>(5, 5)
        };
        TopKSort.sort(array, 0);
        Integer[] correct = new Integer[5];
        assertArrayEquals(correct, array);
    }

    @Tag("C")
    @Order(4)
    @ParameterizedTest(name = "Stress test TopKSort: size: {1}, k: {2}")
    @CsvSource({"42, 3000, 2000", "15, 5000, 1235", "20, 1000, 50"})
    @DisplayName("Stress test TopKSort")
    public void stressTest(int seed, int size, int k) {
        Comparator<IPriorityQueue.PQElement<Integer>> c = Comparator.comparingDouble(x -> x.priority);
        Random r = new Random(seed);
        Integer[] intarray = r.ints(size).boxed().toArray(Integer[]::new);
        Double[] doublearray = Arrays.stream(intarray).map(Double::valueOf).toArray(Double[]::new);
        IPriorityQueue.PQElement<Integer>[] array = new IPriorityQueue.PQElement[intarray.length];
        for (int i = 0; i < intarray.length; i++) {
            array[i] = new IPriorityQueue.PQElement<>(intarray[i], doublearray[i]);
        }
        IPriorityQueue.PQElement<Integer>[] sortedArray = array.clone();
        Arrays.sort(sortedArray, c);
        IPriorityQueue.PQElement<Integer>[] correct = new IPriorityQueue.PQElement[size];
        for (int i = 0; i < correct.length; i++) {
            if (i < k) {
                correct[i] = sortedArray[sortedArray.length - i - 1];
            } else {
                correct[i] = null;
            }
        }
        TopKSort.sort(array, k);
        assertArrayEquals(correct, array);
    }
}
