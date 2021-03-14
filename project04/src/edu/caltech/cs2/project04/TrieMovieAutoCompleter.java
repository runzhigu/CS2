package edu.caltech.cs2.project04;

import edu.caltech.cs2.datastructures.LinkedDeque;
import edu.caltech.cs2.datastructures.TrieMap;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.ITrieMap;

import java.util.Set;
import java.util.HashSet;



public class TrieMovieAutoCompleter extends AbstractMovieAutoCompleter {
    // map of Deque of SUFFIX -> Deque of TITLE
    private static ITrieMap<String, IDeque<String>, IDeque<String>> titles = new TrieMap<>((IDeque<String> s) -> s);


    public static void populateTitles() {
        titles.clear();

        for (String movieEntry : ID_MAP.keySet()) {
            // get movie key name from entry
            String movieTitle = movieEntry;

            // normalize the title
            movieTitle = movieTitle.toLowerCase();

            // split movieTitle by spaces into suffixes,
            String[] split = movieTitle.split(" ");

            // declare deque of strings to add movie suffix to (aka the key of the TrieMap)
            /*
            i.e.
            suffix -> title
            [ "of", "extinction"] -> ["transformers age of extinction", "edge of extinction"]
             */
            IDeque<String> suffix = new LinkedDeque<>();

            // iterate thru each word in split, progressively building smaller suffixes
            for (int i = split.length - 1; i >= 0; i--) {
                // add to titles TrieMap, addFront so it's the right order of suffix
                suffix.addFront(split[i]);

                // make empty value deque
                IDeque<String> value = new LinkedDeque<>();

                // add back to titles, but check first if it's already a key
                if (titles.containsKey(suffix)) {
                    value.addAll(titles.get(suffix));
                }
                // make sure to add capitalized version
                value.add(movieEntry);
                titles.put(suffix, value);

            }

            }

    }

    public static IDeque<String> complete(String term) {
        // initialize prefix, which will be the deque version of 'term'
        IDeque<String> prefix = new LinkedDeque<>();

        int start_index = 0;
        // split term and put into deque
        for (int i = 0; i < term.length(); i++) {
            // split by spaces and keep a running tally of start index
            if (term.charAt(i) == ' ') {
                prefix.addBack(term.substring(start_index, i));
                start_index = i + 1;
            }
        }
        // add last word of movie, assuming it doesn't end with a space
        prefix.addBack(term.substring(start_index));



        // prefix checking already happens in getCompletions
        // get all the relevant values
        IDeque< IDeque<String> > completeMoviesNested = titles.getCompletions(prefix);

        // create deque for un-nesting
        IDeque<String> completeMovies = new LinkedDeque<>();

        // create deque for un-nesting
        Set<String> completeMoviesSet = new HashSet<>();

        // un-nest
        for (IDeque<String> movieDeque : completeMoviesNested) {
            for (String movie : movieDeque) {
//                 prevent duplicates using hashset
                    completeMoviesSet.add(movie);
//                }
            }
        }

        for (String movie : completeMoviesSet) {
            completeMovies.add(movie);
        }


        return completeMovies;
    }
}
