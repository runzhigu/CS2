package edu.caltech.cs2.datastructures;

import java.time.Duration;
import java.util.*;
import java.util.stream.Stream;

import edu.caltech.cs2.helpers.RuntimeInstrumentation;
import edu.caltech.cs2.interfaces.IDictionary;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import static edu.caltech.cs2.project05.Project05TestOrdering.*;
import static org.junit.jupiter.api.Assertions.*;

// This allows putting the burden of creating the reference map on the
// implementer, which enables control over key subclass / implementation.
// Nothing test-related is stored in instance variables, so this does not have (known) side effects.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public interface IDictionaryTests {
    int SINGLE_OP_TIMEOUT_MS();

    int CONTAINS_VALUE_TIMEOUT_MS();

    RuntimeInstrumentation.ComplexityType getAndPutComplexity();

    IDictionary<Object, Object> newIDictionary();

    // This allows the subclass to require specific formats required for keys
    // e.g. IDictionary<K extends Comparable, V>, or IDictionary<K extends Iterable,
    // V>
    Stream<Arguments> iDictionarySmokeDataSource();

    Map<Object, Object> createReferenceMap(String[] keys, Object[] vals);

    Map<Object, Object> generateRandomTestData(int size, Random rand, int maxNodeDegree, int minKeyLength,
            int maxKeyLength);

    default Map<Object, Object> generateRandomTestData(int size, Random rand) {
        return generateRandomTestData(size, rand, 10, 1, 20);
    }

    default void iDictionarySmokeTestHelper(Map<Object, Object> base, boolean testRemove) {
        IDictionary<Object, Object> impl = newIDictionary();
        Set<Object> seenValues = new HashSet<>();

        assertTrue(impl.isEmpty(), "Newly initialized IDictionary is nonempty");
        int expectedSize = 0;

        for (Object k : base.keySet()) {

            // Negative key + value tests
            assertFalse(impl.containsKey(k), "containsKey returns true for missing key " + k);
            if (!seenValues.contains(base.get(k))) {
                seenValues.add(base.get(k));
                assertFalse(impl.containsValue(base.get(k)), "containsValue returns true for missing value");
            }
            assertNull(impl.get(k), "Getting a missing key returns a non null value");

            // Put the key in
            assertNull(impl.put(k, ""), "Putting a new key " + k + " returns a non null value");
            expectedSize++;
            assertEquals(expectedSize, impl.size(), "Incorrect size");

            // Existing key tests
            assertEquals("", impl.get(k), "Getting an existing key " + k + " returns an incorrect value");
            assertEquals("", impl.put(k, base.get(k)), "Putting an existing key " + k + " returns an incorrect value");
            assertEquals(base.get(k), impl.get(k), "Getting an updated key " + k + " returns an old value");
            assertEquals(expectedSize, impl.size(), "Putting an existing key changed the size");
            assertTrue(impl.containsKey(k), "containsKey returns false for present key " + k);
            assertTrue(impl.containsValue(base.get(k)), "containsValue returns false for present value");
        }

        MatcherAssert.assertThat("keys", impl.keys(),
                IsIterableContainingInAnyOrder.containsInAnyOrder(base.keySet().toArray()));
        MatcherAssert.assertThat("iterator", impl,
                IsIterableContainingInAnyOrder.containsInAnyOrder(base.keySet().toArray()));
        MatcherAssert.assertThat("values", impl.values(),
                IsIterableContainingInAnyOrder.containsInAnyOrder(base.values().toArray()));

        if (testRemove) {
            Set<Object> keys = new HashSet<>(base.keySet());
            for (Object k : keys) {
                Object v = base.remove(k);
                assertEquals(v, impl.remove(k), "Removing existing key returns wrong value");
                expectedSize--;
                assertEquals(expectedSize, impl.size(), "Removing existing key did not decrease size");

                assertNull(impl.remove(k), "Removing missing key returns nonnull");
                assertEquals(expectedSize, impl.size(), "Removing missing key changed size");

                assertFalse(impl.containsKey(k), "containsKey returned true for removed key " + k);
                if (!base.containsValue(v)) {
                    assertFalse(impl.containsValue(v), "containsValue returned false for removed value");
                }
            }
        }
    }

    default void iDictionaryStressTestHelper(int seed, int size, boolean testRemove) {
        Random rand = new Random(seed);
        Map<Object, Object> excluded = new HashMap<>();
        Set<Object> includedKeys = new HashSet<>();
        List<Object> includedValues = new ArrayList<>();

        Map<Object, Object> base = generateRandomTestData(size, rand);
        IDictionary<Object, Object> impl = newIDictionary();

        // Randomly choose negative cases
        for (Map.Entry<Object, Object> e : base.entrySet()) {
            // Exclude this element
            if (rand.nextDouble() < 0.4) {
                excluded.put(e.getKey(), e.getValue());
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
            if (testRemove || !excluded.containsKey(e.getKey())) {
                assertNull(impl.put(e.getKey(), ""), "Putting new key returns incorrect value");
                expectedSize++;
                assertEquals(expectedSize, impl.size(), "Adding new key did not appropriately change size");
                assertEquals("", impl.put(e.getKey(), e.getValue()), "Putting old key returns different value");
                assertEquals(e.getValue(), impl.get(e.getKey()), "Getting an updated key returns old value");
                assertEquals(expectedSize, impl.size(), "Putting existing key changed size");
            }
        }

        // Process removals if being tested
        if (testRemove) {
            for (Object k : excluded.keySet()) {
                assertEquals(base.remove(k), impl.remove(k), "Removing existing key returns wrong value");
                expectedSize--;
                assertEquals(expectedSize, impl.size(), "Removing existing key did not decrease size");

                assertNull(impl.remove(k), "Removing missing key returns nonnull");
                assertEquals(expectedSize, impl.size(), "Removing missing key changed size");
            }
        }
        // Match reference map state to what excluded keys
        else {
            for (Object k : excluded.keySet()) {
                base.remove(k);
            }
        }

        // Iterable checks
        MatcherAssert.assertThat("keys", impl.keys(),
                IsIterableContainingInAnyOrder.containsInAnyOrder(includedKeys.toArray()));
        MatcherAssert.assertThat("iterator", impl,
                IsIterableContainingInAnyOrder.containsInAnyOrder(includedKeys.toArray()));
        MatcherAssert.assertThat("values", impl.values(),
                IsIterableContainingInAnyOrder.containsInAnyOrder(includedValues.toArray()));

        // Positive key / value presence
        for (Object k : includedKeys) {
            assertTimeout(Duration.ofMillis(SINGLE_OP_TIMEOUT_MS()), () -> {
                assertTrue(impl.containsKey(k), "Running containsKey on added key returns false.");
            });
            assertTimeout(Duration.ofMillis(CONTAINS_VALUE_TIMEOUT_MS()), () -> {
                assertTrue(impl.containsValue(base.get(k)), "Running containsValue on added value returns false.");
            });
        }

        // Negative key presence check
        for (Object k : excluded.keySet()) {
            assertTimeout(Duration.ofMillis(SINGLE_OP_TIMEOUT_MS()), () -> {
                assertFalse(impl.containsKey(k), "Running containsKey on missing key returns true.");
            });

            Object v = excluded.get(k);
            if (!base.containsValue(v)) {
                assertTimeout(Duration.ofMillis(CONTAINS_VALUE_TIMEOUT_MS()), () -> {
                    assertFalse(impl.containsValue(v), "Running containsValue on missing value returns true.");
                });
            }
        }

    }

    @Order(sanityTestLevel)
    @DisplayName("Smoke test all IDictionary methods")
    @ParameterizedTest(name = "Test IDictionary interface on {0}")
    @MethodSource("iDictionarySmokeDataSource")
    default void smokeTestIDictionaryNoRemove(Map<Object, Object> base) {
        iDictionarySmokeTestHelper(base, false);
    }

    @Order(sanityTestLevel)
    @DisplayName("Smoke test all IDictionary methods, with remove")
    @ParameterizedTest(name = "Test IDictionary interface on {0}")
    @MethodSource("iDictionarySmokeDataSource")
    default void smokeTestIDictionaryRemove(Map<Object, Object> base) {
        iDictionarySmokeTestHelper(base, true);
    }

    @Order(stressTestLevel)
    @DisplayName("Stress test all IDictionary methods")
    @ParameterizedTest(name = "Test IDictionary interface with seed={0} and size={1}")
    @CsvSource({ "24589, 3000", "96206, 5000" })
    default void stressTestIDictionaryNoRemove(int seed, int size) {
        iDictionaryStressTestHelper(seed, size, false);
    }

    @Order(stressTestLevel)
    @DisplayName("Stress test all IDictionary methods, with remove")
    @ParameterizedTest(name = "Test IDictionary interface with seed={0} and size={1}")
    @CsvSource({ "24589, 3000", "96206, 5000" })
    default void stressTestIDictionaryRemove(int seed, int size) {
        iDictionaryStressTestHelper(seed, size, false);
    }

}
