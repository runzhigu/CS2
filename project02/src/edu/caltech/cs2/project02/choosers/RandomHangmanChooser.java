package edu.caltech.cs2.project02.choosers;

import edu.caltech.cs2.project02.ScrabbleWords;
import edu.caltech.cs2.project02.interfaces.IHangmanChooser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class RandomHangmanChooser implements IHangmanChooser {
  private static final Random RAND = new Random();
  private final String secretWord;
  private SortedSet<Character> prevGuesses = new TreeSet<>();
  private int remainingGuesses;



  public RandomHangmanChooser(int wordLength, int maxGuesses) throws FileNotFoundException {

    // word length must be positive
    if (wordLength < 1) {
      throw new IllegalArgumentException("word length" + wordLength + "is less than 1");
    }

    // maxGuesses must be positive
    if (maxGuesses < 1) {
      throw new IllegalArgumentException("max guesses" + maxGuesses + "is less than 1");
    }

    // dictionary of all words
    ScrabbleWords allWords = null;
    try {
      allWords = new ScrabbleWords("data/scrabble.txt");
//      allWords = new ScrabbleWords("/Users/victorialiu/IdeaProjects/project02-vliu/data/scrabble.txt");
      }
    catch (IOException e) {
      e.printStackTrace();
    }


    // to be populated w/ words of length wordLength
    SortedSet<String> sortedWords = new TreeSet<>();
    for (String word : allWords.scrabbleWords) {
      if (word.length() == wordLength) {
        sortedWords.add(word);
      }
    }

    if (sortedWords.size() == 0) {
      throw new IllegalStateException("no words of length" + wordLength);
    }

    // select random index within size of sortedWords
    int randInt = RAND.nextInt(sortedWords.size());

    // get word at randInt from sortedWords
    int i = 0;
    String chosenWord = null;
    for (String word : sortedWords) {
      if (i == randInt) {
        chosenWord = word;
        break;
      }
      i++;
    }


    //initialize everything
    this.secretWord = chosenWord;
//    this.prevGuesses = prevGuesses;
    this.remainingGuesses = maxGuesses;
  }    

  @Override
  public int makeGuess(char letter) {

    // make sure lowercase
    if (!Character.isLowerCase(letter)) {
      throw new IllegalArgumentException(letter + "isn't lowercase");
    }

    // make sure still have guesses
    if (this.remainingGuesses == 0) {
      throw new IllegalStateException("no more guesses");
    }

    // make sure not already guessed
    if (prevGuesses.contains(letter)) {
      throw new IllegalArgumentException("already guessed letter" + letter);
    }

    // if we passed all exceptions
    // count number of times this letter occurs in our word
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
      Character secretLetter = this.secretWord.charAt(i);
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


    return patternSB.toString();
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