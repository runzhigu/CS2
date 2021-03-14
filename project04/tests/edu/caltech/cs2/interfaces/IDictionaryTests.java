package edu.caltech.cs2.interfaces;

import java.time.Duration;
import java.util.*;
import java.util.stream.Stream;

import edu.caltech.cs2.interfaces.IDictionary;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import static edu.caltech.cs2.project04.Project04TestOrdering.sanityTestLevel;
import static edu.caltech.cs2.project04.Project04TestOrdering.stressTestLevel;
import static org.junit.jupiter.api.Assertions.*;

// This allows putting the burden of creating the reference map on the
// implementer, which enables control over key subclass / implementation.
// Nothing test-related is stored in instance variables, so this does not have (known) side effects.

// This whole class is unsafe and I hate it.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public interface IDictionaryTests {
    int SINGLE_OP_TIMEOUT_MS = 25;
    int CONTAINS_VALUE_TIMEOUT_MS = 60;

    IDictionary<Object, Object> newIDictionary();

    // This allows the subclass to require specific formats required for keys
    // e.g. IDictionary<K extends Comparable, V>, or IDictionary<K extends Iterable,
    // V>
    Stream<Arguments> iDictionarySanityDataSource();

    Map<Object, Object> createReferenceMap(String[] keys, Object[] vals);

    Map<Object, Object> generateRandomTestData(int size, Random rand, int maxNodeDegree, int minKeyLength,
            int maxKeyLength);

    default Map<Object, Object> generateRandomTestData(int size, Random rand) {
        return generateRandomTestData(size, rand, 10, 1, 20);
    }

    default void iDictionarySanityTestHelper(Map<Object, Object> base, boolean testRemove) {
        IDictionary<Object, Object> impl = newIDictionary();

        assertTrue(impl.isEmpty(), "Newly initialized IDictionary is nonempty");
        int expectedSize = 0;

        for (Object k : base.keySet()) {

            // Negative key tests
            assertFalse(impl.containsKey(k), "containsKey returns true for missing key " + k);
            assertNull(impl.get(k), "Getting a missing key returns a non null value");

            // Put the key in
            assertNull(impl.put(k, base.get(k)), "Putting a new key " + k + " returns a non null value");
            expectedSize++;
            assertEquals(expectedSize, impl.size(), "Incorrect size");

            // Existing key tests
            assertEquals(base.get(k), impl.get(k), "Getting an existing key " + k + " returns an incorrect value");
            assertEquals(base.get(k), impl.put(k, base.get(k)),
                    "Putting an existing key " + k + " returns an incorrect value");
            assertEquals(expectedSize, impl.size(), "Putting an existing key changed the size");
            assertTrue(impl.containsKey(k), "containsKey returns false for present key " + k);
        }

        MatcherAssert.assertThat("keySet", impl.keys(),
                IsIterableContainingInAnyOrder.containsInAnyOrder(base.keySet().toArray()));
        MatcherAssert.assertThat("iterator", impl,
                IsIterableContainingInAnyOrder.containsInAnyOrder(base.keySet().toArray()));
        MatcherAssert.assertThat("values", impl.values(),
                IsIterableContainingInAnyOrder.containsInAnyOrder(base.values().toArray()));

        if (testRemove) {
            for (Object k : base.keySet()) {
                assertEquals(base.get(k), impl.remove(k), "Removing existing key returns wrong value");
                expectedSize--;
                assertEquals(expectedSize, impl.size(), "Removing existing key did not decrease size");

                assertNull(impl.remove(k), "Removing missing key returns nonnull");
                assertEquals(expectedSize, impl.size(), "Removing missing key changed size");
            }
        }
    }

    default void iDictionaryStressTestHelper(int seed, int size, boolean testRemove) {
        Random rand = new Random(seed);
        Set<Object> excludedKeys = new HashSet<>();
        Set<Object> includedKeys = new HashSet<>();
        List<Object> includedValues = new ArrayList<>();

        Map<Object, Object> base = generateRandomTestData(size, rand);
        IDictionary<Object, Object> impl = newIDictionary();

        // Randomly choose negative cases
        for (Map.Entry<Object, Object> e : base.entrySet()) {
            // Exclude this element
            if (rand.nextDouble() < 0.4) {
                excludedKeys.add(e.getKey());
            }
            // Include this element
            else {
                includedKeys.add(e.getKey());
                includedValues.add(e.getValue());
            }
        }

        // Build the dictionary, verify insertion and retrieval
        int expectedSize = 0;
        for (Map.Entry<Object, Object> e : base.entrySet()) {
            // If testRemove, excluded keys will be removed later
            if (testRemove || !excludedKeys.contains(e.getKey())) {
                assertNull(impl.put(e.getKey(), ""), "Putting new key returns incorrect value");
                expectedSize++;
                assertEquals(expectedSize, impl.size(), "Adding new key did not appropriately change size");
                assertEquals("", impl.put(e.getKey(), e.getValue()), "Putting old key returns different value");
                assertEquals(expectedSize, impl.size(), "Putting existing key changed size");
            }
        }

        // Process removals if being tested
        if (testRemove) {
            for (Object k : excludedKeys) {
                assertEquals(base.get(k), impl.remove(k), "Removing existing key returns wrong value");
                expectedSize--;
                assertEquals(expectedSize, impl.size(), "Removing existing key did not decrease size");

                assertNull(impl.remove(k), "Removing missing key returns nonnull");
                assertEquals(expectedSize, impl.size(), "Removing missing key changed size");
            }
        }

        // Iterable checks
        MatcherAssert.assertThat("keySet", impl.keys(),
                IsIterableContainingInAnyOrder.containsInAnyOrder(includedKeys.toArray()));
        MatcherAssert.assertThat("iterator", impl,
                IsIterableContainingInAnyOrder.containsInAnyOrder(includedKeys.toArray()));
        MatcherAssert.assertThat("values", impl.values(),
                IsIterableContainingInAnyOrder.containsInAnyOrder(includedValues.toArray()));

        // Positive key / value presence
        for (Object k : includedKeys) {
            assertTimeout(Duration.ofMillis(SINGLE_OP_TIMEOUT_MS), () -> {
                assertTrue(impl.containsKey(k), "Running containsKey on added key returns false.");
            });
            assertTimeout(Duration.ofMillis(CONTAINS_VALUE_TIMEOUT_MS), () -> {
                assertTrue(impl.containsValue(base.get(k)), "Running containsValue on added value returns false.");
            });
        }

        // Negative key presence check
        for (Object k : excludedKeys) {
            assertTimeout(Duration.ofMillis(SINGLE_OP_TIMEOUT_MS), () -> {
                assertFalse(impl.containsKey(k), "Running containsKey on missing key returns true.");
            });

            Object v = base.get(k);
            // I didn't actually guarantee it, but collision odds in values are low
            if (!base.containsValue(v)) {
                assertTimeout(Duration.ofMillis(CONTAINS_VALUE_TIMEOUT_MS), () -> {
                    assertFalse(impl.containsValue(v), "Running containsValue on missing value returns true.");
                });
            }
        }

    }

    @Tag("C")
    @Order(sanityTestLevel)
    @DisplayName("Sanity test all IDictionary methods")
    @ParameterizedTest(name = "Test IDictionary interface on {0}")
    @MethodSource("iDictionarySanityDataSource")
    default void sanityTestIDictionaryNoRemove(Map<Object, Object> base) {
        iDictionarySanityTestHelper(base, false);
    }

    @Tag("A")
    @Order(sanityTestLevel)
    @DisplayName("Sanity test all IDictionary methods, with remove")
    @ParameterizedTest(name = "Test IDictionary interface on {0}")
    @MethodSource("iDictionarySanityDataSource")
    default void sanityTestIDictionaryRemove(Map<Object, Object> base) {
        iDictionarySanityTestHelper(base, true);
    }

    @Tag("C")
    @Order(stressTestLevel)
    @DisplayName("Stress test all IDictionary methods")
    @ParameterizedTest(name = "Test IDictionary interface with seed={0} and size={1}")
    @CsvSource({ "24589, 3000", "96206, 5000" })
    default void stressTestIDictionaryNoRemove(int seed, int size) {
        iDictionaryStressTestHelper(seed, size, false);
    }

    @Tag("A")
    @Order(stressTestLevel)
    @DisplayName("Stress test all IDictionary methods, with remove")
    @ParameterizedTest(name = "Test IDictionary interface with seed={0} and size={1}")
    @CsvSource({ "24589, 3000", "96206, 5000" })
    default void stressTestIDictionaryRemove(int seed, int size) {
        iDictionaryStressTestHelper(seed, size, false);
    }
}
