package edu.caltech.cs2.lab05;

import java.util.List;
import java.util.ArrayList;

public class AnagramGenerator {
    public static void printPhrases(String phrase, List<String> dictionary) {
        // convert phrase into phrase LetterBag object
        LetterBag phraseLB = new LetterBag(phrase);

        // iterate thru each word in dict to shear off useless words
//        dictionary.removeIf(
//                possible_word -> phraseLB.subtract(new LetterBag(possible_word)) == null
//        );

        // iterate thru each word in dict to shear off useless words
        // add the good words to a newDictionary
        List<String> newDictionary = new ArrayList<>();

        for (String possible_word : dictionary) {
            if (phraseLB.subtract(new LetterBag(possible_word)) != null) {
                newDictionary.add(possible_word);
            }
        }


        // accumulator
        List<String> anagramWords = new ArrayList<>();

        // call private function
        printPhrases(phraseLB, anagramWords, newDictionary);

    }

    private static void printPhrases(
            LetterBag phraseLB,
            List<String> anagramWords,
            List<String> dictionary
    ) {

        // base case if the phrase is empty now
        if (phraseLB.isEmpty()) {
            System.out.println(anagramWords);
        }

        // recursive case
        else {
            // at each level, must go thru entire dictionary (esp for repeat words)
            for (String possible_word : dictionary) {
                // convert each possible word into a letter bag
                LetterBag possible_wordLB = new LetterBag(possible_word);

                // see what would remain of our phrase after we subtrac this dict word
                LetterBag remainder_phrase = phraseLB.subtract(possible_wordLB);

                // if the current (i.e. remaining) phrase LB contains the letters of possible_wordLB
                if (remainder_phrase != null) {

                    // we add this possible_word
                    anagramWords.add(possible_word);
                    // we recurse to see if this branch is viable
                    printPhrases(remainder_phrase, anagramWords, dictionary);
                    // recursively backtrack (doesn't mean that the word didn't matter
                    // if it mattered, then we will have printed it
                    // but now we are looking down other branches, so we delete it
                    anagramWords.remove(possible_word);
                }
            }
        }
    }

    public static void printWords(String word, List<String> dictionary) {
        // make letterbag version of word
        LetterBag wordLB = new LetterBag(word);

        // iterate thru each word of dictionary
        for (String possible_word : dictionary) {
            // get the remainder, since we're only concerned with one word anagrams
            LetterBag remainder_phrase = wordLB.subtract(new LetterBag(possible_word));
            // if remainder phrase is not null (null means that possible_word > word)
            // and is empty (empty means word and possible_word have the same letters)
            // print out the word
            if (remainder_phrase != null && remainder_phrase.isEmpty()) {
                System.out.println(possible_word);
            }

        }
    }
}
