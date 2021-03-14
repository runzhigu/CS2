package edu.caltech.cs2.lab05;

import edu.caltech.cs2.helpers.CaptureSystemOutput;
import edu.caltech.cs2.helpers.FileSource;
import edu.caltech.cs2.helpers.Reflection;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@CaptureSystemOutput
public class WordAnagramsTests {

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


  @Order(1)
  @Tag("A")
  @DisplayName("AnagramGenerator has no instance fields")
  @Test
  public void testNoFields() {
    Reflection.assertFieldsLessThan(AnagramGenerator.class, "private", 1);
    Reflection.assertFieldsLessThan(AnagramGenerator.class, "public", 1);
  }

  @Order(2)
  @Tag("A")
  @DisplayName("Test printWords method in AnagramGenerator")
  @ParameterizedTest(name = "{0}")
  @FileSource(
          inputs = {
                  "meat",
                  "listen",
          },
          outputFiles = {
                  "word_meat.txt",
                  "word_listen.txt",
          }
  )
  public void testPrintWords(String word, String expectedOutput, CaptureSystemOutput.OutputCapture capture) {
    // Load the dictionary from the file
    List<String> dictionary = getDictionary();

    AnagramGenerator.printWords(word, dictionary);
    assertEquals(expectedOutput.replace("\r\n", "\n").strip(), capture.toString().replace("\r\n", "\n").strip());
  }

  @Order(3)
  @Tag("A")
  @DisplayName("Test printWords method on empty string")
  @Test
  public void testPrintWordsEmpty(CaptureSystemOutput.OutputCapture capture) {
    // Load the dictionary from the file
    List<String> dictionary = getDictionary();

    AnagramGenerator.printWords("", dictionary);
    assertEquals("", capture.toString().replace("\r\n", "\n").strip());
  }

  @Order(4)
  @Tag("A")
  @DisplayName("Test printWords method on null string")
  @Test
  public void testPrintWordsNull() {
    // Load the dictionary from the file
    List<String> dictionary = getDictionary();

    assertThrows(NullPointerException.class, ()->{AnagramGenerator.printWords(null, dictionary);});
  }

  @Order(5)
  @Tag("A")
  @DisplayName("Test printWords method on string with a space")
  @Test
  public void testPrintWordsSpace(CaptureSystemOutput.OutputCapture capture) {
    // Load the dictionary from the file
    List<String> dictionary = getDictionary();

    AnagramGenerator.printWords(" ", dictionary);
    assertEquals("", capture.toString().replace("\r\n", "\n").strip());
  }


  @Order(6)
  @Tag("A")
  @DisplayName("Test printWords method on string with no anagrams")
  @Test
  public void testPrintWordsNone(CaptureSystemOutput.OutputCapture capture) {
    // Load the dictionary from the file
    List<String> dictionary = getDictionary();

    AnagramGenerator.printWords("qwertyuiop", dictionary);
    assertEquals("", capture.toString().replace("\r\n", "\n").strip());
  }

  @Order(7)
  @Tag("A")
  @DisplayName("Test printWords method on smaller dictionary")
  @ParameterizedTest(name = "{0}")
  @FileSource(
          inputs = {
                  "one",
                  "plus",
                  "potato",
                  "twelve",
                  "won",
                  "zebra",
          },
          outputFiles = {
                  "word_one.txt",
                  "word_plus.txt",
                  "word_potato.txt",
                  "word_twelve.txt",
                  "word_won.txt",
                  "word_zebra.txt",
          }
  )
  public void testPrintWordsEleven(String word, String expectedOutput, CaptureSystemOutput.OutputCapture capture) {
    // Load the dictionary from the file
    List<String> dictionary = getDictionaryEleven();

    AnagramGenerator.printWords(word, dictionary);
    assertEquals(expectedOutput.replace("\r\n", "\n").strip(), capture.toString().replace("\r\n", "\n").strip());
  }
}