package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IStack;

import java.util.Iterator;

public class LinkedDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {

    private Node<E> head;
    private int size;
    private Node<E> tail;


    public LinkedDeque(){
        // note that head data is null at first
        this.head = null;
        this.tail = null;
        this.size = 0;
    }
    private static class Node<E> {
        public final E data;
        public Node<E> next;
        public Node<E> prev;

        public Node(E data) {
            this(data, null, null);
        }

        public Node(E data, Node<E> next, Node<E> prev) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    @Override
    public void addFront(E e) {
        if (this.size == 0) {
            this.head = new Node<>(e);
            this.tail = this.head;
        }
        else {
            // new head's "next" points to the previous head; no "prev"
            Node<E> newHead = new Node<>(e, this.head, null);

            // previous head's "prev" points to newHead
            this.head.prev = newHead;

            // now actually switch node head positions
            this.head = newHead;
        }
        this.size++;
    }

    @Override
    public void addBack(E e) {
        if (this.size == 0) {
            this.head = new Node<>(e);
            this.tail = this.head;
        }
        else {
            // newTail is now the newly created node; "prev" points to previous tail
            Node<E> newTail = new Node<>(e, null, this.tail);

            // previous tail's "next" points to newTail
            this.tail.next = newTail;

            // now actually switch node tail positions
            this.tail = newTail;
        }
        this.size++;
    }

    @Override
    public E removeFront() {

        if (this.size == 0) {
            return null;
        }

        // get data for return
        E headData = this.head.data;

        // shift header position
        this.head = this.head.next;

        // remove prev link
        if (this.head != null) {
            this.head.prev = null;
        }

        // decrement size
        this.size--;

        // if we don't have any more elements, need to change tail
        if (this.size == 0) {
            this.tail = this.head;
        }

        return headData;
    }

    @Override
    public E removeBack() {
        if (this.size == 0) {
            return null;
        }
        // get data for return
        E tailData = this.tail.data;

        // shift tail position
        this.tail = this.tail.prev;


        // remove link
        if (this.tail != null) {
            this.tail.next = null;
        }

        // decrement size
        this.size--;

        // if we don't have any more elements, need to change head
        if (this.size == 0) {
            this.head = this.tail;
        }

        return tailData;
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
        return this.head.data;
    }

    @Override
    public E peekBack() {
        if (this.size == 0) {
            return null;
        }
        return this.tail.data;
    }

    @Override
    public Iterator<E> iterator() {
        return new LinkedDequeIterator();
    }

    private class LinkedDequeIterator implements Iterator<E> {
        // currNode is the next index to give to next()
        private Node<E> currNode = LinkedDeque.this.head;
        private int count = 0;


        @Override
        public boolean hasNext() {
            // make sure we don't go beyond the bounds of the deque, since we aren't nullifying
            return this.currNode != null && this.count < LinkedDeque.this.size;
        }

        @Override
        public E next() {
            E element = currNode.data;
            currNode = currNode.next;
            this.count++;
            return element;

        }
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public String toString() {
        if (this.size == 0) {
            return "[]";
        }
        else {
            StringBuilder resultSB = new StringBuilder();
            resultSB.append("[");
            Node<E> currNode = this.head;
            for (int i = 0; i < this.size; i++) {
                resultSB.append(currNode.data).append(", ");
                currNode = currNode.next;
            }
            String result = resultSB.toString();
            result = result.substring(0, result.length() - 2);
            return result + "]";
        }

    }
}
