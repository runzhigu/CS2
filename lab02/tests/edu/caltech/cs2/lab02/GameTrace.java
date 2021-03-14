package edu.caltech.cs2.lab02;

import edu.caltech.cs2.helpers.CaptureSystemOutput;
import edu.caltech.cs2.helpers.FileSource;
import edu.caltech.cs2.helpers.Reflection;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("edu.caltech.cs2.lab02.Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@CaptureSystemOutput
public class GameTrace {

    @Order(1)
    @ParameterizedTest
    @Tag("A")
    @DisplayName("Test full Adventure Game")
    @FileSource(
            inputs = {
                    "{stdin = correctInput.txt}",
                    "{stdin = incorrectInput.txt}",
            },
            outputFiles = {
                    "correctAnswers.txt",
                    "incorrectAnswers.txt"
            }
    )
    public void testAdventureGame(Map<String, String> arguments, String expectedOutput, CaptureSystemOutput.OutputCapture capture)
            throws FileNotFoundException {
        runTestGame(arguments.get("stdin"));
        assertEquals(expectedOutput.replace("\r\n", "\n").strip(), capture.toString().replace("\r\n", "\n").strip());
    }

    public void runTestGame(String filePath) throws FileNotFoundException {
        Scanner scan = new Scanner(new File("tests/data/" + filePath));
        Constructor<AdventureGame> constructor = Reflection.getConstructor(AdventureGame.class, Scanner.class);
        Field rand = Reflection.getFieldByType(AdventureGame.class, Random.class);
        Reflection.<Random>getFieldValue(AdventureGame.class, rand.getName(), null).setSeed(1337);
        AdventureGame game = Reflection.newInstance(constructor, scan);
        game.play();
    }
}
