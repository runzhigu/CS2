package edu.caltech.cs2.project04;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public abstract class AbstractMovieAutoCompleter {
    protected static final Map<String, String> TITLE_MAP = new HashMap<>();
    protected static final Map<String, String> ID_MAP = new HashMap<>();

    static {
        try (Scanner titleData = new Scanner(new File("data/movies.basics.tsv"))) {
            while (titleData.hasNextLine()) {
                String[] line = titleData.nextLine().split("\t");
                String id = line[0];
                String title = line[2];
                TITLE_MAP.put(id, title);
                ID_MAP.put(title, id);
            }
        } catch (FileNotFoundException e) {
        }
    }

    public static Map<String, String> getIDMap() {
        return ID_MAP;
    }
}