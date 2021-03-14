package edu.caltech.cs2.lab04;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class DecisionTree {
    private final DecisionTreeNode root;

    public DecisionTree(DecisionTreeNode root) {
        this.root = root;
    }

    public String predict(Dataset.Datapoint point) {
        // start at root node
        DecisionTreeNode currNode = this.root;

        // base case if we are at an outcome node (i.e. not a terminal)
        if (currNode.isLeaf()) {
            return ((OutcomeNode) currNode).outcome;
        }

        else {
            // get current attribute
            String attribute = ((AttributeNode) currNode).attribute;

            // get map of children <feature of attribute, which child node>
            Map<String, DecisionTreeNode> children = ((AttributeNode) currNode).children;


            // loop thru features of current attribute
            for (String feature : children.keySet()) {
                // to determine which feature point has
                if (feature.equals(point.attributes.get(attribute))) {
                    // find the child node that this feature maps to
                    DecisionTreeNode childNode = children.get(feature);
                    // turn it into a decision tree to continue predicting ;; "CASTING"
                    DecisionTree childDecisionTree = new DecisionTree(childNode);

                    return childDecisionTree.predict(point);
                }
            }


        }
        return null;
    }

    public static DecisionTree id3(Dataset dataset, List<String> attributes) {

        // make sure attributes can be modified
        List<String> attributesAL = new ArrayList<>(attributes);

        /*
        ============ base cases for outcome nodes ===========
         */

        // if outcomes are all the same, it will be stored in knownOutcome
        String knownOutcome = dataset.pointsHaveSameOutcome();
        // if different outcomes, we'll have ""
        if (!knownOutcome.equals("")) {
            // make a new outcome node
            DecisionTreeNode root = new OutcomeNode(knownOutcome);
            // return a new decision tree, will be useful for recursion
            return new DecisionTree(root);
        }

        // if no remaining attributes (aka not perfect accuracy)
        else if (attributesAL.size() == 0) {
            String mostCommonOutcome = dataset.getMostCommonOutcome();
            // make a new outcome node
            DecisionTreeNode root = new OutcomeNode(mostCommonOutcome);
            // return a new decision tree, will be useful for recursion
            return new DecisionTree(root);
        }



        // find lowest entropy attribute
        String bestAttribute = dataset.getAttributeWithMinEntropy(attributesAL);
        // remove best attribute since we only need to consider the other attr for children
        attributesAL.remove(bestAttribute);

        // declare children Map<feature, corresponding child node>
        Map<String, DecisionTreeNode> children = new HashMap<>();

        // for each feature of attribute,
        for (String feature : dataset.getFeaturesForAttribute(bestAttribute)) {
            // get the set of points (Dataset object) that have that feature
            Dataset relevantPoints = dataset.getPointsWithFeature(feature);


            // if no points, guess (make outcome child with most common outcome)
            if (relevantPoints.size() == 0) {
                String mostCommonOutcome = dataset.getMostCommonOutcome();
                // make a new outcome node
                DecisionTreeNode childNode = new OutcomeNode(mostCommonOutcome);
                // and put it in the children map. No return yet since we're looping thru features
                children.put(feature, childNode);
            }


        /*
        =========== recursion =============
        */
            // else recursively make a child
            else {
                // for a specific feature (as codified by relevantPoints,
                // what is the best attribute to continue with in next level?
                // use recursion of the the decision tree below current node;
                DecisionTree childDecisionTree =  id3(
                        relevantPoints,
                        attributesAL
                );

                // childNode must be a Node, so get the node using root ;; "CASTING"
                DecisionTreeNode childNode = childDecisionTree.root;

                // children is a map from features to child nodes
                // i.e. given a feature value for an attribute, go to that child node
                children.put(feature, childNode);

            }
        }
        // make the current attribute node, with the recursive children
        DecisionTreeNode currentAttrNode = new AttributeNode(bestAttribute, children);
        // return the decision tree underneath this node
        return new DecisionTree(currentAttrNode);

    }
}
