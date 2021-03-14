package edu.caltech.cs2.project02.guessers;

import edu.caltech.cs2.project02.ScrabbleWords;
import edu.caltech.cs2.project02.interfaces.IHangmanGuesser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class AIHangmanGuesser implements IHangmanGuesser {

  private static String fileName = "data/scrabble.txt";
  private static ScrabbleWords allWords;


  public AIHangmanGuesser() throws FileNotFoundException {
    // dictionary of all words

    try {
      allWords = new ScrabbleWords(fileName);
    }
    catch (IOException e) {
      e.printStackTrace();
    }

//    this.allWords = allWords;

  }

  @Override
  public char getGuess(String pattern, Set<Character> guesses) throws FileNotFoundException {

    // get set of wrong guesses
    Set<Character> wrongGuesses = new HashSet<>();
    for (Character guess : guesses) {
      boolean add = true;
      for (int i = 0; i < pattern.length(); i++) {
        if (pattern.charAt(i) == guess) {
          add = false;
        }
      }
      if (add) {
        wrongGuesses.add(guess);
      }
    }

    // initialize arraylist of words matching the pattern
    List<String> matchingWords = new ArrayList<>();

    // loop through every word in the dict to see if it matches pattern
    for (String word : this.allWords.scrabbleWords) {
      // start by assuming that the word matches pattern
      boolean matches = true;

      // if word is not of pattern.length(), matches = false
      if (word.length() != pattern.length()) {
        matches = false;
      }
      // if word and pattern are the same size, make sure the patterns match / no wrong guesses
      else {
        // loop through every letter of the pattern
        for (int i = 0; i < pattern.length(); i++) {
          // if pattern doesn't match
          if (pattern.charAt(i) != '-' && pattern.charAt(i) != word.charAt(i)) {
            matches = false;
            break;
          }
          // make sure word doesn't contain any known wrong letters
          else if (wrongGuesses.contains(word.charAt(i))) {
            matches = false;
            break;
          }
        }
      }

      if (matches) {
        matchingWords.add(word);
      }
    }

    // find remaining letters and add them to map, with initial occurrence 0
    TreeMap<Character, Integer> letterFreq = new TreeMap<>();
    for (char letter = 'a'; letter <= 'z'; letter++) {
      if (!guesses.contains(letter)) {
        letterFreq.put(letter, 0);
      }
    }

    // find the frequency of each remaining letter in all the matching words
    // iterate thru all matching words
    for (String matchingWord : matchingWords) {
      for (int i = 0; i < matchingWord.length(); i++) {
        // only add frequency if the letter at this position hasn't been guessed yet
        if (letterFreq.containsKey(matchingWord.charAt(i))) {
          letterFreq.put(
                  matchingWord.charAt(i),
                  letterFreq.get(matchingWord.charAt(i)) + 1
          );
        }
      }
    }

    // get best letter based on the character with the highest frequency
    // if a tie, get what is alphabetically earlier
    // implement the alphabetical order with descendingKeySet
    char bestLetter = 'z';
    int highestFreq = 0;
    for (Character letter : letterFreq.descendingKeySet()) {
      if (letterFreq.get(letter) >= highestFreq) {
        bestLetter = letter;
        highestFreq = letterFreq.get(letter);
      }
    }

    return bestLetter;
  }
}
