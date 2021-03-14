package edu.caltech.cs2.lab02;

import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class AdventureGame {

    public static final Random RAND = new Random(1337);

    private final Scanner sc;
    private AdventureStage currentStage;

    /**
     * The Adventure game!
     *
     * @param sc Scanner to be used for whole game.
     */
    public AdventureGame(Scanner sc) {
        this.sc = sc;
        this.currentStage = new FillerStage(
            "Welcome to our neat nature walk, a neature walk if you will. We are going to see so many cool things on our walk! Lets go! (To answer the prompts, type the possibilities in the brackets to go to that choice)",
            "go",
            new BirdCountingStage(sc)
        );
    }

    /**
     * driving function of game.
     * Plays until the current stage has no more responses left, then ends.
     */
    public void play() {
        while (true) {
            handleStage();
            if (this.currentStage.getResponses().isEmpty()) {
                System.out.println("Go to bed, frosh!!! Ditch Day is Tomorrow!!!!!!!!");
                break;
            }
        }
    }

    /**
     * runs the stage and its puzzles.
     */
    private void handleStage() {
        this.currentStage.playStage();
        System.out.println(this);
        AdventureStage poss;
        while (true) {
            poss = this.parseResponse(this.sc.nextLine());
            if (poss != null) {
                break;
            }

            System.out.println("Try again! Please type the word in the brackets!");
        }
        this.currentStage = poss;
    }

    /**
     * parse user response
     *
     * @param response user input
     * @return null
     */
    private AdventureStage parseResponse(String response) {
        Map<String, AdventureStage> responses = this.currentStage.getResponses();
        for (Map.Entry<String, AdventureStage> other : responses.entrySet()) {
            if (other.getKey().toLowerCase().contains(response.toLowerCase())) {
                return other.getValue();
            }
        }
        return null;
    }

    /**
     * converts prompts/responses to a String.
     *
     * @return String version of prompts/responses.
     */
    @Override
    public String toString() {
        // Example toString output:
        // Aaah, a bear attacks! What do you do?
        // >> [Do nothing] [Lay down] [Fight]
        String result = this.currentStage.riddlePrompt() + "\n";

        Map<String, AdventureStage> responses = this.currentStage.getResponses();
        if (!responses.isEmpty()) {
            result += ">>";
            for (String response : responses.keySet()) {
                result += " [" + response + "]";
            }
        }
        return result;
    }

    public static void main(String[] args) {
        AdventureGame adventure = new AdventureGame(new Scanner(System.in));
        adventure.play();
    }

}
