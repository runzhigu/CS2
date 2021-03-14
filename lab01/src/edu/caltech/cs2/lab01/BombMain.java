package edu.caltech.cs2.lab01;

public class BombMain {
    public static void main(String[] args) {
        Bomb b = new Bomb();
        b.phase0("22961293");
        b.phase1("hdc");
        String password3 = "1374866960 ".repeat(5001);
        b.phase2(password3);
    }
}