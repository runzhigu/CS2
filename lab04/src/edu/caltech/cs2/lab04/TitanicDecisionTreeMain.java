package edu.caltech.cs2.lab04;

import java.util.List;

import static edu.caltech.cs2.lab04.DecisionTree.id3;
import static edu.caltech.cs2.lab04.TitanicDatasetLoader.loadTrainingTitanicDataset;
import static edu.caltech.cs2.lab04.TitanicDatasetLoader.loadValidationTitanicDataset;

public class TitanicDecisionTreeMain {

    /**
     * Compute the accuracy of a decision tree over a given dataset
     * @param tree    the decision tree
     * @param dataset dataset of points to evaluate against
     * @return the accuracy of the decision tree over dataset, or the proportion of correct predictions
     */
    public static double score(DecisionTree tree, Dataset dataset) {
        int correctPred = 0;
        for (Dataset.Datapoint p : dataset) {
            if (tree.predict(p).equals(p.outcome)) {
                correctPred++;
            }
        }
        return (double) correctPred / dataset.size();
    }


    public static void main(String[] args) {
        Dataset titanicTrain = loadTrainingTitanicDataset();
        List<String> attributes = List.of("Age", "Embarked", "Fare", "Parch", "Pclass", "Sex", "SibSp");
        DecisionTree tree = id3(titanicTrain, attributes);
        System.out.println("Train accuracy:\t" + score(tree, titanicTrain));

        Dataset titanicVal = loadValidationTitanicDataset();
        System.out.println("Val accuracy:\t" + score(tree, titanicVal));
    }
}
