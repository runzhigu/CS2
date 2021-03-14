package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.helpers.Inspection;
import edu.caltech.cs2.helpers.MoveToFrontChecker;
import edu.caltech.cs2.helpers.Reflection;
import edu.caltech.cs2.helpers.RuntimeInstrumentation;
import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDictionary;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static edu.caltech.cs2.project05.Project05TestOrdering.classSpecificTestLevel;
import static edu.caltech.cs2.project05.Project05TestOrdering.specialTestLevel;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("B")
public class MoveToFrontDictionaryTests implements IDictionaryNGramTests {
    private static String DICTIONARY_SOURCE = "src/edu/caltech/cs2/datastructures/MoveToFrontDictionary.java";
    public int CONSTANT_TIMEOUT_MS = 15;

    public int SINGLE_OP_TIMEOUT_MS() {
        return 100;
    }

    public int CONTAINS_VALUE_TIMEOUT_MS() {
        return 100;
    }

    @Override
    public RuntimeInstrumentation.ComplexityType getAndPutComplexity() {
        return RuntimeInstrumentation.ComplexityType.LINEAR;
    }

    // No restrictions on key type for MoveToFront
    @Override
    public IDictionary<Object, Object> newIDictionary() {
        return new MoveToFrontDictionary<>();
    }

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
    public void testNoPublicFieldsMTF() {
        Reflection.assertNoPublicFields(MoveToFrontDictionary.class);
    }

    @Order(classSpecificTestLevel)
    @DisplayName("There are no protected fields")
    @Test
    public void testNoProtectedFieldsMTF() {
        Reflection.assertNoProtectedFields(MoveToFrontDictionary.class);
    }

    @Order(classSpecificTestLevel)
    @DisplayName("All fields in MoveToFrontDictionary have modifiers")
    @Test
    public void testFieldModifiersMTF() {
        Reflection.assertFieldModifiers(MoveToFrontDictionary.class);
    }

    @Order(classSpecificTestLevel)
    @DisplayName("Check for linked node class")
    @Test
    public void testLinkedNode() {
        Class[] classes = MoveToFrontDictionary.class.getDeclaredClasses();
        for (Class clazz : classes) {
            if (Iterator.class.isAssignableFrom(clazz)) {
                continue;
            }
            MoveToFrontChecker.isNode(clazz);
        }
    }

    @Order(classSpecificTestLevel)
    @DisplayName("Check MoveToFrontDictionary class is properly implemented")
    @Test
    public void checkMTF() {
        MoveToFrontChecker.checkClass(MoveToFrontDictionary.class);
    }

    @Test
    @DisplayName("Sanity check that accessing keys in various locations in the dictionary works")
    @Order(classSpecificTestLevel)
    public void testDataLocations() {
        MoveToFrontDictionary<Integer, Integer> impl = new MoveToFrontDictionary<>();
        HashMap<Integer, Integer> ref = new HashMap<>();

        for (int i = 0; i < 10; i++) {
            impl.put(i, i);
            ref.put(i, i);
        }

        // Check access of element at front
        assertEquals(ref.get(9), impl.get(9), "Getting an element from the front failed.");

        // Check accessing whatever element is at the back
        for (int i = 0; i < 10; i++) {
            assertEquals(ref.get(i), impl.get(i), "Getting an element from the back returns incorrect result.");
            assertEquals(ref.get(i), impl.get(i), "Key that was just gotten is now missing.");
        }

        // Check removing element at the front
        for (int i = 9; i >= 0; i--) {
            assertEquals(ref.remove(i), impl.remove(i), "Removing an element from the front failed.");
        }

        // Repopulate to make sure that emptying it didn't bork it
        for (int i = 0; i < 10; i++) {
            impl.put(i, i);
            ref.put(i, i);
        }

        // And repeat.
        assertEquals(ref.get(9), impl.get(9));
        for (int i = 0; i < 10; i++) {
            assertEquals(ref.get(i), impl.get(i), "Getting an element from the back failed.");
        }
    }

    @Test
    @DisplayName("Test that referencing a key moves it to the front")
    @Order(specialTestLevel)
    public void testMoveToFrontProperty() {
        MoveToFrontDictionary<Integer, Integer> impl = new MoveToFrontDictionary<>();
        final int DICT_SIZE = 30000;
        for (int i = 0; i <= DICT_SIZE; i++) {
            impl.put(i, i);
        }

        double totalTimeRetrieveFront = 0;
        long startTime, endTime;
        for (int i = 0; i <= DICT_SIZE; i++) {
            // Get key from back to move to front
            impl.containsKey(i);

            // Clock retrieval of key from front.
            startTime = System.nanoTime();
            impl.get(i);
            endTime = System.nanoTime();
            totalTimeRetrieveFront += (endTime - startTime);
        }
        assertTrue(CONSTANT_TIMEOUT_MS > totalTimeRetrieveFront / 1000000,
                "get(key) after calling containsKey(key) takes too long.");

        totalTimeRetrieveFront = 0;
        for (int i = 0; i <= DICT_SIZE; i++) {
            // Get key from back to move to front
            impl.get(i);

            // Clock retrieval of key from front.
            startTime = System.nanoTime();
            impl.get(i);
            endTime = System.nanoTime();
            totalTimeRetrieveFront += (endTime - startTime);
        }
        assertTrue(CONSTANT_TIMEOUT_MS > totalTimeRetrieveFront / 1000000,
                "get(key) after calling get(key) takes too long.");
    }

    @Order(specialTestLevel)
    @DisplayName("Test removing from the front has the desired behavior")
    @Test
    public void testFrontRemove() {
        MoveToFrontDictionary<Integer, Integer> impl = new MoveToFrontDictionary<>();
        final int DICT_SIZE = 1000;
        for (int i = 0; i <= DICT_SIZE; i++) {
            impl.put(i, i);
        }
        Class nodeClass = MoveToFrontChecker.getNodeClass(MoveToFrontDictionary.class);
        Field head = null;
        for (Field field : MoveToFrontDictionary.class.getDeclaredFields()) {
            if (field.getType().equals(nodeClass)) {
                field.setAccessible(true);
                head = field;
                break;
            }
        }
        assertNotNull(head, "There is no head field in MoveToFrontDictionary");
        for (int i = DICT_SIZE; i >= 0; i--) {
            assertEquals(i, impl.get(i), "Getting a key does not return the correct value");
            assertEquals(i, impl.remove(i), "Removing a key does not return the correct value");
            try {
                if (i != 0) {
                    assertNotNull(head.get(impl), "Removing from front leaves a null head pointer");
                } else {
                    assertNull(head.get(impl), "Removing last key does not set head pointer to null");
                }
                ICollection<Integer> values = impl.values();
                assertNull(impl.get(i), "Getting a removed key does not return null");
                assertIterableEquals(values, impl.values(), "Getting a removed key changes the value list");
                if (i != 0) {
                    assertNotNull(head.get(impl), "Getting a removed key leaves a null head pointer");
                } else {
                    assertNull(head.get(impl), "Getting the last key does leave a null head pointer");
                }
            } catch (IllegalAccessException ex) {
                fail("There is no head field in MoveToFrontDictionary");
            }
        }
    }

    // MoveToFrontDictionary takes quadratic time to build, so don't run standard
    // complexity tests.
    @Override
    public void testSizeComplexity() {
    }

    @Override
    public void testGetComplexity() {
    }

    @Override
    public void testPutComplexity() {
    }
}
