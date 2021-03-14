package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IStack;

import java.util.Iterator;

public class LinkedDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {
    //keep track of head and tail node
//data stores the data of that specific node
    //add tail: double link list
    //prev next stuff
    //to run in constant time
    //1 node, prev and next are null
    private Node<E> head;
    private Node<E> tail;
    private int size;

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

    //constructor 1

    public LinkedDeque() {
        this(0);

    }

    //constructor 2
    public LinkedDeque(int InitialCapacity){
        this.head = null;//nothing in it when first starting
        this.tail = null;//nothing in it when first starting
        this.size = 0;
    }


    @Override
    public void addFront(E e) {
        if (this.head == null) {
            this.head = new Node<>(e);//would want to initialize tail here. Tail makes it sou you dont have to loop thorugh list
            this.tail = this.head;//1 item in list
        } else {
            this.head = new Node<>(e, this.head, null);
            this.head.next.prev = this.head;//next node of this.head pounts to this.head

        }
        this.size++;
    }

    @Override
    public void addBack(E e) {
        if (this.head == null) {
            this.head = new Node<>(e);
            this.tail = this.head;
        } else {
            this.tail= new Node<>(e, null, this.tail);//setting the node to be its own next  (e, next, prev)
            this.tail.prev.next = this.tail;//connecting the tail. No longer pointing to itself
        }
        this.size++;
    }

    @Override
    public E removeFront() {
        if (this.head == null) {
            return null;
        } else {
            if(this.size > 1) {
                E elem = this.head.data;
                this.head = this.head.next;//put the next element
                this.head.prev = null; //direction has to be null to ave new connection
                this.size--;
                return elem;
            }
            else{
                E elem = this.head.data;
                this.head = this.tail = null;
                this.size--;
                return elem;
            }
        }
    }

    @Override
    public E removeBack() {
        if (this.head == null) {
            return null;
        }
        else {
            E elem = this.tail.data;

            if (this.size > 1){
                this.tail = this.tail.prev; //disconnect old tail from list
                this.tail.next = null;
                this.size--;
                return elem;
            }
            else{
                this.head = null;
                this.tail = null;
                this.size--;
                return elem;
            }

        }

    }

    @Override
    public boolean enqueue(E e) {//Adds an element to the back of the queue
        addFront(e);
        return true;
    }

    @Override
    public E dequeue() {//Removes and returns the element at the front of the queue
        return removeBack();//FIFO
    }

    @Override
    public boolean push(E e) {
        addBack(e);
        return true;
    }

    @Override
    public E pop() {
        return removeBack(); //LIFO since have a stack
    }

    @Override
    public E peek() {
        return peekBack();
    }

    @Override
    public E peekFront() {
        if (this.head == null) {
            return null;
        } else {
            return this.head.data;//front is the tail
        }
    }

    @Override
    public E peekBack() {
        if (this.head == null) {//implies that there are no nodes?
            return null;
        } else {
            return this.tail.data;//back is the head

        }
    }

    @Override
    public Iterator<E> iterator() {
        return new LinkedDequeIterator();
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public String toString() {

        if (this.isEmpty()) {
            return "[]";
        }
        else {
            String result = "[";
            Node<E> curr = this.head;
            result += curr.data + ", ";
            while (curr.next != null) {
                curr = curr.next;
                result += curr.data + ", ";
            }
            result = result.substring(0, result.length() - 2);
            return result + "]";
        }

    }

    public class LinkedDequeIterator implements Iterator<E> {
        /*
        Option 1: "currentIndex is the previous index that was next()ed."
        Option 2: "currentIndex is the next index to give to next()"
        We chose option 2.
         */
        private Node<E> currentNode;

        public LinkedDequeIterator() {
            this.currentNode = LinkedDeque.this.head;
        }

        public boolean hasNext() {
            return this.currentNode != null;
        }

        public E next() {//want to give
            E ele = this.currentNode.data;
            this.currentNode = this.currentNode.next;
            return ele;
        }

    }
}
