package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IStack;

import java.util.Iterator;

public class ArrayDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {
    private E[] data;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;
    private static final int DEFAULT_GROWTH = 2;

    public ArrayDeque() {
        this(DEFAULT_CAPACITY);
    }

    public ArrayDeque(int initialCapacity) {
        this.data = (E[]) new Object[initialCapacity];
        this.size = 0;
    }

    @Override
    public void addFront(E e) {
        resize();

        if (this.size > 0) {
            System.arraycopy(this.data, 0, this.data, 1, this.size);
        }

        this.data[0] = e;
        this.size++;

    }

    @Override
    public void addBack(E e) {
        resize();
        this.data[this.size] = e;
        this.size++;
    }

    @Override
    public E removeFront() {

        if (this.size == 0) {
            return null;
        }

        // get value of the front element to return later
        E e = this.data[0];
        if (this.size - 1 >= 0) {
            System.arraycopy(this.data, 1, this.data, 0, this.size - 1);
        }

        this.size--;

        return e;
    }

    @Override
    public E removeBack() {
        if (this.size == 0) {
            return null;
        }
        // get last element to return
        E e = this.data[this.size - 1];
        this.size--;
        return e;

    }

    @Override
    public boolean enqueue(E e) {
        addFront(e);
        return true;
    }

    @Override
    public E dequeue() {
        return removeBack();
    }

    @Override
    public boolean push(E e) {
        addBack(e);
        return true;
    }

    @Override
    public E pop() {
        return removeBack();
    }

    @Override
    public E peek() {
        return peekBack();
    }

    @Override
    public E peekFront() {
        if (this.size == 0) {
            return null;
        }
        return this.data[0];
    }

    @Override
    public E peekBack() {
        if (this.size == 0) {
            return null;
        }
        return this.data[this.size - 1];
    }

    @Override
    public Iterator<E> iterator() {
        return new ArrayDequeIterator();
    }
    
    private class ArrayDequeIterator implements Iterator<E> {
        // currentIndex is the next index to give to next()
        private int currentIndex = 0;


        @Override
        public boolean hasNext() {
            return this.currentIndex < ArrayDeque.this.size;
        }

        @Override
        public E next() {
            E element = ArrayDeque.this.data[this.currentIndex];
            this.currentIndex++;
            return element;
        }
    }

    @Override
    public int size() {
        return this.size;
    }





    private void resize() {
        // size is how many elements have been added; length is capacity
        if (this.size == this.data.length) {
            // resize data
            E[] newData = (E[])new Object[this.data.length * DEFAULT_GROWTH];
            System.arraycopy(this.data, 0, newData, 0, this.size);
            this.data = newData;
        }
    }

    @Override
    public String toString() {
        if (this.size == 0) {
            return "[]";
        }
        else {
            StringBuilder resultSB = new StringBuilder();
            resultSB.append("[");
            for (int i = 0; i < this.size; i++) {
                resultSB.append(this.data[i]).append(", ");
            }
            String result = resultSB.toString();
            result = result.substring(0, result.length() - 2);
            return result + "]";
        }

    }
}

