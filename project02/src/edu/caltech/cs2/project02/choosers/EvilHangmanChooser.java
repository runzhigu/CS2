package edu.caltech.cs2.project02.choosers;

import edu.caltech.cs2.project02.ScrabbleWords;
import edu.caltech.cs2.project02.interfaces.IHangmanChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class EvilHangmanChooser implements IHangmanChooser {

  private String secretWord;
  private Set<String> wordsSet = new HashSet<>();
  private SortedSet<Character> prevGuesses = new TreeSet<>();
  private int remainingGuesses;


  public EvilHangmanChooser(int wordLength, int maxGuesses) throws FileNotFoundException {

    // word length must be positive
    if (wordLength < 1) {
      throw new IllegalArgumentException("word length " + wordLength + " is less than 1");
    }

    // maxGuesses must be positive
    if (maxGuesses < 1) {
      throw new IllegalArgumentException("max guesses " + maxGuesses + " is less than 1");
    }

    // dictionary of all words
    ScrabbleWords allWords = null;
    try {
      allWords = new ScrabbleWords("data/scrabble.txt");
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    // to be populated w/ words of length wordLength
    for (String word : allWords.scrabbleWords) {
      if (word.length() == wordLength) {
        wordsSet.add(word);
      }
    }

    // make sure there exist words of size wordLength
    if (wordsSet.size() == 0) {
      throw new IllegalStateException("no words of length " + wordLength);
    }

    //initialize everything
    this.secretWord = "-".repeat(wordLength)
            //initialize everything
    ;
//    this.wordsSet = wordsSet;
//    this.prevGuesses = prevGuesses;
    this.remainingGuesses = maxGuesses;

  }

  @Override
  public int makeGuess(char letter) {

    // make sure lowercase
    if (!Character.isLowerCase(letter)) {
      throw new IllegalArgumentException(letter + " isn't lowercase");
    }

    // make sure we still have guesses left
    if (this.remainingGuesses == 0) {
      throw new IllegalStateException("no more guesses");
    }

    // make sure not already guessed
    if (prevGuesses.contains(letter)) {
      throw new IllegalArgumentException("already guessed letter " + letter);
    }

    // if we passed all exceptions

    // declare new wordFamilies where each key is a different word family based on letter guess
    TreeMap<String, Set<String>> wordFamilies = new TreeMap<>();

    // iterate through all values in current wordsMap to make new families
    for (String prevWord : this.wordsSet) { // prevWord is a word in the current word set
      // StringBuilder to replace dashes with the guessed character, to find families
      StringBuilder keyPatternSB = new StringBuilder(this.secretWord);
      // iterate through every index
      for (int i = 0; i < this.secretWord.length(); i++) {
        if (prevWord.charAt(i) == letter) {
          keyPatternSB.setCharAt(i, letter);
        }
      }
      // SB to string
      String keyPattern = keyPatternSB.toString();

      // put into wordFamilies map; first test if the key is already present
      Set<String> currentKeysValues;
      if (wordFamilies.containsKey(keyPattern)) {
        currentKeysValues = wordFamilies.get(keyPattern);
      }
      else {
        currentKeysValues = new HashSet<>();
      }
      currentKeysValues.add(prevWord);
      wordFamilies.put(keyPattern, currentKeysValues);
    }

    // find the key with the greatest size of values; choose alphabetically earlier if tie
    // implement the alphabetical order by going thru the keys in reverse order
    int maxSize = 0;
    String bestPattern = null;
    for (String keyPattern : wordFamilies.descendingKeySet()) {
      if (wordFamilies.get(keyPattern).size() >= maxSize) {
        maxSize = wordFamilies.get(keyPattern).size();
        bestPattern = keyPattern;
      }
    }

    // update wordsSet with the values of bestPattern
    this.wordsSet = wordFamilies.get(bestPattern);
    // update secretWord with bestPattern
    this.secretWord = bestPattern;


    // see how many instances of letter guess are present in bestPattern / secretWord
    int instancesOfGuess = 0;
    for (int i = 0; i < this.secretWord.length(); i++) {
      if (this.secretWord.charAt(i) == letter) {
        instancesOfGuess += 1;
      }
    }


    // down one guess if our character wasn't present
    if (instancesOfGuess == 0) {
      this.remainingGuesses--;
    }

    // add guess to prevGuesses
    this.prevGuesses.add(letter);


    return instancesOfGuess;
  }

  @Override
  public boolean isGameOver() {

    // iterate over string to see if each letter is contained in prevGuesses
    int correctPositions = 0;
    for (int i = 0; i < this.secretWord.length(); i++) {
      Character secretLetter = (Character) this.secretWord.charAt(i);
      if (this.prevGuesses.contains(secretLetter)) {
        correctPositions += 1;
      }
    }

    // if all letters have been guessed or run out of guesses, return true
    return correctPositions == this.secretWord.length() || this.remainingGuesses == 0;
  }

  @Override
  public String getPattern() {
    // initialize stringbuilder
    StringBuilder patternSB = new StringBuilder();

    // if already guessed, display letter; otherwise, display dash
    for (int i = 0; i < this.secretWord.length(); i++) {
      if (this.prevGuesses.contains(this.secretWord.charAt(i))) {
        patternSB.append(this.secretWord.charAt(i));
      }
      else {
        patternSB.append('-');
      }
    }

    String patternString = patternSB.toString();

    return patternString;
  }

  @Override
  public SortedSet<Character> getGuesses() {
    return this.prevGuesses;
  }

  @Override
  public int getGuessesRemaining() {
    return this.remainingGuesses;
  }

  @Override
  public String getWord() {
    this.remainingGuesses = 0;
    return this.secretWord;
  }





}