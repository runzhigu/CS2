package edu.caltech.cs2.project01;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;


/**
 * reads text file in the specified directory and returns string
 *
 */
public class CryptogramFormatter {
    public static void main(String[] args) throws IOException {
        StringBuilder textBuilder = new StringBuilder();
        String fileName = "/Users/victorialiu/IdeaProjects/project01-vliu/cryptogram.txt";
        Path path = Paths.get(fileName);
        Scanner scanner = new Scanner(path);

        //read file line by line
        scanner.useDelimiter(System.getProperty("line.separator"));
        while (scanner.hasNext()) {
            textBuilder.append(scanner.next());
        }
        scanner.close();

        // convert textBuilder to String
        String text = textBuilder.toString();

        // make uppercase
        text = text.toUpperCase();

        // delete whitespace
        text = text.replaceAll("[^A-Z]", "");

        System.out.println(text);
    }
    }

