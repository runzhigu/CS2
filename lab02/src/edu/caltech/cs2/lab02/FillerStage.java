package edu.caltech.cs2.lab02;

import java.util.HashMap;
import java.util.Map;

public class FillerStage implements AdventureStage {

    private final String prompt;
    private final Map<String, AdventureStage> responses;

    /**
     * Constructor for filler stage at end of game (No responses).
     *
     * @param prompt Prompt for fillerStage.
     */
    public FillerStage(String prompt) {
        this.prompt = prompt;
        this.responses = new HashMap<>();
    }

    /**
     * constructor for filler stage in middle of game.
     *
     * @param prompt      prompt for fillerStage.
     * @param response    responses for fillerStage.
     * @param destination Destination after stage is executed.
     */
    public FillerStage(String prompt, String response, AdventureStage destination) {
        this.prompt = prompt;
        this.responses = Map.of(response, destination);
    }

    /**
     * returns prompt for next stage selection.
     *
     * @return prompt
     */
    @Override
    public String riddlePrompt() {
        return this.prompt;
    }

    /**
     * Plays stage.
     * Filler stages do nothing, but display their prompt, so this does nothing.
     */
    @Override
    public void playStage() {
        // Does nothing, contained in riddlePrompt
        return;
    }

    /**
     * gets the responses stored in responses.
     *
     * @return hashmap of responses made by constructor.
     */
    @Override
    public Map<String, AdventureStage> getResponses() {
        return this.responses;
    }
}
