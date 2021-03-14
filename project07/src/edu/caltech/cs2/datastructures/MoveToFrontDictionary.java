package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDictionary;

import java.util.Iterator;

public class MoveToFrontDictionary<K, V> implements IDictionary<K,V> {
    // fields for dictionary
    private Node<K, V> head;
    private int size;

    // constructor
    public MoveToFrontDictionary() {
        // note that head Node is null at first
        this.head = null;
        this.size = 0;
    }

    // node class
    private static class Node<K, V> {
        // fields for node
        public K key;
        public V value;
        public Node<K, V> next;

        // constructor partial
        public Node(K key, V value) {
            this(key, value, null);
        }

        // constructor full
        public Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    @Override
    public V remove(K key) {
        // if key doesn't exist, return null
        if (!this.containsKey(key)) {
            return null;
        }

        // is the key / value pairing in the head?
        if (this.head.key.equals(key)) {
            // get value for returning
            V value = this.head.value;

            // remove node and decrement size
            this.head = this.head.next;
            this.size--;

            return value;
        }

        // go through each node in linked list, start by declaring currNode
        Node<K, V> currNode = this.head;

        // we've already looked for key in this.head, so look at the next node
        while (currNode.next != null) { // might want to check size?******
            // if this is the node with the key/value pair
            if (currNode.next.key.equals(key)) {
                // get value for return
                V value = currNode.next.value;

                // remove node by skipping over it, and decrement size
                currNode.next = currNode.next.next;
                this.size--;

                return value;
            }

            // go to the next node
            currNode = currNode.next;
        }

        // shouldn't get here?
        return null;
    }

    @Override
    public V put(K key, V value) {
        // remove the key/value pair; remove checks if the key/value pair is present
        // get prevValue for return; null if key/value pair doesn't exist
        V prevValue = remove(key);

        // shift the internal head position to new head
        this.head = new Node<>(key, value, this.head);

        // increment size; note that if if key is already present, remove(key) decremented it
        // so overall would be net zero change
        this.size++;

        return prevValue;
    }

    @Override
    public boolean containsKey(K key) {
        return this.get(key) != null;
    }

    @Override
    public boolean containsValue(V value) {
        return this.values().contains(value);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keys() {
        // initialize new deque to collect keys
        LinkedDeque<K> keysDeque = new LinkedDeque<>();

        // check if head is null; if not, add head's key
        if (this.head != null) {
            keysDeque.addBack(this.head.key);
        }

        // iterate thru linked list nodes starting with this.head
        Node<K, V> currNode = this.head;

        while (currNode.next != null) { // might want to check for size too? *******
            // add to keysDeque
            keysDeque.addBack(currNode.next.key);

            // go to next node
            currNode = currNode.next;
        }

        return keysDeque;
    }

    @Override
    public ICollection<V> values() {
        // initialize new deque to collect values
        LinkedDeque<V> valuesDeque = new LinkedDeque<>();

        // check if head is null; if not, add head's value
        if (this.head == null) {
            return valuesDeque;
        }
        valuesDeque.addBack(this.head.value);

        // iterate thru linked list nodes starting with this.head
        Node<K, V> currNode = this.head;

        while (currNode.next != null) { // might want to check for size too? *******
            // add to keysDeque
            valuesDeque.addBack(currNode.next.value);

            // go to next node
            currNode = currNode.next;
        }
        return valuesDeque;
    }

    public V get(K key) {
        // check head is not null
        if (this.head == null) {
            return null;
        }

        // check if key is in head
        if (this.head.key.equals(key)) {
            return this.head.value;
        }

        // iterate thru nodes of linked list, starting w/ node after head
        Node<K, V> currNode = this.head;

        while (currNode.next != null) { // might want to check for size? *******
            // if the next node is the node we're looking for...
            if (currNode.next.key.equals(key)) {
                // return the value at the next node
                V value = currNode.next.value;

                // name this found node
                Node<K, V> foundNode = currNode.next;

                // remove this found node from its current position
                currNode.next = currNode.next.next;

                // add this found node to the front and change header position
                foundNode.next = this.head;
                this.head = foundNode;

                return value;
            }

            // go to next node
            currNode = currNode.next;
        }
        // if we get here, means that the key isn't in the dictionary
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return this.keys().iterator();
    }

}
