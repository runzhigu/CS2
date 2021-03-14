package edu.caltech.cs2.datastructures;

public class AVLTreeDictionary<K extends Comparable<? super K>, V>
        extends BSTDictionary<K, V> {

    /**
     * A subclass of BSTNode representing a node in the AVLTree
     */
    private static class AVLNode<K, V> extends BSTNode<K, V> {

        public int height;

        /**
         * Constructor invokes the BSTNode constructor and initializes the height
         */
        public AVLNode(K key, V value, int height) {
            super(key, value);
            this.height = height;
        }

    }

    /**
     * Overrides the remove method in BST
     *
     * @param key
     * @return The value of the removed BSTNode if it exists, null otherwise
     */
    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Overrides the put method in BST to create AVLNode<K, V> instances
     *
     * @param key
     * @param value
     * @return the previous value corresponding to key in the AVL tree
     */
    @Override
    public V put(K key, V value) {
        return null;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
