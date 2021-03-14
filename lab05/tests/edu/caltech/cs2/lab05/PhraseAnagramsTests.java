package edu.caltech.cs2.lab05;

import edu.caltech.cs2.helpers.CaptureSystemOutput;
import edu.caltech.cs2.helpers.FileSource;
import edu.caltech.cs2.helpers.Inspection;
import edu.caltech.cs2.helpers.Reflection;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@CaptureSystemOutput
public class PhraseAnagramsTests {
  private static String SOURCE_FILE = "src/edu/caltech/cs2/lab05/AnagramGenerator.java";

  private static List<String> getDictionary() {
    try {
      Scanner input = new Scanner(new File("dictionaries/dictionary.txt"));
      // Read dictionary into a List
      List<String> dictionary = new ArrayList<String>();
      while (input.hasNextLine()) {
        dictionary.add(input.nextLine());
      }
      return dictionary;
    } catch (FileNotFoundException e) {
      fail("Dictionary file not found");
    }
    return null;
  }

  private static List<String> getDictionaryEleven() {
    try {
      Scanner input = new Scanner(new File("dictionaries/eleven.txt"));
      // Read dictionary into a List
      List<String> dictionary = new ArrayList<String>();
      while (input.hasNextLine()) {
        dictionary.add(input.nextLine());
      }
      return dictionary;
    } catch (FileNotFoundException e) {
      fail("Dictionary file not found");
    }
    return null;
  }

  @Order(0)
  @Tag("B")
  @DisplayName("Does not use or import disallowed classes")
  @Test
  public void testForInvalidClasses() {
    List<String> regexps = List.of("java\\.lang\\.reflect", "java\\.io");
    Inspection.assertNoImportsOf(SOURCE_FILE, regexps);
    Inspection.assertNoUsageOf(SOURCE_FILE, regexps);
  }

  @Order(1)
  @Tag("B")
  @DisplayName("AnagramGenerator has no instance fields")
  @Test
  public void testNoFields() {
    Reflection.assertFieldsLessThan(AnagramGenerator.class, "private", 1);
    Reflection.assertFieldsLessThan(AnagramGenerator.class, "public", 1);
  }

  @Order(2)
  @Tag("B")
  @DisplayName("Test printPhrases method in AnagramGenerator")
  @ParameterizedTest(name = "{0}")
  @FileSource(
          inputs = {
                  "meat",
                  "listen",
                  "magikarp",
                  "qwertyuiop",
          },
          outputFiles = {
                  "phrase_meat.txt",
                  "phrase_listen.txt",
                  "phrase_magikarp.txt",
                  "phrase_qwertyuiop.txt",
          }
  )
  public void testPrintPhrases(String word, String expectedOutput, CaptureSystemOutput.OutputCapture capture) {
    // Load the dictionary from the file
    List<String> dictionary = getDictionary();

    AnagramGenerator.printPhrases(word, dictionary);
    assertEquals(expectedOutput.replace("\r\n", "\n").strip(), capture.toString().replace("\r\n", "\n").strip());
  }

  @Order(3)
  @Tag("B")
  @DisplayName("Test printPhrases method on empty string")
  @Test
  public void testPrintPhrasesEmpty(CaptureSystemOutput.OutputCapture capture) {
    // Load the dictionary from the file
    List<String> dictionary = getDictionary();

    AnagramGenerator.printPhrases("", dictionary);
    assertEquals("[]", capture.toString().replace("\r\n", "\n").strip());
  }

  @Order(4)
  @Tag("B")
  @DisplayName("Test printPhrases method on null string")
  @Test
  public void testPrintPhrasesNull() {
    // Load the dictionary from the file
    List<String> dictionary = getDictionary();

    assertThrows(NullPointerException.class, ()->{AnagramGenerator.printPhrases(null, dictionary);});
  }

  @Order(5)
  @Tag("B")
  @DisplayName("Test printPhrases method on string with a space")
  @Test
  public void testPrintPhrasesSpace(CaptureSystemOutput.OutputCapture capture) {
    // Load the dictionary from the file
    List<String> dictionary = getDictionary();

    AnagramGenerator.printPhrases(" ", dictionary);
    assertEquals("[]", capture.toString().replace("\r\n", "\n").strip());
  }

  @Order(6)
  @Tag("B")
  @DisplayName("Test printPhrases method on string with no anagrams")
  @Test
  public void testPrintPhrasesNone(CaptureSystemOutput.OutputCapture capture) {
    // Load the dictionary from the file
    List<String> dictionary = getDictionary();

    AnagramGenerator.printPhrases("xyz", dictionary);
    assertEquals("", capture.toString().replace("\r\n", "\n").strip());
  }

  @Order(7)
  @Tag("B")
  @DisplayName("Test printPhrases method on a phrase")
  @ParameterizedTest(name = "{0}")
  @FileSource(
          inputs = {
                  "eleven plus two",
                  "two plus eleven",
          },
          outputFiles = {
                  "phrase_eleven.txt",
                  "phrase_eleven.txt",
          }
  )
  public void testPrintWordsEleven(String word, String expectedOutput, CaptureSystemOutput.OutputCapture capture) {
    // Load the dictionary from the file
    List<String> dictionary = getDictionaryEleven();

    AnagramGenerator.printPhrases(word, dictionary);
    assertEquals(expectedOutput.replace("\r\n", "\n").strip(), capture.toString().replace("\r\n", "\n").strip());
  }
}