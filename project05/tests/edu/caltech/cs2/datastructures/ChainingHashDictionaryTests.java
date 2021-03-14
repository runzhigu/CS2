package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.helpers.Inspection;
import edu.caltech.cs2.helpers.Reflection;
import edu.caltech.cs2.helpers.RuntimeInstrumentation;
import edu.caltech.cs2.interfaces.IDictionary;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static edu.caltech.cs2.project05.Project05TestOrdering.classSpecificTestLevel;
import static edu.caltech.cs2.project05.Project05TestOrdering.specialTestLevel;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("B")
public class ChainingHashDictionaryTests implements IDictionaryNGramTests {
    private static String DICTIONARY_SOURCE = "src/edu/caltech/cs2/datastructures/ChainingHashDictionary.java";

    public Counter supplierCounter = new Counter();

    @Override
    public int SINGLE_OP_TIMEOUT_MS() {
        return 100;
    }

    @Override
    public int CONTAINS_VALUE_TIMEOUT_MS() {
        return 100;
    }

    @Override
    public RuntimeInstrumentation.ComplexityType getAndPutComplexity() {
        return RuntimeInstrumentation.ComplexityType.CONSTANT;
    }

    @Override
    public IDictionary<Object, Object> newIDictionary() {
        Supplier<IDictionary<Object, Object>> sup = () -> {
            this.supplierCounter.touchCounter();
            return new MoveToFrontDictionary<>();
        };
        return new ChainingHashDictionary<>(sup);
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
        List<String> allowed = List.of("Iterator", "function\\.Supplier", "stream\\.Stream");
        Inspection.assertNoImportsOfExcept(DICTIONARY_SOURCE, "java\\.util", allowed);

        List<String> bannedUsages = List.of("java\\.util\\.(?!" + String.join("|", allowed) + ")");
        Inspection.assertNoUsageOf(DICTIONARY_SOURCE, bannedUsages);
    }

    @Order(classSpecificTestLevel)
    @DisplayName("There are no public fields")
    @Test
    public void testNoPublicFieldsCHD() {
        Reflection.assertNoPublicFields(ChainingHashDictionary.class);
    }

    @Order(classSpecificTestLevel)
    @DisplayName("There are no protected fields")
    @Test
    public void testNoProtectedFieldsCHD() {
        Reflection.assertNoProtectedFields(ChainingHashDictionary.class);
    }

    @Order(classSpecificTestLevel)
    @DisplayName("All fields in ChainingHashDictionary have modifiers")
    @Test
    public void testFieldModifiersCHD() {
        Reflection.assertFieldModifiers(ChainingHashDictionary.class);
    }

    @Order(specialTestLevel)
    @DisplayName("Test that resizing and initializing is fast")
    @Timeout(value = 20, unit = SECONDS)
    @Test
    public void testGrowthCapability() {
        for (int i = 0; i < 25; i++) {
            IDictionary<Object, Object> dict = new ChainingHashDictionary<>(MoveToFrontDictionary::new);
            for (int j = 0; j < 500000; j++) {
                dict.put(j, j);
            }
            // This _should_ get GC'd with a smaller heap size...
        }
    }

    @Order(specialTestLevel)
    @DisplayName("Test that hash map is resized appropriately")
    @Test
    public void testResize() {
        List<Counter> values = new ArrayList<>();
        IDictionary<Object, Object> impl = newIDictionary();
        for (int i = 0; i < 100000; i++) {
            Counter c = new Counter(i);
            impl.put(c, i);
            // Insert newer counters at front so number of touches is ascending
            values.add(0, c);
        }

        // All counters should have been touched at least once
        int currMaxTouches = 1;
        for (Counter c : values) {
            assertTrue(c.touches >= currMaxTouches, "Key was not hashed enough times when rebuilding dictionary");
            currMaxTouches = Math.max(c.touches, currMaxTouches);
        }

        assertTrue(currMaxTouches > 10, "Dictionary was not resized enough times when building");
        // Compare to 2 - last insertion could have triggered resize (unlikely, but
        // might as well - important part
        // is ensuring an upper bound.
        assertTrue(values.get(0).touches <= 2, "Most recent key inserted was hashed too many times");
    }

    @Order(specialTestLevel)
    @DisplayName("Test that supplier function is used in resize")
    @Test
    public void testSupplierUsage() {
        // Reset from any previous tests
        this.supplierCounter.resetCounter();
        IDictionary<Object, Object> impl = newIDictionary();

        for (int i = 0; i < 100000; i++) {
            impl.put(i, i);
        }

        // Number of resized was arbitrarily chosen, but this *should* be fine?
        assertTrue(this.supplierCounter.touches > 50000, "Supplier was not used enough during resizes");
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
