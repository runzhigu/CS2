package edu.caltech.cs2.lab01;

import edu.caltech.cs2.lab01.Level0;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Level0Tests {
    @Order(0)
    @Tag("C")
    @DisplayName("Test Level0")
    @Test
    public void testLevel0() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        Level0.main(null);
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        String output = outputStream.toString();
        assertEquals("HELLO!", output.trim());
    }
}
