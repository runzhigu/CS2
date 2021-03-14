package edu.caltech.cs2.lab04;

import java.util.*;

public class Dataset implements Iterable<Dataset.Datapoint> {


    public static class Datapoint {
        // Map from attribute names to feature that this node has for that attribute
        public Map<String, String> attributes;
        public String outcome;

        public Datapoint(Map<String, String> attributes, String outcome) {
            this.attributes = attributes;
            this.outcome = outcome;
        }
    }

    private final List<Datapoint> datapoints;
    private final Set<String> outcomes;
    private final Map<String, List<String>> attributeToFeatures;
    private final Map<String, String> featureToAttribute;

    public Dataset(List<Datapoint> datapoints, Map<String, List<String>> attributeToFeatures) {
        this.datapoints = datapoints;
        this.outcomes = new TreeSet<>();
        for (Datapoint p : datapoints) {
            outcomes.add(p.outcome);
        }
        this.attributeToFeatures = attributeToFeatures;
        this.featureToAttribute = new HashMap<>();
        for (String a : attributeToFeatures.keySet()) {
            for (String f : attributeToFeatures.get(a)) {
                this.featureToAttribute.put(f, a);
            }
        }
    }

    public List<String> getFeaturesForAttribute(String attribute) {
        return this.attributeToFeatures.get(attribute);
    }

    public int size() {
        return this.datapoints.size();
    }

    public boolean isEmpty() {
        return this.datapoints.isEmpty();
    }

    @Override
    public Iterator<Datapoint> iterator() {
        return datapoints.iterator();
    }

    /**
     * Construct a Dataset solely containing datapoints that possess the given feature.
     *
     * @param feature desired feature. Points with this feature will be in the returned dataset.
     * @return a pruned dataset
     */
    public Dataset getPointsWithFeature(String feature) {
        List<Datapoint> pointsWithFeature = new ArrayList<>();
        String attribute = this.featureToAttribute.get(feature);
        for (Datapoint p : this) {
            if (p.attributes.get(attribute).equals(feature)) {
                pointsWithFeature.add(p);
            }
        }
        return new Dataset(pointsWithFeature, this.attributeToFeatures);
    }

    /**
     * Gets the most common outcome from this Dataset
     *
     * @return the most common outcome across datapoints
     */
    public String getMostCommonOutcome() {
        SortedMap<String, Integer> outcomeFreqs = new TreeMap<>();
        for (String outcome : outcomes) {
            outcomeFreqs.put(outcome, 0);
        }
        for (Datapoint p : this.datapoints) {
            outcomeFreqs.put(p.outcome, 1 + outcomeFreqs.get(p.outcome));
        }
        String mostCommon = "";
        int freq = 0;
        for (String outcome : outcomeFreqs.keySet()) {
            if (outcomeFreqs.get(outcome) > freq) {
                mostCommon = outcome;
                freq = outcomeFreqs.get(outcome);
            }
        }
        return mostCommon;
    }

    /**
     * Check whether all points have the same outcome
     *
     * @return a nonempty string containing the outcome if all points have the same outcome.
     *      If not all points have the same outcome, return an empty string.
     */
    public String pointsHaveSameOutcome() {
        String knownOutcome = this.datapoints.get(0).outcome;
        for (Datapoint p : this) {
            if (!p.outcome.equals(knownOutcome)) {
                return "";
            }
        }
        return knownOutcome;
    }

    /**
     * Compute the probability that a randomly selected point has this feature in a Dataset
     *
     * @param feature feature to check
     * @return probability
     */
    public double getFeatureProbability(String feature) {
        return (double) this.getPointsWithFeature(feature).size() / this.size();
    }

    /**
     * Compute the entropy of a feature over all known outcomes
     *
     * @param feature feature to check
     * @return entropy
     */
    public double getFeatureEntropy(String feature) {
        Map<String, Integer> outcomeFreqs = new HashMap<>();
        for (String outcome : this.outcomes) {
            outcomeFreqs.put(outcome, 0);
        }
        Dataset pointsWithFeature = this.getPointsWithFeature(feature);
        for (Datapoint p : pointsWithFeature.datapoints) {
            outcomeFreqs.put(p.outcome, 1 + outcomeFreqs.get(p.outcome));
        }

        double entropy = 0;
        for (String outcome : this.outcomes) {
            // Returns a small, yet non-zero entropy for numerical stability if we have a singularity
            if (outcomeFreqs.get(outcome) == 0) {
                return 1e-9;
            }
            double prob = (double) outcomeFreqs.get(outcome) / pointsWithFeature.size();
            entropy += prob * Math.log(prob);
        }
        return -entropy;
    }

    /**
     * Compute the attribute that minimizes entropy from a list of attributes. An attribute's entropy is the sum
     * of feature entropies for each feature that corresponds to this attribute.
     *
     * @param attributes attributes to search
     * @return attribute that minimizes entropy
     */
    public String getAttributeWithMinEntropy(List<String> attributes) {
        String bestAttr = "";
        double minEntropy = Double.MAX_VALUE;
        for (String a : attributes) {
            double entropy = 0.0;
            for (String f : this.attributeToFeatures.get(a)) {
                entropy += this.getFeatureProbability(f) * this.getFeatureEntropy(f);
            }
            if (entropy <= minEntropy) {
                minEntropy = entropy;
                bestAttr = a;
            }
        }
        return bestAttr;
    }
}
