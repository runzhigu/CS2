package edu.caltech.cs2.lab04;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TitanicDatasetLoader {
    public static final String TITANIC_TRAINING_DATA_X = "data/X_train.csv";
    public static final String TITANIC_TRAINING_DATA_Y = "data/y_train.csv";
    public static final String TITANIC_VAL_DATA_X = "data/X_val.csv";
    public static final String TITANIC_VAL_DATA_Y = "data/y_val.csv";

    /**
     * Reads a comma-separated data file
     *
     * @param name file name
     * @return a map representation of datapoints
     */
    public static SortedMap<String, List<String>> read_csv(String name) {
        // Maps attributes to all values the datapoints have w.r.t. to that attribute
        SortedMap<String, List<String>> data = new TreeMap<>();
        String line;
        List<String> categories = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(name));
            boolean first = true;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                // Fills attribute headers if first
                if (first) {
                    categories.addAll(Arrays.asList(values));
                    for (String key : categories) {
                        data.put(key, new ArrayList<>());
                    }
                    first = false;
                } else {
                    for (int i = 0; i < categories.size(); i++) {
                        data.get(categories.get(i)).add(values[i]);
                    }
                }
            }
            br.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return data;
    }

    /**
     * Processes a target data file
     *
     * @param data result of reading a csv
     * @return a list of attributes representing targets
     */
    public static List<String> process_y(SortedMap<String, List<String>> data) {
        List<String> survived = new ArrayList<>();
        List<String> values = data.get("Survived");
        for (String v : values) {
            if (v.equals("1")) {
                survived.add("Yes");
            } else {
                survived.add("No");
            }
        }
        return survived;
    }

    /**
     * Processes a datapoints data file
     *
     * @param data result of reading a csv
     * @return a list of maps representing datapoints
     */
    public static List<Map<String, String>> process_x(SortedMap<String, List<String>> data) {
        // Each map represents one datapoint and maps an attribute to specific features
        List<Map<String, String>> datapoints = new ArrayList<>();
        for (int i = 0; i < data.get("Age").size(); i++) {
            datapoints.add(new HashMap<>());
        }
        for (Map.Entry<String, List<String>> entry : data.entrySet()) {
            String a = entry.getKey();
            String f = "";
            List<String> values = entry.getValue();
            // Loops over all values relating to an attribute and updates list
            switch (a) {
                case "Age":
                    for (int i = 0; i < values.size(); i++) {
                        switch (values.get(i)) {
                            case "child":
                                f = "Age_child";
                                break;
                            case "young":
                                f = "Age_young";
                                break;
                            case "middle":
                                f = "Age_middle";
                                break;
                            case "old":
                                f = "Age_old";
                                break;
                        }
                        datapoints.get(i).put(a, f);
                    }
                    break;
                case "Embarked":
                    for (int i = 0; i < values.size(); i++) {
                        switch (values.get(i)) {
                            case "Q":
                                f = "Embarked_Q";
                                break;
                            case "C":
                                f = "Embarked_C";
                                break;
                            case "S":
                                f = "Embarked_S";
                                break;
                        }
                        datapoints.get(i).put(a, f);
                    }
                    break;
                case "Fare":
                    for (int i = 0; i < values.size(); i++) {
                        switch (values.get(i)) {
                            case "low":
                                f = "Fare_low";
                                break;
                            case "average":
                                f = "Fare_average";
                                break;
                            case "high":
                                f = "Fare_high";
                                break;
                            case "rich":
                                f = "Fare_rich";
                                break;
                        }
                        datapoints.get(i).put(a, f);
                    }
                    break;
                case "Parch":
                    for (int i = 0; i < values.size(); i++) {
                        switch (values.get(i)) {
                            case "none":
                                f = "Parch_none";
                                break;
                            case "one":
                                f = "Parch_one";
                                break;
                            case "many":
                                f = "Parch_many";
                                break;
                        }
                        datapoints.get(i).put(a, f);
                    }
                    break;
                case "Pclass":
                    for (int i = 0; i < values.size(); i++) {
                        switch (values.get(i)) {
                            case "poor":
                                f = "Pclass_poor";
                                break;
                            case "middle":
                                f = "Pclass_middle";
                                break;
                            case "rich":
                                f = "Pclass_rich";
                                break;
                        }
                        datapoints.get(i).put(a, f);
                    }
                    break;
                case "Sex":
                    for (int i = 0; i < values.size(); i++) {
                        switch (values.get(i)) {
                            case "male":
                                f = "Sex_male";
                                break;
                            case "female":
                                f = "Sex_female";
                                break;
                        }
                        datapoints.get(i).put(a, f);
                    }
                    break;
                case "SibSp":
                    for (int i = 0; i < values.size(); i++) {
                        switch (values.get(i)) {
                            case "none":
                                f = "SibSp_none";
                                break;
                            case "few":
                                f = "SibSp_few";
                                break;
                            case "many":
                                f = "SibSp_many";
                                break;
                        }
                        datapoints.get(i).put(a, f);
                    }
                    break;
            }
        }
        return datapoints;
    }

    private static Dataset loadTitanicDataset(String xFile, String yFile) {
        List<Map<String, String>> points = process_x(read_csv(xFile));
        List<String> targets = process_y(read_csv(yFile));

        // Attributes to split dataset on
        Map<String, List<String>> attributesFeatures = Map.of(
                "Age", List.of("Age_child", "Age_young", "Age_middle", "Age_old"),
                "Embarked", List.of("Embarked_Q", "Embarked_C", "Embarked_S"),
                "Fare", List.of("Fare_low", "Fare_average", "Fare_high", "Fare_rich"),
                "Parch", List.of("Parch_none", "Parch_one", "Parch_many"),
                "Pclass", List.of("Pclass_poor", "Pclass_middle", "Pclass_rich"),
                "Sex", List.of("Sex_male", "Sex_female"),
                "SibSp", List.of("SibSp_none", "SibSp_few", "SibSp_many")
        );

        List<Dataset.Datapoint> datapoints = new ArrayList<>(points.size());
        for (int i = 0; i < points.size(); i++) {
            Dataset.Datapoint p = new Dataset.Datapoint(points.get(i), targets.get(i));
            datapoints.add(p);
        }
        return new Dataset(datapoints, attributesFeatures);
    }

    public static Dataset loadTrainingTitanicDataset() {
        return loadTitanicDataset(TITANIC_TRAINING_DATA_X, TITANIC_TRAINING_DATA_Y);
    }

    public static Dataset loadValidationTitanicDataset() {
        return loadTitanicDataset(TITANIC_VAL_DATA_X, TITANIC_VAL_DATA_Y);
    }
}
