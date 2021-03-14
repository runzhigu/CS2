package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.helpers.Inspection;
import edu.caltech.cs2.helpers.Reflection;

import java.util.*;
import java.util.ArrayList;

import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.IPriorityQueue;
import edu.caltech.cs2.misc.IntegerComparator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MinFourHeapTests {
    private static String STRING_SOURCE = "src/edu/caltech/cs2/datastructures/MinFourHeap.java";

    public void checkKeyToIndexMap(MinFourHeap<Integer> heap) {
        // Check keyToIndexMap
        IPriorityQueue.PQElement<Integer>[] heapData = Reflection.getFieldValue(MinFourHeap.class, "data", heap);
        IDictionary<Integer, Integer> indexMap = Reflection.getFieldValue(MinFourHeap.class, "keyToIndexMap",
                heap);
        assertEquals(heap.size(), indexMap.size(), "Heap size and keyToIndexMap sizes are different");

        // Reconstruct data from map
        Integer[] dataFromMap = new Integer[heap.size()];
        for (IDictionary.Entry<Integer, Integer> entry : indexMap.entrySet()) {
            assertTrue(entry.value < heap.size(), "Index in keyToIndexMap is larger than heap size");
            // If not null, then was set prior
            assertNull(dataFromMap[entry.value], "Index appears multiple times in keyToIndexMap");
            dataFromMap[entry.value] = entry.key;
        }

        // Only check data that's actually in the heap
        for (int i = 0; i < heap.size(); i++) {
            assertEquals(heapData[i].data, dataFromMap[i], "keyToIndexMap does not match heap data at index " + i);
        }
    }

    @Order(0)
    @DisplayName("Does not use or import disallowed classes")
    @Test
    @Tag("C")
    public void testForInvalidClasses() {
        List<String> regexps = List.of("java.lang.reflect", "java.io");
        Inspection.assertNoImportsOf(STRING_SOURCE, regexps);
        Inspection.assertNoUsageOf(STRING_SOURCE, regexps);
    }

    @Order(1)
    @DisplayName("Does not use or import disallowed classes from java.util")
    @Test
    @Tag("C")
    public void testForInvalidImportsJavaUtil() {
        List<String> allowed = List.of("Iterator");
        Inspection.assertNoImportsOfExcept(STRING_SOURCE, "java\\.util", allowed);

        List<String> bannedUsages = List.of("java\\.util\\.(?!" + String.join("|", allowed) + ")");
        Inspection.assertNoUsageOf(STRING_SOURCE, bannedUsages);
    }

    @Test
    @Tag("C")
    @Order(2)
    @DisplayName("The public interface is correct")
    public void testPublicInterface() {
        Reflection.assertPublicInterface(MinFourHeap.class,
                List.of("enqueue", "dequeue", "iterator", "decreaseKey", "increaseKey", "peek", "size"));
    }

    @Test
    @Tag("C")
    @Order(3)
    @DisplayName("Attempting to enqueue duplicate elements throws an exception")
    public void testDuplicateThrows() {
        MinFourHeap<Integer> heap = new MinFourHeap<>();
        heap.enqueue(new IPriorityQueue.PQElement<>(10, 10));
        assertThrows(IllegalArgumentException.class, () -> {
            heap.enqueue(new IPriorityQueue.PQElement<>(10, 10));
        });
    }

    @Test
    @Tag("C")
    @Order(3)
    @DisplayName("Attempting to modify the priority of a nonexistent element throws an exception")
    public void testChangeKeyNonexistentElem() {
        MinFourHeap<Integer> heap = new MinFourHeap<>();
        heap.enqueue(new IPriorityQueue.PQElement<>(10, 10));
        assertThrows(IllegalArgumentException.class, () -> {
            heap.increaseKey(new IPriorityQueue.PQElement<>(11, 11));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            heap.decreaseKey(new IPriorityQueue.PQElement<>(11, 11));
        });
    }

    @Test
    @Tag("C")
    @Order(4)
    @DisplayName("Smoke test enqueue while checking internal state of heap")
    public void testEnqueue() {
        // create heap
        MinFourHeap<Integer> heap = new MinFourHeap<>();
        List<Integer> values = new ArrayList<>(Arrays.asList(9, -100, 19, 3, -2, 1, 7, -84, -4, 2, 70));

        // step by step look at what heap internal data array should look like
        List<List<Integer>> step_by_step = new ArrayList<>();
        step_by_step.add(Arrays.asList(9));
        step_by_step.add(Arrays.asList(-100, 9));
        step_by_step.add(Arrays.asList(-100, 9, 19));
        step_by_step.add(Arrays.asList(-100, 9, 19, 3));
        step_by_step.add(Arrays.asList(-100, 9, 19, 3, -2));
        step_by_step.add(Arrays.asList(-100, 1, 19, 3, -2, 9));
        step_by_step.add(Arrays.asList(-100, 1, 19, 3, -2, 9, 7));
        step_by_step.add(Arrays.asList(-100, -84, 19, 3, -2, 9, 7, 1));
        step_by_step.add(Arrays.asList(-100, -84, 19, 3, -2, 9, 7, 1, -4));
        step_by_step.add(Arrays.asList(-100, -84, 2, 3, -2, 9, 7, 1, -4, 19));
        step_by_step.add(Arrays.asList(-100, -84, 2, 3, -2, 9, 7, 1, -4, 19, 70));

        // enqueue values while examining internal state
        for (int i = 0; i < values.size(); i++) {
            assertTrue(heap.enqueue(new IPriorityQueue.PQElement<>(values.get(i), values.get(i))));
            assertEquals(i + 1, heap.size());

            IPriorityQueue.PQElement<Integer>[] heapData = Reflection.getFieldValue(MinFourHeap.class, "data", heap);
            for (int j = 0; j < heap.size(); j++) {
                assertEquals(step_by_step.get(i).toArray()[j], heapData[j].data);
            }

            checkKeyToIndexMap(heap);
        }
    }

    @Test
    @Tag("C")
    @Order(4)
    @DisplayName("Smoke test dequeue while checking internal state of heap")
    public void testDequeue() {
        Comparator<Integer> c = new IntegerComparator();
        MinFourHeap<Integer> heap = new MinFourHeap<>();
        PriorityQueue<Integer> reference = new PriorityQueue<>(c);
        List<Integer> values = new ArrayList<>(Arrays.asList(9, -100, 19, 3, -2, 1, 7, -84, -4, 2, 70));
        for (int value : values) {
            assertTrue(heap.enqueue(new IPriorityQueue.PQElement<>(value, value)));
            reference.add(value);
        }
        for (int i = 0; i < reference.size(); i++) {
            assertEquals(reference.remove(), heap.dequeue().data);
            checkKeyToIndexMap(heap);
            assertEquals(reference.size(), heap.size());
        }
    }

    @Test
    @Tag("C")
    @Order(5)
    @DisplayName("Smoke test increaseKey while checking internal state of heap")
    public void testIncreaseKey() {
        // Build heap
        MinFourHeap<Integer> heap = new MinFourHeap<>();
        List<Integer> values = new ArrayList<>(Arrays.asList(9, -100, 19, 3, -2, 1, 7, -84, -4, 2, 70));
        for (Integer value : values) {
            assertTrue(heap.enqueue(new IPriorityQueue.PQElement<>(value, value)));
        }
        // Assert constructed heap is correct
        Integer[] correctHeapData = {-100, -84, 2, 3, -2, 9, 7, 1, -4, 19, 70};
        IPriorityQueue.PQElement<Integer>[] heapData = Reflection.getFieldValue(MinFourHeap.class, "data", heap);
        for (int j = 0; j < heap.size(); j++) {
            assertEquals(correctHeapData[j], heapData[j].data);
        }
        // Increase the root's priority
        heap.increaseKey(new IPriorityQueue.PQElement<>(-100, 100));

        // Verify the heap after moving is correct
        double[] correctHeapPrioritiesAfterIncrease = {-84, -4, 2, 3, -2, 9, 7, 1, 100, 19, 70};
        heapData = Reflection.getFieldValue(MinFourHeap.class, "data", heap);
        checkKeyToIndexMap(heap);
        for (int i = 0; i < heap.size(); i++) {
            assertEquals(correctHeapPrioritiesAfterIncrease[i], heapData[i].priority);
        }
    }

    @Test
    @Tag("C")
    @Order(5)
    @DisplayName("Smoke test decreaseKey while checking internal state of heap")
    public void testDecreaseKey() {
        // Build heap
        MinFourHeap<Integer> heap = new MinFourHeap<>();
        List<Integer> values = new ArrayList<>(Arrays.asList(9, -100, 19, 3, -2, 1, 7, -84, -4, 2, 70));
        for (Integer value : values) {
            assertTrue(heap.enqueue(new IPriorityQueue.PQElement<>(value, value)));
        }

        // Assert constructed heap is correct
        Integer[] correctHeapData = {-100, -84, 2, 3, -2, 9, 7, 1, -4, 19, 70};
        IPriorityQueue.PQElement<Integer>[] heapData = Reflection.getFieldValue(MinFourHeap.class, "data", heap);
        for (int j = 0; j < heap.size(); j++) {
            assertEquals(correctHeapData[j], heapData[j].data);
        }
        // Decrease some node's priority
        heap.decreaseKey(new IPriorityQueue.PQElement<>(7, -105));

        // Verify the heap after moving is correct
        double[] correctHeapPrioritiesAfterDecrease = {-105, -100, 2, 3, -2, 9, -84, 1, -4, 19, 70};
        heapData = Reflection.getFieldValue(MinFourHeap.class, "data", heap);
        checkKeyToIndexMap(heap);
        for (int i = 0; i < heap.size(); i++) {
            assertEquals(correctHeapPrioritiesAfterDecrease[i], heapData[i].priority);
        }
    }

    @Test
    @Tag("C")
    @Order(6)
    @DisplayName("Dequeueing with no further percolation leaves the heap in a consistent state")
    public void testDequeueWithNoPercolation() {
        MinFourHeap<Integer> heap = new MinFourHeap<>();
        List<Integer> values = new ArrayList<>(Arrays.asList(1, 6, 7, 8, 2));
        for (int value : values) {
            assertTrue(heap.enqueue(new IPriorityQueue.PQElement<>(value, value)));
        }
        // Dequeueing 1 won't cause any further percolations, since 2 is in the right place.
        // There's some edge cases around this for some reason, which is why the test is here...
        assertEquals(1, heap.dequeue().data);
        checkKeyToIndexMap(heap);
    }


    @Tag("C")
    @Order(6)
    @Test
    @DisplayName("Check that increaseKey that percolates near end of array does not throw")
    public void testDecreaseKeyBeyondArrayBounds() {
        MinFourHeap<Integer> heap = new MinFourHeap<>();
        // Heap:
        // 0 => [2 => [5, 6, 7, 8], 1 => [9], 3 => [], 4 => []]
        List<Integer> values = new ArrayList<>(Arrays.asList(0, 2, 1, 3, 4, 5, 6, 7, 8, 9));
        for (int value : values) {
            assertTrue(heap.enqueue(new IPriorityQueue.PQElement<>(value, value)));
        }
        IPriorityQueue.PQElement<Integer>[] heapData = Reflection.getFieldValue(MinFourHeap.class, "data", heap);
        // Make sure our heap data is still "good" for the test
        assertEquals(10, heapData.length, "Heap data array is not a default size of 10 or was resized prematurely");

        // Increase the node at the root. The node gets swapped with 1, then compared against children.
        // But, 9 is at the last index in the heap array and not the last child.
        heap.increaseKey(new IPriorityQueue.PQElement<>(0, 100));
        // Correctness is checked elsewhere, so don't do anything here. Only thing that matters is that this
        // executes successfully.
        // 1 => [2 => [5, 6, 7, 8], 9 => [0 (100)], 3 => [], 4 => []]
    }

    @Tag("C")
    @Order(7)
    @ParameterizedTest(name = "Stress test increaseKey and decreaseKey with {1} random elements and seed = {0}")
    @DisplayName("Stress test increaseKey, decreaseKey")
    @CsvSource({"100, 30000, 15000", "42, 10000, 5000"})
    public void stressTestIncreaseDecrease(int seed, int size, int numToReplace) {
        MinFourHeap<Integer> heap = new MinFourHeap<>();
        Comparator<Integer> c = new IntegerComparator();
        PriorityQueue<Integer> reference = new PriorityQueue<>(c);
        Set<Integer> removed = new TreeSet<>();
        Random r = new Random(seed);
        for (int i = 0; i < size; i++) {
            int num = r.nextInt();
            while (reference.contains(num)) {
                num = r.nextInt();
            }
            reference.add(num);
            heap.enqueue(new IPriorityQueue.PQElement<>(num, num));
            assertEquals(reference.size(), heap.size());
        }

        for (int j = 0; j < numToReplace; j++) {
            int newPriority = r.nextInt();
            while (reference.contains(newPriority) || removed.contains(newPriority)) {
                newPriority = r.nextInt();
            }
            IPriorityQueue.PQElement<Integer>[] heapData = Reflection.getFieldValue(MinFourHeap.class, "data", heap);
            Integer origKey = heapData[r.nextInt(heap.size())].data;
            while (removed.contains(origKey)) {
                origKey = heapData[r.nextInt(heap.size())].data;
            }
            if (newPriority < origKey) {
                heap.decreaseKey(new IPriorityQueue.PQElement<>(origKey, newPriority));
            } else {
                heap.increaseKey(new IPriorityQueue.PQElement<>(origKey, newPriority));
            }
            assertEquals(reference.size(), heap.size());
            removed.add(origKey);
            reference.remove(origKey);
            reference.add(newPriority);
            assertEquals(reference.size(), heap.size());
        }
        int i = 0;
        while (!reference.isEmpty()) {
            Integer er = reference.remove();
            IPriorityQueue.PQElement<Integer> mr = heap.dequeue();
            if (er != mr.priority) {
                System.err.println(i);
                System.err.println(reference.size());
                System.err.println(heap.size());
            }
            assertEquals((double) er, mr.priority);
            i++;
        }
    }

    @Tag("C")
    @Order(7)
    @ParameterizedTest(name = "Stress test enqueue and dequeue with {1} random elements and seed = {0}")
    @CsvSource({"100, 10000", "42, 10000"})
    @DisplayName("Stress test enqueue, dequeue")
    public void stressTestEnqueueDequeue(int seed, int size) {
        MinFourHeap<Integer> heap = new MinFourHeap<>();
        Comparator<Integer> c = new IntegerComparator();
        PriorityQueue<Integer> reference = new PriorityQueue<>(c);
        Random r = new Random(seed);
        for (int i = 0; i < size; i++) {
            int num = r.nextInt();
            while (reference.contains(num)) {
                num = r.nextInt();
            }
            reference.add(num);
            assertTrue(heap.enqueue(new IPriorityQueue.PQElement<>(num, num)));

            // Check at intervals to save computation
            if (i % 499 == 0) {
                checkKeyToIndexMap(heap);
            }

            assertEquals(reference.size(), heap.size());
        }
        while (heap.size() != 0) {
            assertEquals(reference.remove(), heap.dequeue().data);

            if (heap.size() % 499 == 0) {
                checkKeyToIndexMap(heap);
            }
        }
    }

}