package edu.caltech.cs2.project03;

import edu.caltech.cs2.datastructures.CircularArrayFixedSizeQueue;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IFixedSizeQueue;
import java.lang.Math;

import java.util.Random;


public class CircularArrayFixedSizeQueueGuitarString {
    private static final double SAMPLING_RATE = 44100;
    private static final double DECAY_FACTOR = 0.996;
    private static final Random RAND = new Random();
    private IFixedSizeQueue<Double> guitarString;

    public CircularArrayFixedSizeQueueGuitarString(double frequency) {
        // number of samples in a given sampling period
        int n = (int) Math.ceil(SAMPLING_RATE / frequency);

        // declare and initialize
        guitarString = new CircularArrayFixedSizeQueue<>(n);

        for (int i = 0; i < n; i++) {
            guitarString.enqueue((double) 0);
        }

    }

    public int length() {
        return this.guitarString.size();
    }

    public void pluck() {

        // enqueue for each sample of the guitar string
        for (int i = 0; i < this.length(); i++) {
            this.guitarString.dequeue();
            // dequeue the zero or whatever was on there previously
            this.guitarString.enqueue(RAND.nextDouble() - 0.5);
        }
    }

    public void tic() {
        // dequeue front member
        Double dequeued = this.guitarString.dequeue();

        // enqueue based on Karplus-Strong algorithm
        this.guitarString.enqueue(
                DECAY_FACTOR * average(dequeued, this.guitarString.peek())
        );
    }

    public double sample() {
        return this.guitarString.peek();
    }

    private double average(double a, double b) {
        return 0.5 * (a + b);
    }
}
