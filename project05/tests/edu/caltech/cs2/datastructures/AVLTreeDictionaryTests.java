package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.helpers.Inspection;
import edu.caltech.cs2.helpers.Reflection;
import edu.caltech.cs2.helpers.RuntimeInstrumentation;
import edu.caltech.cs2.interfaces.IDictionary;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;

import static edu.caltech.cs2.project05.Project05TestOrdering.classSpecificTestLevel;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("A+")
// Yes, strictly this should extend BSTDictionary. I don't have the time to reason through it at the moment, though.
public class AVLTreeDictionaryTests implements IDictionaryNGramTests {

    private static String DICTIONARY_SOURCE =
            "src/edu/caltech/cs2/datastructures/AVLTreeDictionary.java";

    @Override
    public IDictionary<Object, Object> newIDictionary() {
        return (IDictionary<Object, Object>) (IDictionary<? extends Object, Object>) newAVLTreeDictionary();
    }

    public AVLTreeDictionary<Comparable<Object>, Object> newAVLTreeDictionary() {
        return new AVLTreeDictionary<>();
    }


    @Override
    public int SINGLE_OP_TIMEOUT_MS() {
        return 60;
    }

    @Override
    public int CONTAINS_VALUE_TIMEOUT_MS() {
        return 120;
    }

    @Override
    public RuntimeInstrumentation.ComplexityType getAndPutComplexity() {
        return RuntimeInstrumentation.ComplexityType.LOGARITHMIC;
    }


    @Order(0)
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


    @Order(0)
    @DisplayName("There are no public fields")
    @Test
    public void testNoPublicFields() {
        Reflection.assertNoPublicFields(AVLTreeDictionary.class);
    }


    @Order(1)
    @DisplayName("The AVL tree self-balances")
    @Test
    public void testBalance() {
        AVLTreeDictionary<String, Integer> avl = new AVLTreeDictionary<>();

        // Left rotation
        avl.put("m", 1);
        avl.put("s", 2);
        avl.put("x", 3);
        assertEquals("{s: 2, m: 1, x: 3}", avl.toString());

        // Right rotation
        avl.put("i", 4);
        avl.put("a", 5);
        assertEquals("{s: 2, i: 4, x: 3, a: 5, m: 1}",
                avl.toString());

        // Left-right rotation
        avl.put("p", 6);
        assertEquals("{m: 1, i: 4, s: 2, a: 5, p: 6, x: 3}",
                avl.toString());

        // Right-left rotation
        avl.put("u", 7);
        avl.put("y", 8);
        avl.put("t", 9);
        assertEquals("{m: 1, i: 4, u: 7, a: 5, s: 2, x: 3, p: 6, t: 9, y: 8}",
                avl.toString());
    }

    // Do not test AVL remove
    @Override
    public void smokeTestIDictionaryRemove(Map<Object, Object> base) {}
    @Override
    public void stressTestIDictionaryRemove(int seed, int size) {}
}