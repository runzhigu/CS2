package edu.caltech.cs2.lab01;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GradeHistogramTests {
    @Order(0)
    @Tag("D")
    @DisplayName("Test add/get")
    @Test
    public void testAddGet() {
        GradeHistogram h = new GradeHistogram();
        h.addGrade(1);
        h.addGrade(1);
        h.addGrade(1);
        h.addGrade(1);
        h.addGrade(1);
        h.addGrade(1);
        assertEquals(6, h.getFrequency(1));
    }

    @Order(1)
    @Tag("D")
    @DisplayName("Stress Test add/get")
    @Test
    public void stressTestAddGet() {
        List<Integer> correct = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            correct.add(0);
        }

        GradeHistogram h = new GradeHistogram();

        Random r = new Random(1337);

        for (int i = 0; i < 10000; i++) {
            int x = r.nextInt(10);
            h.addGrade(x);
            correct.set(x, correct.get(x) + 1);

            for (int j = 0; j < 10; j++) {
                assertEquals(correct.get(j), h.getFrequency(j));
            }
        }
    }

    @Order(2)
    @Tag("D")
    @Test
    @DisplayName("Test toString")
    public void testToString() {
        GradeHistogram h = new GradeHistogram();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < i; j++) {
                h.addGrade(i);
            }
        }
        assertEquals(
            "         *\n" +
            "        **\n" +
            "       ***\n" +
            "      ****\n" +
            "     *****\n" +
            "    ******\n" +
            "   *******\n" +
            "  ********\n" +
            " *********\n", h.toString());
    }

    @Order(3)
    @Tag("D")
    @Test
    @DisplayName("Test allowed grade range")
    public void testGradeRange() {
        GradeHistogram h = new GradeHistogram();
        for (int i = -100; i < 100; i++) {
            int grade = i;
            Executable addGrade = () -> h.addGrade(grade);
            if (0 <= grade && grade < 10) {
                assertDoesNotThrow(addGrade);
            }
            else {
                assertThrows(IllegalArgumentException.class, addGrade);
            }
        }
        for (int i = -100; i < 100; i++) {
            int grade = i;
            if (0 <= grade && grade < 10) {
                assertEquals(h.getFrequency(i), 1);
            }
            else {
                assertThrows(IllegalArgumentException.class, () -> h.getFrequency(grade));
            }
        }
    }
}
