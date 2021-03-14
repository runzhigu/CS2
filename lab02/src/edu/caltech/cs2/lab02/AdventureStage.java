package edu.caltech.cs2.lab02;

import java.util.Map;

public interface AdventureStage {

    String riddlePrompt();

    void playStage();

    Map<String, AdventureStage> getResponses();

}
