package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.datastructures.LinkedDeque;
import edu.caltech.cs2.interfaces.IDictionary;

import java.util.Iterator;

public class BSTDictionary<K extends Comparable<? super K>, V>
        implements IDictionary<K, V> {

    protected BSTNode<K, V> root;
    protected int size;

    /**
     * Class representing an individual node in the Binary Search Tree
     */
    protected static class BSTNode<K, V> {
        public K key;
        public V value;

        public BSTNode<K, V> left;
        public BSTNode<K, V> right;

        /**
         * Constructor initializes this node's key, value, and children
         */
        public BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
        }

        public BSTNode(BSTNode<K, V> o) {
            this.key = o.key;
            this.value = o.value;
            this.left = o.left;
            this.right = o.right;
        }

        public boolean isLeaf() {
            return this.left == null && this.right == null;
        }

        public boolean hasBothChildren() {
            return this.left != null && this.right != null;
        }
    }

    /**
     * Initializes an empty Binary Search Tree
     */
    public BSTDictionary() {
        this.root = null;
        this.size = 0;
    }


    @Override
    public V get(K key) {
        // check if root is null
        if (this.root == null) {
            return null;
        }

        // root is not null
        // use private recursion to start searching from the root
        return get(this.root, key);
    }

    private V get(BSTNode<K, V> current, K key) {
        // base case, where we've reached dead end
        if (current == null) {
            return null;
        }
        // base case, if we've reached a leaf
        else if (current.isLeaf()) {
            // compare to see if value is in this leaf
            if (key.compareTo(current.key) != 0) {
                return null;
            }
            else {
                return current.value;
            }
        }

        // recursive case
        // if key is less than current node, go left
        if (key.compareTo(current.key) < 0) {
            return get(current.left, key);
        }
        // if key is greater than current node, go right
        else if (key.compareTo(current.key) > 0) {
            return get(current.right, key);
        }
        // if key is the same as this node's key, return the value at this node
        else {
            return current.value;
        }
    }

    @Override
    public V remove(K key) {
        // make sure root is not null and key actually exists
        if (this.root == null || !this.containsKey(key)) {
            return null;
        }

        // value of key, to be deleted
        V prevValue = get(key);

        // remove recursively
        this.root = remove(this.root, key);

        // only decrement if we actually removed
        this.size--;
        return prevValue;
    }

    private BSTNode<K, V> remove(BSTNode<K, V> current, K key) {
        // base case if we've reached a child of a leaf and it's null
        // tell parent to keep it as null, nothing to remove
        if (current == null) {
            return null;
        }

        // base case if we reach the node with our key, return the node we want to replace it w/
        if (key.compareTo(current.key) == 0) {
            // if current is a leaf, replace it with nothing
            if (current.isLeaf()) {
                return null;
            }
            // if current has no left child, replace with right child
            else if (current.left == null) {
                return current.right;
            }
            // if current has no right child, replace with left child
            else if (current.right == null) {
                return current.left;
            }
            // else it has two children, and find the next successor to replace it with
            else {
                // save the left and right nodes for the replacing node to point to
//                BSTNode<K, V> left = current.left;
//                BSTNode<K, V> right = current.right;
                // set the replaced key/value pairing to the node being replaced
                current.key = findMin(current.right).key;
                current.value = findMin(current.right).value;
                // change the replacing node's left and right to the replaced node's left and right
//                current.left = left;
//                current.right = right;

                // now recursively delete the successive node (i.e. the node doing the replacing)
                // to avoid duplicate key
                current.right = remove(current.right, current.key);
            }
        }

        // recursive case (not deleting this node, but check if need delete children)
        // key is less than, go left to see if it should delete left child
        if (key.compareTo(current.key) < 0) {
            current.left = remove(current.left, key);
        }
        // else if key is greater than, go right to see if it should delete right child
        else if (key.compareTo(current.key) > 0) {
            current.right = remove(current.right, key);
        }
        // need to return current so that the null doesn't propagate upwards
        return current;
    }


    private BSTNode<K, V> findMin(BSTNode<K, V> current){
            // finding successor by finding the min of the right node (current is the right node)
            // while the left is not null, find the left-most node
            while (current.left != null) {
                current = current.left;
            }
            return current;
        }


    @Override
    public V put(K key, V value) {
        // will only decrement if key previously existed
        V prevValue = this.remove(key);

        // use recursion to place pair
        // remember to update the root;
        // here, passing original state of object to the method
        this.root = put(this.root, key, value);

        this.size++;
        return prevValue;
    }

    private BSTNode<K, V> put(BSTNode<K, V> current, K key, V value) {
        // base case
        if (current == null) {
            current = new BSTNode<>(key, value);
        }
        // if inserting key is less than the key at current node
        else if (key.compareTo(current.key) < 0) {
            current.left = put(current.left, key, value);
        }
        // if inserting key is greater than key at current node
        else if (key.compareTo(current.key) > 0) {
            current.right = put(current.right, key, value);
        }

        // return changed object from the method
        return current;
    }

    @Override
    public boolean containsKey(K key) {
        return this.get(key) != null;
    }

    @Override
    public boolean containsValue(V value) {
        return this.values().contains(value);
    }

    /**
     * @return number of nodes in the BST
     */
    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keys() {
        // declare / initialize keys deque
        LinkedDeque<K> keysDeque = new LinkedDeque<>();

        // don't need to check if this.head is null; done in private func
        keys(this.root, keysDeque);

        return keysDeque;
    }

    private void keys(BSTNode<K, V> current, IDeque<K> keysDeque) {
        // if current is null, don't do anything; nothing to add

        // if current is not null
        if (current != null) {
            // add the current node's key to the mega keysDeque
            keysDeque.addBack(current.key);

            // go thru all nodes underneath and all their keys too
            keys(current.left, keysDeque);
            keys(current.right, keysDeque);
        }

    }


    @Override
    public ICollection<V> values() {
        // declare / initialize values deque
        LinkedDeque<V> valuesDeque = new LinkedDeque<>();

        // don't need to check if this.head is null; done in private func
        values(this.root, valuesDeque);

        return valuesDeque;
    }

    private void values(BSTNode<K, V> current, IDeque<V> valuesDeque) {
        // if current is null, don't do anything; nothing to add

        // if current is not null
        if (current != null) {
            // add the current node's value to the mega valuesDeque
            valuesDeque.addBack(current.value);

            // go thru all nodes underneath and all their values too
            values(current.left, valuesDeque);
            values(current.right, valuesDeque);
        }

    }



    /**
     * Implementation of an iterator over the BST
     */

    @Override
    public Iterator<K> iterator() {
        return this.keys().iterator();
    }

    @Override
    public String toString() {
        if (this.root == null) {
            return "{}";
        }

        StringBuilder contents = new StringBuilder();

        IQueue<BSTNode<K, V>> nodes = new ArrayDeque<>();
        BSTNode<K, V> current = this.root;
        while (current != null) {
            contents.append(current.key + ": " + current.value + ", ");

            if (current.left != null) {
                nodes.enqueue(current.left);
            }
            if (current.right != null) {
                nodes.enqueue(current.right);
            }

            current = nodes.dequeue();
        }

        return "{" + contents.substring(0, contents.length() - 2) + "}";
    }
}
