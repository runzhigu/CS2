package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.helpers.Inspection;
import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.IPriorityQueue;
import edu.caltech.cs2.types.NGram;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NGramMapTests {
    private static String STRING_SOURCE = "src/edu/caltech/cs2/datastructures/NGramMap.java";

    @Order(0)
    @DisplayName("Does not use or import disallowed classes")
    @Tag("B")
    @Test
    public void testForInvalidClasses() {
        List<String> regexps = List.of(
                "java.lang.reflect", "java.io");
        Inspection.assertNoImportsOf(STRING_SOURCE, regexps);
        Inspection.assertNoUsageOf(STRING_SOURCE, regexps);
    }

    @Order(1)
    @DisplayName("Does not use or import disallowed classes from java.util")
    @Test
    @Tag("B")
    public void testForInvalidImportsJavaUtil() {
        List<String> allowed = List.of("Iterator", "function.Supplier", "Random", "Scanner");
        Inspection.assertNoImportsOfExcept(STRING_SOURCE, "java\\.util", allowed);

        List<String> bannedUsages = List.of("java\\.util\\.(?!" + String.join("|", allowed) + ")");
        Inspection.assertNoUsageOf(STRING_SOURCE, bannedUsages);
    }

    private NGramMap initNGramMap(Scanner in, int N) {
        IDictionary<NGram, IDictionary<IterableString, Integer>> newOuter = new ChainingHashDictionary<>(
                MoveToFrontDictionary::new);
        Supplier<IDictionary<IterableString, Integer>> newInner = () -> new ChainingHashDictionary<>(
                MoveToFrontDictionary::new);
        return new NGramMap(in, N, newOuter, newInner);
    }

    @Test
    @Tag("B")
    @Order(2)
    @DisplayName("Test text containing a single NGram")
    public void testSingleNGram() {
        String text = "One two three";
        NGramMap map = initNGramMap(new Scanner(text), 2);
        IPriorityQueue.PQElement<String>[] items = map.getCountsAfter(new NGram("One two"));
        assertEquals(1, items.length);
        assertEquals("three", items[0].data);
        assertEquals(1, items[0].priority);
    }

    @Test
    @Tag("B")
    @Order(3)
    @DisplayName("Test text containing multiple NGrams")
    public void testMultipleNGram() {
        String text = "a a a a a a a a a a a a";
        NGramMap map = initNGramMap(new Scanner(text), 2);
        IPriorityQueue.PQElement<String>[] items = map.getCountsAfter(new NGram("a a"));
        assertEquals(1, items.length);
        assertEquals("a", items[0].data);
        assertEquals(10, items[0].priority);
    }

    @Test
    @Tag("B")
    @Order(4)
    @DisplayName("Test getCountsAfter on a nonexistent NGram")
    public void testNonexistentNGram() {
        NGramMap map = initNGramMap(new Scanner("blah blah"), 2);
        assertNull(map.getCountsAfter(new NGram("not in")));
    }

    @Test
    @Tag("B")
    @Order(5)
    @DisplayName("Test seenWordAfter")
    public void testSeenWordAfter() {
        String text = "The quick brown fox jumps over the lazy dog";
        NGramMap map = initNGramMap(new Scanner(text), 2);

        map.updateCount(new NGram("The quick"), "brown");
        IPriorityQueue.PQElement<String>[] items = map.getCountsAfter(new NGram("The quick"));
        assertEquals("brown", items[0].data);
        assertEquals(2, items[0].priority);

        map.updateCount(new NGram("fox jumps"), "nonexistent");
        items = map.getCountsAfter(new NGram("fox jumps"));
        assertEquals(2, items.length);
        for (int i = 0; i < items.length; i++) {
            if (items[i].data.equals("over")) {
                assertEquals(1, items[i].priority);
            } else {
                assertEquals("nonexistent", items[i].data);
                assertEquals(1, items[i].priority);
            }
        }
    }

    @Test
    @Tag("B")
    @Order(6)
    @DisplayName("Test getWordsAfter")
    public void testGetWordsAfter() {
        String text = "The quick brown fox jumps over the lazy dog";
        NGramMap map = initNGramMap(new Scanner(text), 2);
        map.updateCount(new NGram("The quick"), "brown");
        map.updateCount(new NGram("fox jumps"), "nonexistent");
        map.updateCount(new NGram("fox jumps"), "nonexistent");

        // Check nonexistent behavior
        String[] afterNull = map.getWordsAfter(new NGram("lol fake"), 1);
        assertNotNull(afterNull, "getWordsAfter() returns a null value, not a String array");
        assertEquals(0, afterNull.length);

        String[] afterQuick = map.getWordsAfter(new NGram("The quick"), 2);
        assertEquals(1, afterQuick.length);
        assertEquals("brown", afterQuick[0]);

        String[] afterJumps = map.getWordsAfter(new NGram("fox jumps"), 4);
        assertEquals(2, afterJumps.length);
        assertEquals("nonexistent", afterJumps[0]);
        assertEquals("over", afterJumps[1]);
    }}