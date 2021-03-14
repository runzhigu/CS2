package edu.caltech.cs2.lab05;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class ConsoleAnagramPrinter {
    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Welcome to the CS 2 Anagram Printer!");

        Scanner console = new Scanner(System.in);

        // open the dictionary file
        Scanner input = new Scanner(new File("dictionaries/dictionary.txt"));

        // Read dictionary into a List
        List<String> dictionary = new ArrayList<String>();
        while (input.hasNextLine()) {
            dictionary.add(input.nextLine());
        }

        // Generate word and phrase anagrams
        dictionary = Collections.unmodifiableList(dictionary);
        while (true) {
            System.out.println();
            System.out.print("What word or phrase should I find anagrams for (return to quit)? ");
            String phrase = console.nextLine();
            if (phrase.isEmpty()) {
                break;
            }

            System.out.println("The matching phrases are...");
            AnagramGenerator.printPhrases(phrase, dictionary);

            System.out.println();
            System.out.println("The matching words are...");
            AnagramGenerator.printWords(phrase, dictionary);
        }
    }
}
