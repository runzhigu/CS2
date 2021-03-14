package edu.caltech.cs2.project04;

import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.datastructures.LinkedDeque;
import edu.caltech.cs2.datastructures.ArrayDeque;

import java.util.HashMap;
import java.util.Map;

public class HashMovieAutoCompleter extends AbstractMovieAutoCompleter {
    // map of TITLE -> Deque of SUFFIXES
    private static Map<String, IDeque<String>> titles = new HashMap<>();

//    // map of TITLE -> MOVIE ID
//    private static Map<String, String> ID_MAP = AbstractMovieAutoCompleter.getIDMap();



    public static void populateTitles() {
        titles.clear();
        for (Map.Entry<String, String> movieEntry: ID_MAP.entrySet()) {
            // get movie key name from entry
            String movieTitle = movieEntry.getKey();

            // normalize the title
//            movieTitle = movieTitle.replaceAll("[^a-zA-Z ]", "");
            movieTitle = movieTitle.toLowerCase();

            // make sure movie title is not empty
            if (movieTitle.length() != 0) {
                // declare deque of strings to add movie suffixes to (aka the value of the hashmap)
                IDeque<String> suffixes = new LinkedDeque<>();

                // split string by spaces into suffixes, iterating backward
                for (int i = movieTitle.length() - 1; i >= 0; i--) {
                    // if there is a space (iterating backward)
                    if (movieTitle.charAt(i) == ' ') {
                        // add from the next index after the space to the end, making the suffix
                        suffixes.add(movieTitle.substring(i + 1));
                    }
                }
                // add the suffix where it's the whole title
                suffixes.add(movieTitle);

                // after getting all the suffixes, add the movie title key and deque to titles
                titles.put(movieEntry.getKey(), suffixes);
            }

        }
    }

    public static IDeque<String> complete(String term) {
        // make case insensitive
        term = term.toLowerCase();

        // collect all the complete titles
        IDeque<String> completeTitles = new LinkedDeque<>();

        // iterate thru the dictionary values
        for (Map.Entry<String, IDeque<String>> entry : titles.entrySet()) {
            // for each suffix
            for (String suffix : entry.getValue()) {

                // if the terms are the same length
                if (term.length() == suffix.length()) {
                    // if the strings are equal, add to the movies deque
                    if (term.equals(suffix)) {
                        completeTitles.add(entry.getKey());
                        // use break statement to avoid duplicate values; we've already added this movie to the deque
                        break;
                    }
                }

                // if the term is less than the suffix
                else if (term.length() < suffix.length()) {
                    // make sure the beginning substring is the same as term, and that the next char is a space
                    if (term.equals(suffix.substring(0, term.length())) && suffix.charAt(term.length()) == ' ') {
                        completeTitles.add(entry.getKey());
                        // use break statement to avoid duplicate values; we've already added this movie to the deque
                        break;
                    }
                }
                }
            }

        return completeTitles;
        }

}
