package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.IPriorityQueue;

import java.util.Iterator;

public class MinFourHeap<E> implements IPriorityQueue<E> {

    private static final int DEFAULT_CAPACITY = 10;
    private static final int DEFAULT_GROWTH = 4;

    private int size;
    private PQElement<E>[] data;
    private IDictionary<E, Integer> keyToIndexMap;

    /**
     * Creates a new empty heap with DEFAULT_CAPACITY.
     */
    public MinFourHeap() {
        this.size = 0;
        this.data = new PQElement[DEFAULT_CAPACITY];
        this.keyToIndexMap = new ChainingHashDictionary<>(MoveToFrontDictionary::new);

    }

    @Override
    public void decreaseKey(PQElement<E> key) {
        // check if key already exists in our data map
        if (!keyToIndexMap.containsKey(key.data)) {
            throw new IllegalArgumentException("data doesn't exist currently");
        }

        // get current index of key
        int currentIndex = keyToIndexMap.get(key.data);
        // change to the new priority
        this.data[currentIndex] = key;

        // percolate up
        // must ensure node's priority value is less than its parent's
        // parent index is floor((currentIndex - 1) / 4)
        while (key.priority < this.data[(currentIndex - 1) / 4].priority) {
            int parentIndex = (currentIndex - 1) / 4;
            PQElement<E> parentNode = this.data[parentIndex];

            // swap parent and child
            this.data[parentIndex] = this.data[currentIndex];
            this.data[currentIndex] = parentNode;

            // change keyToIndexMap for percolated parent
            keyToIndexMap.put(parentNode.data, currentIndex);

            // swap indices
            currentIndex = parentIndex;
        }

        // change keyToIndexMap for key (if didn't percolate, currentIndex remains the same)
        keyToIndexMap.put(key.data, currentIndex);
    }





    @Override
    public void increaseKey(PQElement<E> key) {
        // check if key already exists in our data map
        if (!keyToIndexMap.containsKey(key.data)) {
            throw new IllegalArgumentException("data doesn't exist currently");
        }

        // get current index of key
        int currentIndex = keyToIndexMap.get(key.data);
        // change to the new priority
        this.data[currentIndex] = key;

        // percolate down
        // must ensure node's priority value is greater than each of its children's
        // use smallestChild helper function, which returns index of child node with smallest priority value
        // -1 means there is no children at all
        while (
                smallestChildIndex(currentIndex) != -1 &&
                key.priority > this.data[smallestChildIndex(currentIndex)].priority
        ) {
            // what is the index of the child I should percolate w/?
            int childIndex = smallestChildIndex(currentIndex);

            // save child node for swap
            PQElement<E> childNode = this.data[childIndex];

            // swap parent and child
            this.data[childIndex] = this.data[currentIndex];
            this.data[currentIndex] = childNode;

            // change keyToIndexMap for the percolated node
            keyToIndexMap.put(childNode.data, currentIndex);

            // swap indices
            currentIndex = childIndex;
        }

        // change keyToIndexMap for key node
        keyToIndexMap.put(key.data, currentIndex);

    }

    /**
     find index of smallest (highest priority) of four children nodes
     @param parentIndex parent node index
     @return index of smallest child
     **/
    private int smallestChildIndex(int parentIndex) {
        // if no children at all, return -1; anything beyond this.size is null
        // greater than equal to bc zero indexing of this.data array
        if (parentIndex * 4 + 1 >= this.size) {
            return -1;
        }

        // parentIndex is parent node index
        // smallestChildIndex is index the smallest child node, initialize to first child
        int smallestChildIndex = parentIndex * 4 + 1;

        // if second child is null, then first child is min
        if (parentIndex * 4 + 2 >= this.size) {
            return smallestChildIndex;
        }
        // else compare first and second child
        else if (this.data[parentIndex * 4 + 2].priority < this.data[smallestChildIndex].priority) {
            smallestChildIndex = parentIndex * 4 + 2;
        }

        // if third child is null, return current value
        if (parentIndex * 4 + 3 >= this.size) {
            return smallestChildIndex;
        }
        // else compare second and third child
        else if (this.data[parentIndex * 4 + 3].priority < this.data[smallestChildIndex].priority) {
            smallestChildIndex = parentIndex * 4 + 3;
        }

        // if fourth child is null, return current value
        if (parentIndex * 4 + 4 >= this.size) {
            return smallestChildIndex;
        }
        // else compare third and fourth child
        else if (this.data[parentIndex * 4 + 4].priority < this.data[smallestChildIndex].priority) {
            smallestChildIndex = parentIndex * 4 + 4;
        }
        return smallestChildIndex;
    }





