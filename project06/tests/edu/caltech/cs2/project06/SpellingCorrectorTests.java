package edu.caltech.cs2.project06;

import edu.caltech.cs2.datastructures.*;
import edu.caltech.cs2.helpers.Inspection;
import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.misc.CorrectionChoice;
import edu.caltech.cs2.types.NGram;
import org.junit.jupiter.api.*;
import wordcorrector.SpellingCorrector;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SpellingCorrectorTests {
    private static final String DICT_FILENAME = "./data/dictionary.txt";
    private static final String EDIT_DIST_FILENAME = "./data/edit_distance_test";
    private static final String POSSIBLE_CORRECTIONS_DIRECTORY = "./data/possible_corrections";
    private static final String ALICE_FILENAME = "./data/alice";
    private static final String BEST_FILENAME = "./data/best_corrections";
    private static final int EDIT_DIST_TIMEOUT = 80;

    private static String STRING_SOURCE = "src/wordcorrector/SpellingCorrector.java";
    
    @Order(0)
    @DisplayName("Does not use or import disallowed classes")
    @Test
    @Tag("A")
    public void testForInvalidClasses() {
        List<String> regexps = List.of("java.lang.reflect", "java.io");
        Inspection.assertNoImportsOf(STRING_SOURCE, regexps);
        Inspection.assertNoUsageOf(STRING_SOURCE, regexps);
    }

    @Order(0)
    @DisplayName("Does not use or import disallowed classes from java.util")
    @Test
    @Tag("A")
    public void testForInvalidClassesUtil() {
        List<String> allowed = List.of("Iterator");
        Inspection.assertNoImportsOfExcept(STRING_SOURCE, "java\\.util", allowed);

        List<String> bannedUsages = List.of("java\\.util\\.(?!" + String.join("|", allowed) + ")");
        Inspection.assertNoUsageOf(STRING_SOURCE, bannedUsages);
    }

    private NGramMap initNGramMap(Scanner in, int N) {
        IDictionary<NGram, IDictionary<IterableString, Integer>> newOuter = new ChainingHashDictionary<>(MoveToFrontDictionary::new);
        Supplier<IDictionary<IterableString, Integer>> newInner = () -> new ChainingHashDictionary<>(MoveToFrontDictionary::new);
        return new NGramMap(in, N, newOuter, newInner);
    }

    @Test
    @Tag("A")
    @Order(1)
    @DisplayName("Test correctness and speed of editDistance")
    public void testEditDistance() {
        assertTimeout(Duration.ofMillis(EDIT_DIST_TIMEOUT), this::testEditDistanceHelper);
    }

    private void testEditDistanceHelper() {
        Scanner in;
        try {
            in = new Scanner(new File(EDIT_DIST_FILENAME));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not find edit distance test file!");
        }
        while (in.hasNext()) {
            String a = in.next();
            String b = in.next();
            assertEquals(in.nextInt(), SpellingCorrector.editDistance(a, b));
        }
    }

    @Test
    @Tag("A")
    @Order(2)
    @DisplayName("Test possibleCorrections")
    public void testPossibleCorrections() {
        File folder = new File(POSSIBLE_CORRECTIONS_DIRECTORY);
        File[] listOfFiles = folder.listFiles();
        for (File f : listOfFiles) {
            testPossibleCorrection(f);
        }
    }

    private void testPossibleCorrection(File correctionsFile) {
        SpellingCorrector corrector = new SpellingCorrector(DICT_FILENAME);
        // retrieve the correct choices from the file
        IDictionary<String, CorrectionChoice> correctChoices = new ChainingHashDictionary<String, CorrectionChoice>(
                MoveToFrontDictionary::new);

        Scanner correctChoiceFile;
        try {
            correctChoiceFile = new Scanner(correctionsFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not find possible corrections file!");
        }
        String misspelledWord = correctChoiceFile.nextLine();
        ICollection<CorrectionChoice> choices = corrector.getPossibleCorrections(misspelledWord);
        while (correctChoiceFile.hasNext()) {
            String[] elements = correctChoiceFile.nextLine().split(":");
            correctChoices.put(elements[0], new CorrectionChoice(elements[0], Integer.parseInt(elements[1]),
                    Integer.parseInt(elements[2])));
        }

        // check that we got the right number of corrections
        assertEquals(correctChoices.size(), choices.size());
        // check that each of the choices we got was present in the correct ones
        for (CorrectionChoice choice : choices) {
            assertTrue(correctChoices.containsKey(choice.word));
            assertEquals(correctChoices.get(choice.word), choice);
        }
    }

    @Test
    @Tag("A")
    @Order(3)
    @DisplayName("Test bestCorrection")
    public void testBestCorrection () {
        // test best corrections
        SpellingCorrector corrector = new SpellingCorrector(DICT_FILENAME);
        Scanner bestFile;
        try {
            bestFile = new Scanner(new File(BEST_FILENAME));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not find best correction test file!");
        }

        while (bestFile.hasNext()) {
            // Initialize NGramMap
            Scanner aliceFile;
            try {
                aliceFile = new Scanner(new File(ALICE_FILENAME));
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Could not find alice!");
            }
            NGramMap map = initNGramMap(aliceFile, 3);

            String[] elements = bestFile.nextLine().split(",");
            assertEquals(elements[0], corrector.getBestCorrection(map,
                    elements[1], elements[2].strip(), 10));
        }
    }
}
