package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.helpers.RuntimeInstrumentation;
import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.textgenerator.NGram;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.provider.Arguments;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static edu.caltech.cs2.project05.Project05TestOrdering.sanityTestLevel;
import static edu.caltech.cs2.project05.Project05TestOrdering.specialTestLevel;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Wrapper class for all IDictionaries that will be tested using NGram keys to reduce code repetition
public interface IDictionaryNGramTests extends IDictionaryTests {

    @Override
    default Stream<Arguments> iDictionarySmokeDataSource() {
        return Stream.of(
                Arguments.of(createReferenceMap(new String[] { "a", "ab", "abc", "abcd", "abcde" },
                        new Integer[] { 1, 2, 3, 4, 5 })),
                Arguments.of(createReferenceMap(new String[] { "abcde", "abcd", "abc", "ab", "a" },
                        new Integer[] { 1, 2, 3, 4, 5 })),
                Arguments.of(createReferenceMap(new String[] { "a", "add", "app" },
                        new String[] { "hello", "1 + 1", "for a phone" })),
                Arguments.of(createReferenceMap(
                        new String[] { "adam", "add", "app", "bad", "bag", "bags", "beds", "bee", "cab" },
                        new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 })));
    }

    @Override
    default Map<Object, Object> createReferenceMap(String[] keys, Object[] vals) {
        Map<Object, Object> ref = new HashMap<>();
        for (int i = 0; i < keys.length; i++) {
            ref.put(NGramTests.stringToNGram(keys[i]), vals[i]);
        }
        return ref;
    }

    @Override
    default Map<Object, Object> generateRandomTestData(int size, Random rand, int maxNodeDegree, int minKeyLength,
                                                       int maxKeyLength) {
        Map<Object, Object> base = new HashMap<>();
        for (int i = 0; i < size; i++) {
            int keyLength = minKeyLength + rand.nextInt(maxKeyLength - minKeyLength);
            String[] key = new String[keyLength];
            for (int j = 0; j < keyLength; j++) {
                key[j] = String.valueOf(rand.nextInt(maxNodeDegree));
            }
            base.put(new NGram(key), rand.nextInt());
        }
        return base;
    }

    @Order(sanityTestLevel)
    @DisplayName("Test that keys are not compared using reference equality")
    @Test
    default void testNoReferenceEquality() {
        IDictionary<Object, Object> t = newIDictionary();
        for (int i = 0; i < 10; i++) {
            t.put(new NGram(new String[] {"" + i}), 0);
        }
        for (int i = 0; i < 10; i ++) {
            assertTrue(t.containsKey(new NGram(new String[] {new String("" + i)})),
                    "NGram that is a distinct object with same data should be a contained key, but is not");
        }
    }

    @Order(specialTestLevel)
    @DisplayName("Test get() -- complexity")
    @Test
    @Timeout(value = 20, unit = SECONDS)
    default void testGetComplexity() {
        Function<Integer, IDictionary<Object, Object>> provide = (Integer numElements) -> {
            IDictionary<Object, Object> t = newIDictionary();
            for (int i = 0; i < numElements - 4; i++) {
                t.put(new NGram(new String[] { "" + i }), 0);
            }
            return t;
        };

        Consumer<IDictionary<Object, Object>> get = (IDictionary<Object, Object> t) -> {
            t.get(new NGram(new String[] { "0" }));
        };
        RuntimeInstrumentation.assertAtMost("get", getAndPutComplexity(), provide, get, 8);
    }

    @Order(specialTestLevel)
    @DisplayName("Test put() -- complexity")
    @Test
    @Timeout(value = 20, unit = SECONDS)
    default void testPutComplexity() {
        Function<Integer, IDictionary<Object, Object>> provide = (Integer numElements) -> {
            IDictionary<Object, Object> t = newIDictionary();
            for (int i = 0; i < numElements - 4; i++) {
                t.put(new NGram(new String[] { "" + i }), 0);
            }
            return t;
        };

        Consumer<IDictionary<Object, Object>> put = (IDictionary<Object, Object> t) -> {
            t.put(new NGram(new String[] { "0" }), 0);
        };
        RuntimeInstrumentation.assertAtMost("put", getAndPutComplexity(), provide, put, 8);
    }

    @Order(specialTestLevel)
    @DisplayName("Test size() -- complexity")
    @Test
    @Timeout(value = 20, unit = SECONDS)
    default void testSizeComplexity() {
        Function<Integer, IDictionary<Object, Object>> provide = (Integer numElements) -> {
            IDictionary<Object, Object> t = newIDictionary();
            for (int i = 0; i < numElements - 4; i++) {
                t.put(new NGram(new String[] { "" + i }), 0);
            }
            return t;
        };

        Consumer<IDictionary<Object, Object>> size = IDictionary::size;
        RuntimeInstrumentation.assertAtMost("size", RuntimeInstrumentation.ComplexityType.CONSTANT, provide, size, 8);
    }
}
