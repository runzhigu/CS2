package edu.caltech.cs2.textgenerator;

import edu.caltech.cs2.datastructures.LinkedDeque;
import edu.caltech.cs2.interfaces.IDeque;

import java.util.Iterator;

public class NGram implements Iterable<String>, Comparable<NGram> {
    public static final String NO_SPACE_BEFORE = ",?!.-,:'";
    public static final String NO_SPACE_AFTER = "-'><=";
    public static final String REGEX_TO_FILTER = "”|\"|“|\\(|\\)|\\*";
    public static final String DELIMITER = "\\s+|\\s*\\b\\s*";
    private IDeque<String> data;

    public static String normalize(String s) {
        return s.replaceAll(REGEX_TO_FILTER, "").strip();
    }

    public NGram(IDeque<String> x) {
        this.data = new LinkedDeque<>();
        for (String word : x) {
            this.data.add(word);
        }
    }

    public NGram(String data) {
        this(normalize(data).split(DELIMITER));
    }

    public NGram(String[] data) {
        this.data = new LinkedDeque<>();
        for (String s : data) {
            s = normalize(s);
            if (!s.isEmpty()) {
                this.data.addBack(s);
            }
        }
    }

    public NGram next(String word) {
        String[] data = new String[this.data.size()];
        Iterator<String> dataIterator = this.data.iterator();
        dataIterator.next();
        for (int i = 0; i < data.length - 1; i++) {
            data[i] = dataIterator.next();
        }
        data[data.length - 1] = word;
        return new NGram(data);
    }

    public String toString() {
        String result = "";
        String prev = "";
        for (String s : this.data) {
            result += ((NO_SPACE_AFTER.contains(prev) || NO_SPACE_BEFORE.contains(s) || result.isEmpty()) ? "" : " ") + s;
            prev = s;
        }
        return result.strip();
    }

    @Override
    public Iterator<String> iterator() {
        return this.data.iterator();
    }

    @Override
    public int compareTo(NGram other) {
        // iterators
        Iterator<String> thisIterator = this.data.iterator();
        Iterator<String> otherIterator = other.data.iterator();

        // first assume one is not the substring of the other
        while (thisIterator.hasNext() && otherIterator.hasNext()) {
            int comparison = thisIterator.next().compareTo(otherIterator.next());
            if (comparison != 0) {
                return comparison;
            }
        }

        // they are either the same IDeque or have the same beginning

        // if 0, then they are the same IDeque
        // positive number means this.data is greater
        // negative number means this.data is smaller

        return this.data.size() - other.data.size();
    }

    @Override
    public boolean equals(Object o) {
        // object must be of NGram instance, or else methods won't work
        if (!(o instanceof NGram)) {
            return false;
        }

        // cast to NGram
        NGram comparedObj = (NGram) o;

        // objects should be the same size
        if (this.data.size() != comparedObj.data.size()) {
            return false;
        }

        // iterators
        Iterator<String> thisIterator = this.data.iterator();
        Iterator<String> comparedObjIterator = comparedObj.data.iterator();

        // compare each string of the IDeque
        while (thisIterator.hasNext()) {
            if (!thisIterator.next().equals(comparedObjIterator.next())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        // iterator
        Iterator<String> thisIterator = this.data.iterator();
        // hash starts at 0
        int hash = 0;

        // hashcode calculation
        while (thisIterator.hasNext()) {
            // use horner's rule
            hash = (31 * hash) + thisIterator.next().hashCode();
        }

        return hash;
    }
}