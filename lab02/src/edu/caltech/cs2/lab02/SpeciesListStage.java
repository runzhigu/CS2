package edu.caltech.cs2.lab02;

import java.util.*;

public class SpeciesListStage implements AdventureStage {

    private static final List<String> REFERENCE_1 = List.of("cats", "dogs");
    private static final List<String> REFERENCE_2 = List.of("squirrels", "turtles");
    private static final List<String> REFERENCE_3 = new ArrayList<>();

    private final Scanner sc;
    private final Map<String, AdventureStage> responses;

    /**
     * Constructor for SpeciesListStage.
     * Sets responses and scanner.
     *
     * @param sc scanner used by whole game.
     */
    public SpeciesListStage(Scanner sc) {
        this.sc = sc;
        this.responses = new HashMap<>();
        this.responses.put("millikan", new FillerStage(
            "Oooh that water is very refreshingly neat! Woah! I think I see an animal up ahead!",
            "go",
            new PalindromeAnimalStage(sc)
        ));
        this.responses.put("chandler", new FillerStage(
            "Wow that food is really good! Might get boring after a while, though. Look up ahead! There's a neat animal! Let's go check it out!",
            "go",
            new PalindromeAnimalStage(sc)
        ));
    }

    /**
     * returns prompt
     *
     * @return prompt for next stage selection.
     */
    @Override
    public String riddlePrompt() {
        return "Wow! That was pretty neat! We got to see so many neat animals! Would you like to get a drink of water at Millikan? Or maybe some food at Chandler?";
    }

    /**
     * Plays stage.
     * 3 stages that each repeat until the correct input is given.
     */
    @Override
    public void playStage() {
        System.out.println("As part of your Caltech Neature Camp experience, you need to be able to identify all kinds of neat " +
                "animals that can be found around campus. Here are a few:");
        System.out.println("- These neat felines can often be found outside of Page House");
        System.out.println("- This neat animal can often be found inside Adam's office!");
        System.out.println("- Type their names into the terminal (separated by ',')");
        this.handleResponses(REFERENCE_1);

        System.out.println("Woah! There are even more neat animals here!");
        System.out.println("- These bushy-tailed friends are everywhere in and around the trees on campus");
        System.out.println("- These reptilians get their very own pond!");
        System.out.println("- Type their names into the terminal (separated by ',')");
        this.handleResponses(REFERENCE_2);

        System.out.println("Well, there's nothing left here! press enter to move.");
        this.handleResponses(REFERENCE_3);
    }

    /**
     * Handles user input and drives the arraySimilarity helper.
     *
     * @param reference a reference list of animals.
     */
    private void handleResponses(List<String> reference) {
        while (true) {
            String input = this.sc.nextLine();
            List<String> user;
            if (input.isEmpty()) {
                user = new ArrayList<>();
            }
            else {
                user = Arrays.asList(input.toLowerCase().split("[ ,]+"));
            }
            double similarity = arraySimilarity(reference, user);
            if (similarity != 1 && reference.size() != 0) {
                System.out.println("Try again! You got " + Math.round(similarity * reference.size()) + " animals correct!");
                continue;
            }
            break;
        }
    }

    /**
     * Gets hashmap of responses
     *
     * @return hashmap of responses generated by constructor.
     */
    @Override
    public Map<String, AdventureStage> getResponses() {
        return this.responses;
    }


    /**
     * Computes the proportion of objects in listOne that appear in listTwo. If an object appears multiple times
     * in listOne, it will be counted at most as many times as it appears in listTwo.
     *
     * @param listOne list of items to look for
     * @param listTwo list to check containment in
     * @return the proportion of objects in listOne that appear in listTwo as a double in [0, 1]
     */
    public static double arraySimilarity(List<String> listOne, List<String> listTwo) {
        if (listOne.size() == 0 || listTwo.size() == 0){
            return 0;
        }
        List<String> copy = new ArrayList<>();
        copy.addAll(listOne);
        double similarObjects = 0;
        for (String o : listTwo) {
            if (copy.contains(o)) {
                similarObjects += 1;
                copy.remove(o);
            }
        }
        return similarObjects / listOne.size();
    }

}
