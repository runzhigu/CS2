package edu.caltech.cs2.lab01;

import edu.caltech.cs2.lab01.Level1;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Level1Tests {
    @Order(0)
    @Tag("C")
    @DisplayName("Test Level1")
    @Test
    public void testLevel1() {
        Random r = new Random(1337);
        List<Integer> l1 = new ArrayList<>();
        List<Integer> l2 = new ArrayList<>();
        assertEquals(true, Level1.listsEqual(l1, l2));

        for (int i = 0; i < 1000; i++) {
            int ri = r.nextInt();
            l1.add(ri);
            l2.add(ri);
            assertTrue(Level1.listsEqual(l1, l2));
        }

        l1.add(5, 1);
        l2.add(5, 2);
        assertFalse(Level1.listsEqual(l1, l2));
    }
}


