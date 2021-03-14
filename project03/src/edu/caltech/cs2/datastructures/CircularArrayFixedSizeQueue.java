package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IFixedSizeQueue;

import java.util.Iterator;

public class CircularArrayFixedSizeQueue<E> implements IFixedSizeQueue<E> {

    private E[] data;
    // front represents first index of array
    private int front;
    // back represents the last index of array
    private int back;

    public CircularArrayFixedSizeQueue(int capacity) {
        // initialize empty list
        this.data = (E[]) new Object[capacity];

        // front and back are both initially 0; size = 0 in this case since front points to null
        this.front = 0;
        this.back = 0;

    }

    @Override
    public boolean isFull() {
        return this.size() == this.capacity();
    }

    @Override
    public int capacity() {
        return this.data.length;
    }

    @Override
    public boolean enqueue(E e) {
        if (this.isFull()) {
            return false;
        }

        // null means the circular array is empty; add element, but don't increment
        if (this.data[this.front] == null) {
            this.data[this.back] = e;
        }
        // circular array is not empty, so add element at next index
        else {
            // get next index according to mod
            int nextIdx = (this.back + 1) % this.capacity();

            // add and update back
            this.data[nextIdx] = e;
            this.back = nextIdx;
        }
//        this.size = this.size();
        return true;
    }

    @Override
    public E dequeue() {
        if (this.size() == 0) {
            return null;
        }

        // extract front most data for return
        E e;
        e = this.data[this.front];

        // make data point null
        this.data[this.front] = null;

        // update front pointer, depending on size of array
        // if front == back, then we have an empty array, so don't move front
        if (this.front != this.back) {
            this.front = (this.front + 1) % this.capacity();
        }

        return e;
    }

    @Override
    public E peek() {
        if (this.size() == 0) {
            return null;
        }

        return this.data[this.front];
    }

    @Override
    public int size() {
        // if front points to null, size is  0
        if (this.data[this.front] == null) {
            return 0;
        }
        else {
            // mod can return negative numbers, so add this.capacity()
            return (this.back - this.front + this.capacity()) % this.capacity() + 1;
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new CircularArrayFixedSizeQueueIterator();
    }

    private class CircularArrayFixedSizeQueueIterator implements Iterator<E> {
        private int idx = 0;


        @Override
        public boolean hasNext() {
            return this.idx < CircularArrayFixedSizeQueue.this.size();
        }


        @Override
        public E next() {
            E element = CircularArrayFixedSizeQueue.this.data[
                    (this.idx + CircularArrayFixedSizeQueue.this.front) % CircularArrayFixedSizeQueue.this.capacity()
                    ];
            this.idx++;
            return element;

        }
    }

    @Override
    public String toString() {
        if (this.size() == 0) {
            return "[]";
        }
        else {
            StringBuilder resultSB = new StringBuilder();
            resultSB.append("[");

            // iterate thru the backing array
            for (int i = 0; i < this.size(); i++) {
                // use mod to wrap around
                resultSB.append(
                        this.data[(i+this.front) % this.capacity()]
                ).append(
                        ", "
                );
            }

            // convert to string and remove last comma and space
            String result = resultSB.toString();
            result = result.substring(0, result.length() - 2);
            return result + "]";
        }

    }
}
