package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.helpers.Inspection;
import edu.caltech.cs2.helpers.Reflection;
import edu.caltech.cs2.helpers.RuntimeInstrumentation;
import edu.caltech.cs2.interfaces.IDictionary;
import org.junit.jupiter.api.*;

import java.util.List;

import static edu.caltech.cs2.project05.Project05TestOrdering.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("A")
public class BSTDictionaryTests implements IDictionaryNGramTests {
    @Override
    public int SINGLE_OP_TIMEOUT_MS() {
        return 50;
    }

    @Override
    public int CONTAINS_VALUE_TIMEOUT_MS() {
        return 100;
    }

    @Override
    public RuntimeInstrumentation.ComplexityType getAndPutComplexity() {
        return RuntimeInstrumentation.ComplexityType.LOGARITHMIC;
    }

    @Override
    public IDictionary<Object, Object> newIDictionary() {
        return (IDictionary<Object, Object>) (IDictionary<? extends Object, Object>) newBSTDictionary();
    }

    public BSTDictionary<Comparable<Object>, Object> newBSTDictionary() {
        return new BSTDictionary<>();
    }

    private static String DICTIONARY_SOURCE =
            "src/edu/caltech/cs2/datastructures/BSTDictionary.java";

    @Order(classSpecificTestLevel)
    @DisplayName("Does not use or import disallowed classes")
    @Test
    public void testForInvalidClasses() {
        List<String> regexps = List.of("java\\.lang\\.reflect", "java\\.io");
        Inspection.assertNoImportsOf(DICTIONARY_SOURCE, regexps);
        Inspection.assertNoUsageOf(DICTIONARY_SOURCE, regexps);
    }

    @Order(classSpecificTestLevel)
    @DisplayName("Does not use or import disallowed classes from java.util")
    @Test
    public void testForInvalidImportsJavaUtil() {
        List<String> allowed = List.of("Iterator");
        Inspection.assertNoImportsOfExcept(DICTIONARY_SOURCE, "java\\.util", allowed);

        List<String> bannedUsages = List.of("java\\.util\\.(?!" + String.join("|", allowed) + ")");
        Inspection.assertNoUsageOf(DICTIONARY_SOURCE, bannedUsages);
    }


    @Order(classSpecificTestLevel)
    @DisplayName("There are no public fields")
    @Test
    public void testNoPublicFields() {
        Reflection.assertNoPublicFields(BSTDictionary.class);
    }

    @Test
    @Order(sanityTestLevel)
    @DisplayName("Test nonbalancing BST Implementation")
    public void testActualBST() {
        BSTDictionary<String, Integer> bst =  new BSTDictionary<>();

        bst.put("m", 1);
        bst.put("s", 2);
        bst.put("x", 3);
        bst.put("i", 4);
        bst.put("a", 5);
        bst.put("p", 6);
        bst.put("u", 7);
        bst.put("y", 8);
        bst.put("t", 9);

        assertEquals("{m: 1, i: 4, s: 2, a: 5, p: 6, x: 3, u: 7, y: 8, t: 9}", bst.toString(), "Incorrect binary search tree implementation");

    }
}
