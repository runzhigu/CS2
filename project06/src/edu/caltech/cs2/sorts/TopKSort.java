package edu.caltech.cs2.sorts;

import edu.caltech.cs2.datastructures.MinFourHeap;
import edu.caltech.cs2.interfaces.IPriorityQueue;

public class TopKSort {
    /**
     * Sorts the largest K elements in the array in descending order. Modifies the array in place.
     * @param array - the array to be sorted; will be manipulated.
     * @param K - the number of values to sort
     * @param <E> - the type of values in the array
     * @throws IllegalArgumentException if K < 0
     */
    public static <E> void sort(IPriorityQueue.PQElement<E>[] array, int K) {
        if (K < 0) {
            throw new IllegalArgumentException("K cannot be negative!");
        }

        if (array.length != 0) {
            MinFourHeap<E> sortHeap = new MinFourHeap<E>();
        //paring down the array to only contain the k highest priority elements
            for (IPriorityQueue.PQElement<E> elem : array) {
                //when the heap is not full
                if (sortHeap.size() < K) {
                    sortHeap.enqueue(elem);
                }
                //when the heap is full, but there exist elements with higher priority values (lower priority)
                else if (sortHeap.peek() != null && elem.priority > sortHeap.peek().priority) {
                    sortHeap.dequeue();
                    sortHeap.enqueue(elem);
                }
            }
            for (int i = 0; i < array.length; i++){
                array[i] = null;
            }
            if (K <= array.length) {
                for (int i = K - 1; i >= 0; i--) {
                    array[i] = sortHeap.dequeue();
                }
            }
            else {
                while(sortHeap.size() > array.length) sortHeap.dequeue();
                for (int i = array.length - 1; i >= 0; i--) {
                    array[i] = sortHeap.dequeue();
                }
            }
        }
    }
}
