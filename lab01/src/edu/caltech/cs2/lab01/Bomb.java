package edu.caltech.cs2.lab01;

import java.util.*;

public class Bomb {
    public String shufflePassword(String s) {
        String code = "" + s.hashCode();
        List<String> l = new ArrayList<>();
        for (char c : code.toCharArray()) {
            l.add("" + c);
        }
        Random r = new Random(1337);
        Collections.shuffle(l, r);

        return String.join("", l);
    }

    public void phase0(String password) {
        String correctPassword = shufflePassword("hello");
        if (!password.equals(correctPassword)) {
            System.err.println("BOOM!");
            System.exit(1);
        }
        System.err.println("You passed phase 0 with the password \"" + password + "\"");
    }

    public void phase1(String password) {
        Set<String> animals = new HashSet<>(2, 1.0f);
        animals.add("cow");
        animals.add("horse");
        animals.add("dog");
        int i = 0;
        for (String animal : animals) {
            if (password.charAt(i) != animal.charAt(0)) {
                System.err.println("BOOM!");
                System.exit(2);
            }
            i++;
        }
        System.err.println("You passed phase 1 with the password \"" + password + "\"");
    }

    public void phase2(String password) {
        String[] passwordPieces = password.split(" ");

        Random r = new Random(1337);
        Set<Integer> numbers = new HashSet<>();
        while (numbers.size() < 10000) {
            numbers.add(r.nextInt());
        }
        boolean correct = false;
        int i = 0;
        for (int number : numbers) {
            if (i == 5000 && Integer.parseInt(passwordPieces[i]) == number) {
                correct = true;
            }
            i++;
        }
        if (!correct) {
            System.err.println("BOOM!");
            System.exit(3);
        }
        System.err.println("You passed phase 2 with the password \"" + password + "\"");
    }
}