    @Override
    public boolean enqueue(PQElement<E> epqElement) {
        // resize if necessary
        resize();


        // if epqElement already exists in heap
        if (this.keyToIndexMap.containsKey(epqElement.data)) {
            throw new IllegalArgumentException("no repeated elements");
        }

        // it doesn't exist in heap yet
        // add new node as the very bottom right most node
        this.data[this.size] = epqElement;
        int currentIndex = this.size;

        // percolate up
        // must ensure node's priority value is less than its parent's
        // parent index is floor((currentIndex - 1) / 4)
        while (epqElement.priority < this.data[(currentIndex - 1) / 4].priority
        ) {
            int parentIndex = (currentIndex - 1) / 4;
            PQElement<E> parentNode = this.data[parentIndex];

            // swap parent and child
            this.data[parentIndex] = this.data[currentIndex];
            this.data[currentIndex] = parentNode;

            // change keytoIndexMap
            keyToIndexMap.put(epqElement.data, parentIndex);
            keyToIndexMap.put(parentNode.data, currentIndex);

            // swap indices
            currentIndex = parentIndex;
        }

        // add to dict
        this.keyToIndexMap.put(epqElement.data, currentIndex);

        // increment size
        this.size++;
        return true;
    }

    private void resize() {
        // size is how many elements have been added; length is capacity
        if (this.size == this.data.length) {
            // resize data
            PQElement<E>[] newData = new PQElement[this.data.length * DEFAULT_GROWTH];
            System.arraycopy(this.data, 0, newData, 0, this.size);
            this.data = newData;
        }
    }

    @Override
    public PQElement<E> dequeue() {
        // get for return statement
        PQElement<E> dpqElement = this.data[0];

        if (dpqElement != null) {
            // replace top of heap with the bottom / right most node
            PQElement<E> percolateElement = this.data[this.size - 1];
            this.data[0] = percolateElement;

            // percolate down
            // must ensure node's priority value is greater than each of its children's
            // use smallestChild helper function, which returns index of child node with smallest priority value
            // -1 means there is no children at all
            int currentIndex = 0;
            while (
                    smallestChildIndex(currentIndex) != -1 &&
                    percolateElement.priority > this.data[smallestChildIndex(currentIndex)].priority
            ) {
                // what is the index of the child I should percolate w?
                int childIndex = smallestChildIndex(currentIndex);

                // save child node for swap
                PQElement<E> childNode = this.data[childIndex];

                // swap parent and child
                this.data[childIndex] = this.data[currentIndex];
                this.data[currentIndex] = childNode;

                // change keyToIndexMap for percolated node
                keyToIndexMap.put(childNode.data, currentIndex);

                // swap indices
                currentIndex = childIndex;
            }
            // change keyToIndexMap for changed node
            keyToIndexMap.put(percolateElement.data, currentIndex);

            // remove dequeued element from keyToIndexMap
            this.keyToIndexMap.remove(dpqElement.data);

            // decrement size
            this.size--;
        }
        return dpqElement;
    }

    @Override
    public PQElement<E> peek() {
        return this.data[0];
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Iterator<PQElement<E>> iterator() {
        return null;
    }
}