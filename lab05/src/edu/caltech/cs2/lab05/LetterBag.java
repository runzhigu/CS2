package edu.caltech.cs2.lab05;

import java.util.HashMap;
import java.util.Map;

public class LetterBag {
    private Map<Character, Integer> bag;

    public LetterBag(String s) {
        this.bag = new HashMap<>();
        for (Character c : s.toCharArray()) {
            if (c < 'a' || c > 'z') {
                continue;
            }

            if (!this.bag.containsKey(c)) {
                this.bag.put(c, 0);
            }
            this.bag.put(c, this.bag.get(c) + 1);
        }
    }

    public boolean isEmpty() {
        return this.bag.isEmpty();
    }

    public LetterBag subtract(LetterBag other) {
        LetterBag result = new LetterBag("");

        if (!this.bag.keySet().containsAll(other.bag.keySet())) {
            return null;
        }

        for (Character c : this.bag.keySet()) {
            int remaining = this.bag.get(c) - other.bag.getOrDefault(c, 0);
            if (remaining < 0) {
                return null;
            }
            if (remaining > 0) {
                result.bag.put(c, remaining);
            }
        }

        return result;
    }

    @Override
    public int hashCode() {
        return this.bag.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LetterBag)) {
            return false;
        }
        return this.bag.equals(((LetterBag)o).bag);
    }
}