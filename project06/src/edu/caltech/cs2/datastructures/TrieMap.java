package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.ITrieMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public class TrieMap<A, K extends Iterable<A>, V> implements ITrieMap<A, K, V> {
    private TrieNode<A, V> root;
    private Function<IDeque<A>, K> collector;
    private int size;

    public TrieMap(Function<IDeque<A>, K> collector) {
        this.root = null;
        this.collector = collector;
        this.size = 0;
    }
    

    @Override
    public boolean isPrefix(K key) {
    //not going to get an actual key only
        if(this.root == null){
            return false;
        }
        TrieNode<A,V> curr = this.root;
        for(A s: key){
            if (curr.pointers.containsKey(s)){
                curr = curr.pointers.get(s);//going down the Trie
            }
            else{
                return false;
            }
        }
        return true;
    }

    @Override
    public IDeque<V> getCompletions(K prefix) {
        HashSet<K> keys = new HashSet<>();
        ArrayDeque<V> completions = new ArrayDeque<>();
        TrieNode<A,V> curr = this.root;
        if (curr != null) {
        for(A s: prefix) {
            if (curr.pointers.containsKey(s)) {
                curr = curr.pointers.get(s);//going down the Trie
            } else {
                return completions;
            }
        }
        }
        values(curr, completions);
        return completions;
    }

    @Override
    public void clear() {
        this.root = null;
        this.size = 0;
    }

    @Override
    public V get(K key) {
        if(this.root == null){
            return null;
        }
        TrieNode<A,V> curr = this.root;
        for(A s: key){
            if (curr.pointers.containsKey(s)){
                curr = curr.pointers.get(s);//going down the Trie
            }
            else{
                return null;
            }
        }
        return curr.value;
    }

    @Override
    public V remove(K key) {
        if (this.root == null) {
            return null;
        }
        V value = this.get(key);
        Iterator iter = key.iterator();
        remove(this.root, key, iter);
        if (value != null) {
            return value;
        }
        return null;
    }
    private boolean remove(TrieNode curr, K key, Iterator iter) {
//        if (curr.value == null && curr.pointers.isEmpty()) {
//            curr = null;
//            //this.size --;
//            return false;
//        }
        if (!iter.hasNext()) {
            curr.value = null;
            curr = null;
            this.size --;
            return false;
        }
        else {
            Object nextNode = iter.next();
            if (curr.pointers.keySet().contains(nextNode)) {
                boolean shouldKeep = remove((TrieNode) curr.pointers.get(nextNode), key, iter);
                if (shouldKeep == false) {
                    curr.pointers.remove(nextNode);
                    this.size--;
                }
                if (curr.value == null && curr.pointers.isEmpty()) {
                    curr = null;
                    //this.size--;
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public V put(K key, V value) {
        if(this.root == null){
            TrieNode<A,V> newNode = new TrieNode<>();
            this.root = newNode;
        }
        TrieNode<A,V> curr = this.root;
        for(A s: key){
            if (curr.pointers.containsKey(s)){
                curr = curr.pointers.get(s);//going down the Trie
            }
            else{
                TrieNode<A,V> newNode = new TrieNode<>();
                curr.pointers.put(s, newNode);//arrow goes to a new node
                curr = newNode;//want to go down arrow (new node below it)
            }
        }
        V old = curr.value;
        curr.value = value;
        if(old == null){
            size++; //number of keys
        }
        return old;
    }

    @Override
    public boolean containsKey(K key) {
        if(this.root == null){
            return false;
        }
        TrieNode<A,V> curr = this.root;
        for(A s: key){
            if (curr.pointers.get(s) != null){
                curr = curr.pointers.get(s);//going down the Trie
            }
            else{
                return false;
            }
        }
        if (curr.value != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(V value) {
        if (this.root != null) {
            return containsValue(value, this.root);
        }
        return false;
    }

    private boolean containsValue(V value, TrieNode curr) {
        if (curr.value != null) {
            if (curr.value.equals(value)) {
                return true;
            }
        }
        for (Object node : curr.pointers.values()) {
            TrieNode<A, V> castNode = (TrieNode<A, V>) node;
            containsValue(value, castNode);
        }
        return true;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keys() {
        ArrayDeque keyDeque = new ArrayDeque();
        ArrayDeque acc = new ArrayDeque();
        if (this.root != null) {
            keys(this.root, keyDeque, acc);
        }
        //System.out.println(keyDeque.toString());
        return keyDeque;
    }

    private void keys(TrieNode curr, ArrayDeque keyDeque, ArrayDeque acc) {
        if (curr.value != null) {
            keyDeque.add(collector.apply(acc));
            //System.out.println(acc.toString());
            //System.out.println("recorded");
        }

        // traverse all the paths
        //if (curr.pointers.keySet().size() != 0) {
            for (Object c : curr.pointers.keySet()) {
                acc.addBack(c); // add to acc
                //if (curr.pointers.get(c) != null) {
                    keys((TrieNode) curr.pointers.get(c), keyDeque, acc); // recurse
                //}
                acc.removeBack();
            }
       // }
    }

    @Override
    public ICollection<V> values() {
        ArrayDeque valueDeque = new ArrayDeque();
        if (this.root != null) {
            values(this.root, valueDeque);
        }
        return valueDeque;
    }

    private void values(TrieNode curr, ArrayDeque valueDeque) {
        if (curr.value != null) {
            valueDeque.add(curr.value);
        }
        for (Object node : curr.pointers.values()) {
            TrieNode<A, V> castNode = (TrieNode<A, V>) node;
            //valueDeque.add(castNode.value);
            values(castNode, valueDeque);
        }
    }

    @Override
    public Iterator<K> iterator() {
        return keys().iterator();
    }
    private class TrieMapIterator implements Iterator<K> {
        /*
        Option 1: "currentIndex is the previous index that was next()ed."
        Option 2: "currentIndex is the next index to give to next()"
        We chose option 2.
         */
        private int currentIndex;

        public TrieMapIterator() {
            this.currentIndex = 0;
        }

        public boolean hasNext() {
            return this.currentIndex < (TrieMap.this).keys().size();
        }

        public K next() {
            K element = TrieMap.this.keys().iterator().next();
            this.currentIndex++;
            return element;
        }
    }

    private static class TrieNode<A, V> {
        public final Map<A, TrieNode<A, V>> pointers;
        public V value;

        public TrieNode() {
            this(null);
        }

        public TrieNode(V value) {
            this.pointers = new HashMap<>();
            this.value = value;
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            if (this.value != null) {
                b.append("[" + this.value + "]-> {\n");
                this.toString(b, 1);
                b.append("}");
            }
            else {
                this.toString(b, 0);
            }
            return b.toString();
        }

        private String spaces(int i) {
            StringBuilder sp = new StringBuilder();
            for (int x = 0; x < i; x++) {
                sp.append(" ");
            }
            return sp.toString();
        }

        protected boolean toString(StringBuilder s, int indent) {
            boolean isSmall = this.pointers.entrySet().size() == 0;

            for (Map.Entry<A, TrieNode<A, V>> entry : this.pointers.entrySet()) {
                A idx = entry.getKey();
                TrieNode<A, V> node = entry.getValue();

                if (node == null) {
                    continue;
                }

                V value = node.value;
                s.append(spaces(indent) + idx + (value != null ? "[" + value + "]" : ""));
                s.append("-> {\n");
                boolean bc = node.toString(s, indent + 2);
                if (!bc) {
                    s.append(spaces(indent) + "},\n");
                }
                else if (s.charAt(s.length() - 5) == '-') {
                    s.delete(s.length() - 5, s.length());
                    s.append(",\n");
                }
            }
            if (!isSmall) {
                s.deleteCharAt(s.length() - 2);
            }
            return isSmall;
        }
    }
}
