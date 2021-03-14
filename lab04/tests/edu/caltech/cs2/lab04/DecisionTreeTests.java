package edu.caltech.cs2.lab04;

import edu.caltech.cs2.helpers.Inspection;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static edu.caltech.cs2.lab04.DecisionTree.id3;
import static edu.caltech.cs2.lab04.TitanicDecisionTreeMain.score;
import static edu.caltech.cs2.lab04.TitanicDatasetLoader.loadTrainingTitanicDataset;
import static edu.caltech.cs2.lab04.TitanicDatasetLoader.loadValidationTitanicDataset;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DecisionTreeTests {

    private static final String DECISION_TREE_SOURCE = "src/edu/caltech/cs2/lab04/DecisionTree.java";

    @Tag("A")
    @Test
    @DisplayName("Does not use or import disallowed classes")
    @Order(0)
    public void testForInvalidClasses() {
        List<String> regexps = List.of("java\\.lang\\.reflect", "java\\.io", "javax\\.swing");
        Inspection.assertNoImportsOf(DECISION_TREE_SOURCE, regexps);
        Inspection.assertNoUsageOf(DECISION_TREE_SOURCE, regexps);
    }

    @Tag("A")
    @Test
    @DisplayName("The only field is the root")
    @Order(1)
    public void testNoFields() {
        assertEquals(1, DecisionTree.class.getDeclaredFields().length, "You may only have the root as a field.");
    }

    public DecisionTree constructTennisTree() {
        return new DecisionTree(
                new AttributeNode(
                        "Weather", Map.of(
                        "Sunny", new AttributeNode(
                                "Humidity", Map.of(
                                "High", new OutcomeNode("No"),
                                "Normal", new OutcomeNode("Yes"))
                        ),
                        "Cloudy", new OutcomeNode("Yes"),
                        "Rainy", new AttributeNode(
                                "Wind", Map.of(
                                "Strong", new OutcomeNode("No"),
                                "Weak", new OutcomeNode("Yes")
                        ))
                )
                )
        );
    }

    public static Stream<Arguments> tennisNodeProvider() {
        return Stream.of(
                Arguments.of(Map.of("Weather", "Sunny", "Humidity", "High", "Wind", "Strong"), "No"),
                Arguments.of(Map.of("Weather", "Sunny", "Humidity", "High", "Wind", "Weak"), "No"),
                Arguments.of(Map.of("Weather", "Sunny", "Humidity", "Normal", "Wind", "Strong"), "Yes"),
                Arguments.of(Map.of("Weather", "Sunny", "Humidity", "Normal", "Wind", "Weak"), "Yes"),
                Arguments.of(Map.of("Weather", "Cloudy", "Humidity", "High", "Wind", "Strong"), "Yes"),
                Arguments.of(Map.of("Weather", "Cloudy", "Humidity", "High", "Wind", "Weak"), "Yes"),
                Arguments.of(Map.of("Weather", "Cloudy", "Humidity", "Normal", "Wind", "Strong"), "Yes"),
                Arguments.of(Map.of("Weather", "Cloudy", "Humidity", "Normal", "Wind", "Weak"), "Yes"),
                Arguments.of(Map.of("Weather", "Rainy", "Humidity", "High", "Wind", "Strong"), "No"),
                Arguments.of(Map.of("Weather", "Rainy", "Humidity", "High", "Wind", "Weak"), "Yes"),
                Arguments.of(Map.of("Weather", "Rainy", "Humidity", "Normal", "Wind", "Strong"), "No"),
                Arguments.of(Map.of("Weather", "Rainy", "Humidity", "Normal", "Wind", "Weak"), "Yes")
        );
    }

    @Tag("A")
    @ParameterizedTest(name = "Test predict() on point={0}; outcome={1}")
    @MethodSource("tennisNodeProvider")
    @DisplayName("Test predict() on a small tree")
    @Order(2)
    public void testPredict(Map<String, String> attributes, String outcome) {
        DecisionTree tree = constructTennisTree();
        String result = tree.predict(new Dataset.Datapoint(attributes, ""));

        assertEquals(outcome, result);
    }

    private static double evaluateSet(boolean val) {
        Dataset titanicTrain = loadTrainingTitanicDataset();
        List<String> attributes = List.of("Age", "Embarked", "Fare", "Parch", "Pclass", "Sex", "SibSp");
        Dataset titanicVal = loadValidationTitanicDataset();
        DecisionTree tree = id3(titanicTrain, attributes);

        return val ? score(tree, titanicVal) : score(tree, titanicTrain);
    }

    @Tag("A")
    @Test
    @DisplayName("Check that accuracy on training set >= 88%")
    @Order(3)
    public void testTrainingAccuracy() {
        double result = evaluateSet(false);
        assertTrue(result >= 0.88, "Training accuracy is only " + result + ", expected >= 0.88");
    }

    @Tag("A")
    @Test
    @DisplayName("Check that accuracy on validation set >= 79%")
    @Order(4)
    public void testValidationAccuracy() {
        double result = evaluateSet(true);
        assertTrue(result >= 0.79, "Validation accuracy is only " + result + ", expected >= 0.79");
    }
}
