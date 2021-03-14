package edu.caltech.cs2.lab01;

import edu.caltech.cs2.lab01.BombMain;
import org.junit.jupiter.api.*;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BombTests {
    @Order(0)
    @Tag("A")
    @DisplayName("Test BombMain")
    @Test
    public void testBombMain() {
        PrintStream systemErr = System.err;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outputStream));
        BombMain.main(null);
        System.setErr(systemErr);

        String output = outputStream.toString();
        System.out.println(output);
        String[] lines = output.split("\r?\n");
        assertEquals(54955992, lines[0].split("\"")[1].hashCode());
        assertEquals(103143, lines[1].split("\"")[1].hashCode());
        assertEquals(-1099484678, lines[2].split("\"")[1].split(" ")[5000].hashCode());
    }
}
