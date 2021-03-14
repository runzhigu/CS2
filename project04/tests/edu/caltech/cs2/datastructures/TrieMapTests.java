package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.helpers.Inspection;
import edu.caltech.cs2.helpers.Reflection;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.ITrieMap;
import edu.caltech.cs2.interfaces.ITrieMapTests;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Function;

import static edu.caltech.cs2.project04.Project04TestOrdering.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TrieMapTests implements ITrieMapTests {

    private static String STRING_SOURCE = "src/edu/caltech/cs2/datastructures/TrieMap.java";
    public Counter collectorCounter = new Counter();

    public ITrieMap<Object, Iterable<Object>, Object> newITrieMap() {
        Constructor c = Reflection.getConstructor(TrieMap.class, Function.class);
        this.collectorCounter.resetCounter();
        Function<IDeque<Object>, Iterable<Object>> collector = (IDeque<Object> o) -> {
            this.collectorCounter.touchCounter();
            List<Object> k = new ArrayList<>(o.size());
            for (Object m : o) {
                k.add(m);
            }

            return k;
        };

        return Reflection.newInstance(c, collector);
    }

    @Order(classSpecificTestLevel)
    @Tag("C")
    @DisplayName("Does not use or import disallowed packages")
    @Test
    public void testForInvalidClasses() {
        List<String> regexps = List.of("java\\.lang\\.reflect", "java\\.io");
        Inspection.assertNoImportsOf(STRING_SOURCE, regexps);
        Inspection.assertNoUsageOf(STRING_SOURCE, regexps);
    }

    @Order(classSpecificTestLevel)
    @Tag("C")
    @DisplayName("Does not use or import disallowed classes from java.util")
    @Test
    public void testForInvalidImportsJavaUtil() {
        List<String> allowed = List.of("Map", "HashMap", "Set", "HashSet", "Iterator", "function\\.Function");
        Inspection.assertNoImportsOfExcept(STRING_SOURCE, "java\\.util", allowed);
        List<String> bannedUsages = List.of("java\\.lang\\.reflect", "java\\.io",
                "java\\.util\\.(?!" + String.join("|", allowed) + ")");
        Inspection.assertNoUsageOf(STRING_SOURCE, bannedUsages);
    }

    @Order(specialTestLevel)
    @Tag("C")
    @DisplayName("Test that collector function is used in keys")
    @Test
    public void testCollectorUsage() {
        ITrieMap<Object, Iterable<Object>, Object> impl = newITrieMap();
        Map<Iterable<Object>, Object> base = generateRandomTestDataIterable(2000, new Random(69), 2, 1, 10);
        for (Map.Entry<Iterable<Object>, Object> e : base.entrySet()) {
            impl.put(e.getKey(), e.getValue());
        }
        impl.keys();
        // Random test data may repeat keys, so we use the base's size
        assertEquals(base.size(), this.collectorCounter.touches, "Collector was not called once for each key.");
    }

    public void verifyNonlazyRemoveHelper(Object trieNode) {
        Map<Object, Object> c = Reflection.getFieldValue(trieNode.getClass(), "pointers", trieNode);
        if (c.isEmpty()) {
            Object v = Reflection.getFieldValue(trieNode.getClass(), "value", trieNode);
            assertNotNull(v, "Leaf node of TrieMap has null value and should have been pruned");
        }
        for (Object v : c.values()) {
            verifyNonlazyRemoveHelper(v);
        }
    }

    @Tag("A")
    @Order(specialTestLevel)
    @DisplayName("Test that remove is not a lazy implementation")
    @ParameterizedTest(name = "Verify nonlazy remove for seed={0}, size={1}")
    @CsvSource({"24589, 3000", "96206, 5000"})
    public void verifyNonlazyRemove(int seed, int size) {
        Random rand = new Random(seed);
        int maxKeyLength = 10;
        Map<Iterable<Object>, Object> base = generateRandomTestDataIterable(size, rand, 2, 1, maxKeyLength);
        int longestKeyLength = base.keySet().stream().mapToInt(i -> (int) i.spliterator().getExactSizeIfKnown()).max()
                .getAsInt();
        ITrieMap<Object, Iterable<Object>, Object> impl = newITrieMap();

        // Populate ITrieMap, tracking the longest keys to remove the all current leaves,
        // guaranteeing that lazy deletion will leave hanging leaves.
        List<Iterable<Object>> longKeys = new ArrayList<>();
        for (Map.Entry<Iterable<Object>, Object> e : base.entrySet()) {
            impl.put(e.getKey(), e.getValue());
            if (e.getKey().spliterator().getExactSizeIfKnown() >= longestKeyLength) {
                longKeys.add(e.getKey());
            }
        }

        for (Iterable<Object> k : longKeys) {
            impl.remove(k);
            assertFalse(impl.containsKey(k), "Removed key still present in map.");
        }

        // Explore ITrieMap to completion, and verify that all leaves have values.
        Object root = Reflection.getFieldValue(TrieMap.class, "root", impl);
        verifyNonlazyRemoveHelper(root);
    }

    @Tag("C")
    @Order(sanityTestLevel)
    @DisplayName("Test that ITrieMap clear sets root to null")
    @Test
    public void testNullRootAfterClear() {
        ITrieMap<Object, Iterable<Object>, Object> impl = newITrieMap();

        Object root = Reflection.getFieldValue(TrieMap.class, "root", impl);
        assertNull(root, "New empty TrieMap nas nonnull root node");

        List<Object> key = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        impl.put(key, 0);

        root = Reflection.getFieldValue(TrieMap.class, "root", impl);
        assertNotNull(root, "Nonempty TrieMap has null root node");

        impl.clear();
        root = Reflection.getFieldValue(TrieMap.class, "root", impl);
        assertNull(root, "TrieMap emptied by clear() has nonnull root node");
    }

    @Tag("A")
    @Order(sanityTestLevel)
    @DisplayName("Test that emptying a TrieMap with remove() sets the root node to null")
    @Test
    public void verifyNullRootAfterEmpty() {
        ITrieMap<Object, Iterable<Object>, Object> impl = newITrieMap();

        Object root = Reflection.getFieldValue(TrieMap.class, "root", impl);
        assertNull(root, "New empty TrieMap nas nonnull root node");

        List<Object> key = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        impl.put(key, 0);

        root = Reflection.getFieldValue(TrieMap.class, "root", impl);
        assertNotNull(root, "Nonempty TrieMap has null root node");

        impl.remove(key);
        root = Reflection.getFieldValue(TrieMap.class, "root", impl);
        assertNull(root, "TrieMap emptied by remove() has nonnull root node");
    }

    private static class Counter {
        public int touches;
        public Object data;

        public Counter() {
            this(0);
        }

        public Counter(Object data) {
            this.touches = 0;
            this.data = data;
        }

        public void resetCounter() {
            this.touches = 0;
        }

        public void touchCounter() {
            this.touches++;
        }

        @Override
        public int hashCode() {
            this.touchCounter();
            return this.data.hashCode();
        }

        @Override
        // Equals does not count as a "touch"
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            } else if (!(o instanceof Counter)) {
                return false;
            }
            Counter c = (Counter) o;
            if (this.data == null || c.data == null) {
                return (this.data == null && c.data == null);
            }
            return this.data.equals(c.data);
        }

        @Override
        public String toString() {
            return this.data == null ? "null" : this.data.toString();
        }
    }

}
