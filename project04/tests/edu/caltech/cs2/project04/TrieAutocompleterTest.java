package edu.caltech.cs2.project04;

import edu.caltech.cs2.helpers.Inspection;
import edu.caltech.cs2.interfaces.IDeque;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TrieAutocompleterTest {
    private static String STRING_SOURCE = "src/edu/caltech/cs2/project04/TrieMovieAutoCompleter.java";

    static {
        TrieMovieAutoCompleter.populateTitles();
    }

    @Order(1)
    @Tag("B")
    @DisplayName("Tests complete(String val)")
    @ParameterizedTest(name = "Test complete(\"{0}\")")
    @CsvSource({
            "mission: impossible, test_mission_impossible",
            "the avengers, test_the_avengers",
            "this, test_this",
            "age of, test_age_of"
    })
    void complete(String lookup, String res_file) throws IOException {
        IDeque<String> suggestions = TrieMovieAutoCompleter.complete(lookup);
        List<String> expected = new ArrayList<>();

        Scanner fr = new Scanner(new File("data/" + res_file));

        while (fr.hasNextLine()) {
            String title = fr.nextLine();
            expected.add(title);
        }

        MatcherAssert.assertThat(suggestions,
                IsIterableContainingInAnyOrder.containsInAnyOrder(expected.toArray()));

    }

    @Order(0)
    @Tag("B")
    @DisplayName("Does not use or import disallowed classes")
    @Test
    public void testForInvalidClasses() {
        // You may not import anything from java.uil except for these classes
        List<String> regexps = List.of("java\\.util\\.(?!Arrays|HashSet|Set)",
                "java\\.lang\\.reflect", "java\\.io");
        Inspection.assertNoImportsOf(STRING_SOURCE, regexps);
        Inspection.assertNoUsageOf(STRING_SOURCE, regexps);
    }
}