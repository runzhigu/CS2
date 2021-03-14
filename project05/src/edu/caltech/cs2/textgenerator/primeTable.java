package edu.caltech.cs2.textgenerator;

import edu.caltech.cs2.datastructures.ArrayDeque;
import edu.caltech.cs2.interfaces.IDeque;

import java.util.Iterator;

public class primeTable {
    private int[] tableSizes;
    private int initCapacity;
    private int upperBound;

    public primeTable(int initCapacity, int upperBound) {
        this.tableSizes = primesArray(initCapacity, upperBound);
    }

    public boolean[] primesBitSet(int upperBound) {
        // return bitset of primes

        boolean[] prime = new boolean[upperBound + 1];

        // first set every number to true (aka prime)
        for (int i = 0; i <= upperBound; i++)
            prime[i] = true;

        // every marked number is false
        for (int p = 2; p * p <= upperBound; p++) {
            // If prime[p] is not changed, then it is a
            // prime
            if (prime[p]) {
                // Update all multiples of p
                for (int i = p * p; i <= upperBound; i += p)
                    prime[i] = false;
            }
        }

        return prime;

    }

    public int[] primesArray(int initCapacity, int upperBound) {
        // @ return array of relevant primes

        // get bitset version
        boolean [] primeBitSet = primesBitSet(upperBound);

        // declare / initialize ideque for capturing >2n primes
        ArrayDeque<Integer> primesAD = new ArrayDeque<>(15);
        // add initCapacity first so peek will have something to look at
        primesAD.addBack(initCapacity);

        // iterate thru bitset and add relevant numbers to ideque; start at 2 * initCapacity + 1
        for (int i = initCapacity * 2 + 1; i <= upperBound; i++) {
            // check if i is prime and also greater than the previous prime on deque
            if (primeBitSet[i] && i > 2 * primesAD.peekBack()) {
                // add to the back
                primesAD.addBack(i);
                }
            }


        // now convert primesAD to primes[] array
        // declare / initialize primes[]
        int[] tableSizes = new int[primesAD.size()];

        // create iterator for deque
        Iterator<Integer> dequeIterator = primesAD.iterator();

        // convert deque to array tableSizes
        int i = 0;
        while (dequeIterator.hasNext()) {
            tableSizes[i] = dequeIterator.next();
            i++;
        }
        return tableSizes;
        }


    public int[] getTableSizes(){
        return this.tableSizes;
    }
    public static void main(String[] args) {
        primeTable myPrimes = new primeTable(7, 400000);
        for (int i = 0; i < myPrimes.tableSizes.length; i++)
            System.out.println(myPrimes.tableSizes[i]);

    }



}
