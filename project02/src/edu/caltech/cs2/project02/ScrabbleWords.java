package edu.caltech.cs2.project02;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;

public class ScrabbleWords {
    public Set<String> scrabbleWords = new HashSet<>();
    //public static String fileName = "/Users/victorialiu/IdeaProjects/project02-vliu/data/scrabble.txt";
    public ScrabbleWords(String fileName) throws IOException {

        Path path = Paths.get(fileName);
        Scanner scanner = new Scanner(path);

        //read file line by line
        scanner.useDelimiter(System.getProperty("line.separator"));
        while (scanner.hasNext()) {
            scrabbleWords.add(scanner.next());
        }
        scanner.close();

//        this.scrabbleWords = scrabbleWords;
    }

    public static void main(String[] args) {
        try {
            ScrabbleWords testDict = new ScrabbleWords("data/scrabble.txt");
            for (String word : testDict.scrabbleWords){
                System.out.println(word);
            }
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
