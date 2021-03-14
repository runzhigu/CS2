package edu.caltech.cs2.interfaces;

import edu.caltech.cs2.helpers.RuntimeInstrumentation;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static edu.caltech.cs2.project04.Project04TestOrdering.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public interface ITrieMapTests extends IDictionaryTests {
    ITrieMap<Object, Iterable<Object>, Object> newITrieMap();

    static List<Object> getFirstNElements(Iterable<Object> iter, int nElem) {
        List<Object> ret = new ArrayList<>();
        if (nElem <= 0) {
            return ret;
        }
        for (Object o : iter) {
            ret.add(o);
            if (ret.size() >= nElem) {
                break;
            }
        }
        return ret;
    }

    /*
     * This is a way of working around Java's generics. Basic idea is that
     * IDictionary makes no assumptions about key type, but TrieMap does. So
     * IDictionary<Object, Object> created from IDictionary<Iterable<Object>,
     * Object> could potentially have non Iterable objects inserted, which would
     * break it.
     *
     * In the IDictionaryTest class, however, IDictionary only obtains data from
     * generateRandomTestData, which is passed up as a required method to implement,
     * so we place the burden on the implementer to ensure that the data generated
     * matches the expected types.
     */
    default IDictionary<Object, Object> newIDictionary() {
        return (IDictionary<Object, Object>) (IDictionary<? extends Object, Object>) newITrieMap();
    }

    default Map<Object, Object> generateRandomTestData(int size, Random rand, int maxNodeDegree, int minKeyLength,
                                                       int maxKeyLength) {
        return (Map<Object, Object>) (Map<? extends Object, Object>) generateRandomTestDataIterable(size, rand,
                maxNodeDegree, minKeyLength, maxKeyLength);
    }

    default Map<Iterable<Object>, Object> generateRandomTestDataIterable(int size, Random rand, int maxNodeDegree,
                                                                         int minKeyLength, int maxKeyLength) {
        Map<Iterable<Object>, Object> base = new HashMap<>();
        for (int i = 0; i < size; i++) {
            int keyLength = minKeyLength + rand.nextInt(maxKeyLength - minKeyLength);
            List<Object> key = new ArrayList<>();
            for (int j = 0; j < keyLength; j++) {
                key.add(rand.nextInt(maxNodeDegree));
            }
            base.put(key, rand.nextInt());
        }
        return base;
    }

    default Map<Iterable<Object>, Object> generateRandomTestDataIterable(int size, Random rand) {
        return generateRandomTestDataIterable(size, rand, 10, 1, 20);
    }

    @Override
    default Map<Object, Object> createReferenceMap(String[] keys, Object[] vals) {
        return (Map<Object, Object>) (Map<? extends Object, Object>) createReferenceIterableMap(keys, vals);
    }

    default Map<Iterable<Object>, Object> createReferenceIterableMap(String[] keys, Object[] vals) {
        Map<Iterable<Object>, Object> ref = new HashMap<>();
        for (int i = 0; i < keys.length; i++) {
            Character[] charArr = keys[i].chars().mapToObj(c -> (char) c).toArray(Character[]::new);
            ref.put(Arrays.asList(charArr), vals[i]);
        }
        return ref;
    }

    /*
     * Types, man. I spent a while trying to work out how to force it, and couldn't.
     * Leaving it like this is a (probably correct) workaround (Problem is that
     * Set<Iterable<Object>> and List<List<Object>> are incompatible) (And need the
     * two types for stress and sanity tests. It's been a while since I wrote this.)
     *
     * 2021 - nope, still looks like crap.
     */
    default List<Object> naiveGetCompletions(Map<Iterable<Object>, Object> map, Collection<Iterable<Object>> keys,
                                             List<Object> prefix) {
        // Gather matched keys naively
        List<Object> matchingValues = new ArrayList<>();

        for (Iterable<Object> kIter : keys) {
            List<Object> k = new ArrayList<>();
            kIter.forEach(k::add);
            if (k.size() < prefix.size())
                continue;

            if (k.subList(0, prefix.size()).equals(prefix)) {
                matchingValues.add(map.get(k));
            }
        }

        return matchingValues;
    }

    default List<Object> naiveGetCompletions(Map<Iterable<Object>, Object> map, List<List<Object>> keys,
                                             List<Object> prefix) {
        // Gather matched keys naively
        List<Object> matchingValues = new ArrayList<>();

        for (List<Object> k : keys) {
            if (k.size() < prefix.size())
                continue;

            if (k.subList(0, prefix.size()).equals(prefix)) {
                matchingValues.add(map.get(k));
            }
        }

        return matchingValues;
    }

    default Stream<Arguments> iDictionarySanityDataSource() {
        return Stream.of(
                Arguments.of(createReferenceMap(new String[]{"a", "ab", "abc", "abcd", "abcde"},
                        new Integer[]{1, 2, 3, 4, 5})),
                Arguments.of(createReferenceMap(new String[]{"abcde", "abcd", "abc", "ab", "a"},
                        new Integer[]{1, 2, 3, 4, 5})),
                Arguments.of(createReferenceMap(new String[]{"a", "add", "app"},
                        new String[]{"hello", "1 + 1", "for a phone"})),
                Arguments.of(createReferenceMap(
                        new String[]{"adam", "add", "app", "bad", "bag", "bags", "beds", "bee", "cab"},
                        new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8})));
    }

    default Stream<Arguments> iTrieMapSanityDataSource() {
        return Stream.of(
                Arguments.of(createReferenceIterableMap(new String[]{"a", "ab", "abc", "abcd", "abcde"},
                        new Integer[]{1, 2, 3, 4, 5}), new String[]{"b"}),
                Arguments.of(createReferenceIterableMap(new String[]{"abcde", "abcd", "abc", "ab", "a"},
                        new Integer[]{1, 2, 3, 4, 5}), new String[]{"b"}),
                Arguments.of(createReferenceIterableMap(new String[]{"a", "add", "app"},
                        new String[]{"hello", "1 + 1", "for a phone"}), new String[]{"adds"}),
                Arguments.of(createReferenceIterableMap(
                        new String[]{"adam", "add", "app", "bad", "bag", "bags", "beds", "bee", "cab"},
                        new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8}), new String[]{"bay"}));
    }

    default void iTrieMapSanityTestHelper(Map<Iterable<Object>, Object> base, String[] negativePrefixesToCheck,
                                          boolean testRemove) {
        // Build ITrieMap
        ITrieMap<Object, Iterable<Object>, Object> impl = newITrieMap();
        for (Map.Entry<Iterable<Object>, Object> e : base.entrySet()) {
            impl.put(e.getKey(), e.getValue());
        }

        // Test known positive cases
        for (Map.Entry<Iterable<Object>, Object> e : base.entrySet()) {
            List<Object> keyAsList = new ArrayList<>();
            e.getKey().forEach(keyAsList::add);

            for (int i = 0; i < keyAsList.size(); i++) {
                List<Object> prefix = keyAsList.subList(0, i);
                assertTrue(impl.isPrefix(prefix), "isPrefix returns false for present prefix " + prefix);

                List<Object> trueCompletions = naiveGetCompletions(base, base.keySet(), prefix);
                MatcherAssert.assertThat("getCompletions", impl.getCompletions(prefix),
                        IsIterableContainingInAnyOrder.containsInAnyOrder(trueCompletions.toArray()));
            }
        }

        // Test provided negative cases
        for (String prefixStr : negativePrefixesToCheck) {
            Character[] charArr = prefixStr.chars().mapToObj(c -> (char) c).toArray(Character[]::new);
            List<Object> prefix = Arrays.asList(charArr);

            assertFalse(impl.isPrefix(prefix), "isPrefix returns true for missing prefix " + prefix);
            assertEquals(0, impl.getCompletions(prefix).size(),
                    "getCompletions returns nonempty completions for missing prefix " + prefix);
        }

        // Simple test for removals
        if (testRemove) {
            for (Map.Entry<Iterable<Object>, Object> e : base.entrySet()) {
                impl.remove(e.getKey());
            }
            for (Map.Entry<Iterable<Object>, Object> e : base.entrySet()) {
                assertFalse(impl.isPrefix(e.getKey()), "isPrefix returns true for removed key " + e.getKey());
                assertEquals(0, impl.getCompletions(e.getKey()).size(),
                        "getCompletions returns nonempty completions for missing key " + e.getKey());
            }
        }
    }

    default void iTrieMapStressTestHelper(int seed, int size, boolean testRemove) {
        Random rand = new Random(seed);

        Map<Iterable<Object>, Object> base = generateRandomTestDataIterable(size, rand);
        ITrieMap<Object, Iterable<Object>, Object> impl = newITrieMap();
        int minPrefixSize = 2;
        int maxPrefixSize = 20;

        Set<List<Object>> excludedPrefixes = new HashSet<>();
        for (Iterable<Object> k : base.keySet()) {
            if (rand.nextDouble() < 0.1) {
                int prefixSize = minPrefixSize + rand.nextInt(maxPrefixSize - minPrefixSize);
                excludedPrefixes.add(getFirstNElements(k, prefixSize));
            }
        }

        Set<List<Object>> includedKeys = new HashSet<>();
        Set<List<Object>> excludedKeys = new HashSet<>();
        for (Map.Entry<Iterable<Object>, Object> e : base.entrySet()) {
            // If testRemove, excluded keys will be removed later
            boolean exclude = testRemove;
            List<Object> list = new ArrayList<>();
            e.getKey().forEach(list::add);

            if (!testRemove) {
                for (int i = 0; i <= list.size() && !exclude; i++) {
                    if (excludedPrefixes.contains(list.subList(0, i))) {
                        exclude = true;
                    }
                }
            }
            if (!exclude) {
                impl.put(e.getKey(), e.getValue());
                includedKeys.add(list);
            } else {
                excludedKeys.add(list);
            }
        }

        // Test isPrefix positive cases
        for (List<Object> k : includedKeys) {
            for (int i = 0; i <= k.size(); i++) {
                assertTrue(impl.isPrefix(k.subList(0, i)), "Prefix that was added returns false from isPrefix.");
            }
        }

        // Test getCompletions random cases
        int numCompletionsToCheck = 50;
        List<List<Object>> keyList = new ArrayList<>(includedKeys);
        for (int i = 0; i < numCompletionsToCheck; i++) {
            List<Object> key = keyList.get(rand.nextInt(includedKeys.size()));
            int prefixSize = 1 + rand.nextInt(key.size());
            List<Object> prefix = key.subList(0, prefixSize);
            List<Object> matchingValues = naiveGetCompletions(base, keyList, prefix);
            MatcherAssert.assertThat("Prefix returns incorrect getCompletions", impl.getCompletions(prefix),
                    IsIterableContainingInAnyOrder.containsInAnyOrder(matchingValues.toArray()));
        }

        // Process removals if necessary
        if (testRemove) {
            for (List<Object> k : excludedKeys) {
                for (int i = 0; i <= k.size(); i++) {
                    assertTrue(impl.isPrefix(k.subList(0, i)), "Prefix that was added returns false from isPrefix.");
                }
                impl.remove(k);
            }
        }

        // Test isPrefix negative cases
        for (List<Object> p : excludedPrefixes) {
            assertFalse(impl.isPrefix(p), "Prefix that was removed still exists in the trie");
            MatcherAssert.assertThat("Prefix that doesn't exist returns non-empty array from getCompletions",
                    impl.getCompletions(p), IsIterableContainingInAnyOrder.containsInAnyOrder(new ArrayList<>()));
        }

    }

    @Tag("C")
    @Order(sanityTestLevel)
    @DisplayName("Sanity test ITrieMap isPrefix, getCompletions")
    @ParameterizedTest(name = "Test ITrieMap with data={0}")
    @MethodSource("iTrieMapSanityDataSource")
    default void sanityTestITrieMapNoRemove(Map<Iterable<Object>, Object> base, String[] prefixesToCheck) {
        iTrieMapSanityTestHelper(base, prefixesToCheck, false);
    }

    @Tag("A")
    @Order(sanityTestLevel)
    @DisplayName("Sanity test ITrieMap isPrefix, getCompletions, with remove")
    @ParameterizedTest(name = "Test ITrieMap with data={0}")
    @MethodSource("iTrieMapSanityDataSource")
    default void sanityTestITrieMapRemove(Map<Iterable<Object>, Object> base, String[] prefixesToCheck) {
        iTrieMapSanityTestHelper(base, prefixesToCheck, true);
    }

    @Tag("C")
    @Order(stressTestLevel)
    @DisplayName("Stress test ITrieMap isPrefix, getCompletions")
    @ParameterizedTest(name = "Test ITrieMap with seed={0} and size={1}")
    @CsvSource({"24589, 3000", "96206, 5000"})
    default void stressTestITrieMapNoRemove(int seed, int size) {
        iTrieMapStressTestHelper(seed, size, false);
    }

    @Tag("A")
    @Order(stressTestLevel)
    @DisplayName("Stress test ITrieMap isPrefix, getCompletions, with remove")
    @ParameterizedTest(name = "Test IDictionary  with seed={0} and size={1}")
    @CsvSource({"24589, 3000", "96206, 5000"})
    default void stressTestITrieMapRemove(int seed, int size) {
        iTrieMapStressTestHelper(seed, size, false);
    }


    @Tag("C")
    @Order(sanityTestLevel)
    @DisplayName("Test ITrieMap clear")
    @Test
    default void testClear() {
        int size = 100;
        Map<Iterable<Object>, Object> base = generateRandomTestDataIterable(size, new Random(1), 3, 1, 10);
        ITrieMap<Object, Iterable<Object>, Object> impl = newITrieMap();
        for (Map.Entry<Iterable<Object>, Object> e : base.entrySet()) {
            impl.put(e.getKey(), e.getValue());
        }
        assertEquals(impl.size(), base.size(), "Trie size is not correct");
        impl.clear();
        assertEquals(0, impl.size(), "Size after clear is not zero");
        assertTrue(impl.keys().isEmpty(), "Keys after clear is not empty");
        assertTrue(impl.values().isEmpty(), "Values after clear is not empty");
    }

    @Order(specialTestLevel)
    @Tag("C")
    @DisplayName("Test size() -- constant complexity")
    @Timeout(value = 10, unit = SECONDS)
    @Test
    default void testSizeComplexity() {
        Function<Integer, ITrieMap<Object, Iterable<Object>, Object>> provide = (Integer numElements) -> {
            Random rand = new Random(34857);
            Map<Iterable<Object>, Object> o = generateRandomTestDataIterable(numElements, rand);
            ITrieMap<Object, Iterable<Object>, Object> impl = newITrieMap();
            for (Map.Entry<Iterable<Object>, Object> e : o.entrySet()) {
                impl.put(e.getKey(), e.getValue());
            }
            return impl;
        };

        Consumer<ITrieMap<Object, Iterable<Object>, Object>> size = IDictionary::size;
        RuntimeInstrumentation.assertAtMost("size", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, size, 8);
    }

    @Order(sanityTestLevel)
    @Tag("C")
    @DisplayName("Test isPrefix() returns false for empty TrieMap")
    @Test
    default void testIsPrefixEmpty() {
        ITrieMap<Object, Iterable<Object>, Object> impl = newITrieMap();
        assertFalse(impl.isPrefix(new ArrayList<>()), "isPrefix([]) on empty TrieMap should return false");

        impl.put(new ArrayList<>(), 0);
        assertTrue(impl.isPrefix(new ArrayList<>()), "isPrefix([]) on nonempty TrieMap should return true");
    }

    @Order(sanityTestLevel)
    @Tag("C")
    @DisplayName("Test get() returns null for empty TrieMap")
    @Test
    default void testGetEmpty() {
        ITrieMap<Object, Iterable<Object>, Object> impl = newITrieMap();
        assertNull(impl.get(new ArrayList<>()), "get([]) on empty TrieMap should return null");

        impl.put(new ArrayList<>(), 0);
        assertEquals(0, impl.get(new ArrayList<>()), "get([]) on TrieMap with mapping {[], 0} does not return 0");
    }

    @Order(sanityTestLevel)
    @Tag("A")
    @DisplayName("Test remove() returns null for empty TrieMap")
    @Test
    default void testRemoveEmpty() {
        ITrieMap<Object, Iterable<Object>, Object> impl = newITrieMap();
        assertNull(impl.remove(new ArrayList<>()), "remove([]) on empty TrieMap should return null");

        impl.put(new ArrayList<>(), 0);
        assertEquals(0, impl.get(new ArrayList<>()), "get([]) on TrieMap with mapping {[], 0} does not return 0");

        assertEquals(0, impl.remove(new ArrayList<>()), "remove([]) on TrieMap with mapping {[], 0} does not return 0");
        assertNull(impl.get(new ArrayList<>()), "get([]) on TrieMap after removal of [] is not null");
    }

}
