package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.ITrieMap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.Iterator;

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
        // if the root is null, key can't be a prefix
        if (this.root == null) {
            return false;
        }


        // traverse trie to identify key's existence
        TrieNode<A, V> currNode = this.root;

        // iterate through each alphabet of key
        for (A alphabet : key) {
            // if node pointers contain alphabet in keyset, then go down the relevant child node
            if (currNode.pointers.containsKey(alphabet)) {
                currNode = currNode.pointers.get(alphabet);
            }

            // if node doesn't contain the alphabet in its pointer keys, then key doesn't exist
            else {
                return false;
            }
        }

        // returns true, assuming all terminal leaves have values?
        return true;
    }

    @Override
    public IDeque<V> getCompletions(K prefix) {
        // result is a deque of appropriate values for a particular prefix
        IDeque<V> result = new LinkedDeque<>();

        // make sure root is not null and the prefix is a prefix
        if (isPrefix(prefix)) {
            // traverse trie to identify which branch node to start at, from root first
            TrieNode<A, V> curr = this.root;
            for (A alphabet : prefix) {
                curr = curr.pointers.get(alphabet);
            }

            // use recursion to find all the values for the prefix
            getCompletions(result, curr);
        }

        // return the result that was worked on in getCompletions
        return result;
    }

    private void getCompletions(IDeque<V> result, TrieNode<A, V> curr) {
        // if we are at a node with a value (doesn't have to be leaf)
        if (curr.value != null) {
            result.add(curr.value);
        }

        // no else statement because we might have values not at leaves
        // recurse via child nodes
        for (TrieNode<A, V> childNode : curr.pointers.values()) {
            getCompletions(result, childNode);
        }




    }

    @Override
    public void clear() {
        this.root = null;
        this.size = 0;
    }

    @Override
    public V get(K key) {
        // set current node to the root
        TrieNode<A, V> currNode = this.root;

        // iterate over alphabet of key
        for (A alphabet : key) {
            // if currNode (not just the value, but the entire node) is null
            if (currNode == null) {
                return null;
            }

            // else see if currNode has pointer key with alphabet
            else {
                if (!currNode.pointers.containsKey(alphabet)) {
                    return null;
                }
                else {
                    // set currNode to next child
                    currNode = currNode.pointers.get(alphabet);
                }
            }

        }

        // if we reach here, then the trie must have the key as a substring at least
        // but is the key actually a key in the trie? (i.e. terminal?)
        // returning the current node's value will tell us null vs the actual value
        if (currNode == null) {
            return null;
        }
        else {
            return currNode.value;
        }
    }

    @Override
    public V remove(K key) {
        // make sure root is not null and the key actually exists
        if (this.root == null || !this.containsKey(key)) {
            return null;
        }

        // value of key, to be deleted
        V prevValue = get(key);

        // iterator
        Iterator<A> keyIterator = key.iterator();

        // call private method
        remove(this.root, keyIterator);

        // see if I should remove the root node
        if (this.root.pointers.isEmpty() && this.root.value == null) {
            this.root = null;
        }

        // only decrement if we actually removed
        this.size--;
        return prevValue;
    }

    private boolean remove(TrieNode<A, V> curr, Iterator<A> keyIterator) {
        // base case if we run out of alphabet letters in the key
        if (!keyIterator.hasNext()) {
            // set the value to null; can't set node to null bc local variable
            curr.value = null;

            // if this child has no more children, tell parent to delete
            if (curr.pointers.isEmpty()) {
                return true;
            }

            // if it still has children, tell parent not to delete
            else {
                return false;
            }
        }

        // recursive case
        else {
            // go to next letter
            A nextLetter = keyIterator.next();
            // get the next node down the line
            TrieNode<A, V> child = curr.pointers.get(nextLetter);

            // should I remove my child? (recursion)
            boolean removeYes = remove(child, keyIterator);
            if (removeYes) {
                curr.pointers.remove(nextLetter);
            }

            // should I remove myself? Remove myself when I have no value and no pointers
            return curr.value == null && curr.pointers.isEmpty();
        }

    }

    @Override
    public V put(K key, V value) {
        // make sure the starting root is never null
        if (this.root == null) {
            // null value and empty hashmap, but node itself isn't null
            this.root = new TrieNode<>();
        }

        // set current node to root
        TrieNode<A, V> currNode = this.root;

        // iterate through key (deque of strings)
        // go down one level during each iteration
        for (A alphabet : key) {

            // if currNode's pointer hashmap contains alphabet
            if (currNode.pointers.containsKey(alphabet)) {
                // get this existing child node and make it the currNode
                currNode = currNode.pointers.get(alphabet);
            }

            // if currNode's pointer hashmap doesn't contain alphabet
            else {
                // make new child node
                TrieNode<A, V> childNode = new TrieNode<>();
                // insert key/value (alphabet/childNode) pair into pointers
                currNode.pointers.put(alphabet, childNode);

                // make currNode the childNode now; one level down
                currNode = childNode;
            }
        }

        // if key is already present, then we must've done if>> every time
        // V is null otherwise
        V prevValue = currNode.value;

        // now actually put the value in the last node
        currNode.value = value;

        // only increment size if it's a new value
        if (prevValue == null) {
            this.size++;
        }

        return prevValue;
    }

    @Override
    public boolean containsKey(K key) {
        return this.get(key) != null;
    }


    @Override
    public boolean containsValue(V value) {
        // for the movie trie-auto-completer, each value will be a deque of strings
        ICollection<V> values = values();

        return values.contains(value);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keys() {
        // result is a deque of individual keys (i.e. [ ["of", "ultron"], ["age", "of", "ultron"] ])
        IDeque<K> result = new LinkedDeque<>();

        // accumulator is a deque of individual alphabet letters (i.e. ["age", "of"]
        IDeque<A> accumulator = new LinkedDeque<>();

        // make sure root is not null; otherwise will have null pointer exceptions
        if (this.root != null) {
            // call private method to update result based on the root node; accumulator is modified multiple times
            keys(result, accumulator, this.root);
        }

        return result;
    }

    private void keys(IDeque<K> result, IDeque<A> accumulator, TrieNode<A, V> curr) {
        // if the value exists, then the key exists
        if (curr.value != null) {
            // add the current accumulator to the mega result ideque
            result.add(this.collector.apply(accumulator));
        }

        // no else statement because a key might be the prefix of another key
        // recurse for each child of curr
        for (A alphabet : curr.pointers.keySet()) {
            // enqueue the next letter (letter is the key in the pointer hashmap)
            accumulator.add(alphabet);

            // recurse
            keys(result, accumulator, curr.pointers.get(alphabet));

            // when done with a branch, return accumulator back to the value it had at the branch node
            accumulator.removeBack();
        }

    }

    @Override
    public ICollection<V> values() {
        /* result is a deque of deques of values
        i.e.
        [
        ["black panther", "pink panther"],
        ["love island", "treasure island"],
        ["beauty and the beast"]
        ]

         */

        // each value is a deque of strings

        IDeque<V> result = new LinkedDeque<>();

        // check root not null; otherwise will have null pointer exceptions
        if (this.root != null) {
            // call private method to update result based on the root node; accumulator is modified multiple times
            values(result, this.root);
        }
        return result;
    }

    private void values(ICollection<V> result, TrieNode<A, V> curr) {
        // if we are at a node with a value (doesn't have to be leaf)
        if (curr.value != null) {
            // add value (string) to results
            result.add(curr.value);
        }

        // no else statement because we might have values not at leaves
        // recurse via child nodes
        for (TrieNode<A, V> childNode : curr.pointers.values()) {
            // add to result mega-deque
            values(result, childNode);

        }


    }

    @Override
    public Iterator<K> iterator() {
        return this.keys().iterator();
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
