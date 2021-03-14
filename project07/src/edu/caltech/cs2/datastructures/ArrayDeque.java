package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IStack;

import java.util.Iterator;

//do i need to shift everything when i add to the front and remove from the front since this is an array?
//how about from the back?
//intantiate array dekck and then default capacity (2 constructors)
//index is size -1!!!!
public class ArrayDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {

    private static final int DEFAULT_CAPACITY = 10;
    private static final int GROW_FACTOR = 2;
    private E[] data; //creates an array
    private int size;

    //constructor 1
    public ArrayDeque() {
        this(10);
    }

    //constructor 2
    public ArrayDeque(int initialCapacity) {
        this.data = (E[]) new Object[initialCapacity];
        this.size = 0;

    }

    private void resize() { //why is this private???
        // size vs. capacity < how much storage space in the list
        // ^ how many elements in the list
        // capacity == this.data.length
        if (this.size == this.data.length) {
            // Time to resize!
            E[] newData = (E[]) new Object[this.data.length * GROW_FACTOR];
            for (int i = 0; i < this.size; i++) {
                newData[i] = this.data[i];
            }
            this.data = newData;
        }
    }

//create method that checks array has capacity or expands capacity to have enough space

    @Override
    public void addFront(E e) {//want to check that the size can afford to add
        if (this.size > 0) { //want to be able to expand if run out of space (multiply capacity by growth factor)
            resize();
            for (int i = this.size; i > 0; i--) {
                this.data[i] = this.data[i - 1];
            }
        }

        this.data[0] = e;
        this.size++;

    }

    @Override
    public void addBack(E e) {
        resize();
        this.data[this.size] = e;//dont need -1 since i want to add something so size changes
        this.size++;
    }

    @Override
    public E removeFront() {
        if (this.size != 0) {
            E ele = this.data[0];

            for (int i = 0; i < this.size-1; i++) {//-1 bc index
                this.data[i] = this.data[i + 1];
            }

            //[abcd]
            //[bcdd]
            this.data[this.size-1] = null;
            this.size--;
            return ele;
        } else {
            return null;
        }
    }

    @Override
    public E removeBack() {//create a new array without it???
        if (!(this.size == 0)) {
            E ele = this.data[this.size-1];
            this.data[this.size-1] = null;
            this.size--;
            return ele;
        } else {
            return null;
        }
    }

    @Override
    public boolean enqueue(E e) {//Adds an element to the back of the queue
        addFront(e);
        return true;
    }

    //front:[cba]:back//adding from the front
    @Override
    public E dequeue() {//Removes and returns the element at the front of the queue
        return removeBack();//FIFO
    }

    @Override
    // WHEN IS THIS FALSE??????
    public boolean push(E e) {//adds element to the top of the stack
        addBack(e);
        return true;//always will be able to resize so never false

    }

    @Override
    public E pop() {//Removes and returns the element at the top of the stack
        return removeBack(); //LIFO since have a stack
    }

    @Override
    public E peek() {//Queue: peek first, stack: last one, dequqe is dbl sided queue, (usually add or remove elements at end of array
        return peekBack();
    }

    @Override
    public E peekFront() {
        if (this.size != 0) {
            return this.data[0];
        }
        else {
            return null;
        }
    }

    @Override
    public E peekBack() {
        if (!(this.size == 0)) {
            return this.data[this.size - 1];
        } else {
            return null;
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new ArrayDequeIterator();
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    //When to use this and why it's not recognizing it?
    public String toString() {
        if (this.isEmpty()) {
            return "[]";
        }

        String result = "[";
        for (int i = 0; i < this.size; i++) {
            result += this.data[i] + ", ";
        }

        result = result.substring(0, result.length() - 2);
        return result + "]";
    }


    public class ArrayDequeIterator implements Iterator<E> {
        /*
        Option 1: "currentIndex is the previous index that was next()ed."
        Option 2: "currentIndex is the next index to give to next()"
        We chose option 2.
         */
        private int currentIndex;

        public ArrayDequeIterator() {
            this.currentIndex = 0;
        }

        public boolean hasNext() {
            return this.currentIndex < (ArrayDeque.this).size();
        }

        public E next() {
            E element = ArrayDeque.this.data[this.currentIndex];
            this.currentIndex++;
            return element;
        }
    }
}

