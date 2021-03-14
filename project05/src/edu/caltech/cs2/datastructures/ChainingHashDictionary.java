package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.textgenerator.primeTable;

import java.lang.Math;
import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ChainingHashDictionary<K, V> implements IDictionary<K, V> {
    private final int INITIAL_TABLE_SIZE = 7;
    // theoretically no upper bound, but just so our table doesn't go berserk
    private final int UPPER_BOUND = 900000;
    // tableSizes should come from primeTable
    private final int[] tableSizes;


    // use chain.get() every time you want to make a chain; functional programming
    private Supplier<IDictionary<K, V>> chain;

    // size and capacity together compute lambda
    private int size = 0;
    private int capacity = INITIAL_TABLE_SIZE;

    // use array of dictionaries; the dictionaries represent the separate chains
    private IDictionary<K, V>[] hashTable = new IDictionary[INITIAL_TABLE_SIZE];


    public ChainingHashDictionary(Supplier<IDictionary<K, V>> chain) {
        primeTable primeStructure = new primeTable(INITIAL_TABLE_SIZE, UPPER_BOUND);
        this.tableSizes = primeStructure.getTableSizes();
        this.chain = chain;
    }

    /**
     * @param key key to find
     * @return value corresponding to key
     */
    @Override
    public V get(K key) {
        // figure out the address of the key based on hashcode modding of table length
        int address = Math.abs(key.hashCode() % this.capacity);

        // make sure there is a dictionary at the address and it isn't empty
        if (this.hashTable[address] == null || this.hashTable[address].isEmpty()) {
            return null;
        }

        // we know there is a dictionary, so if the key exists, it must exist at that address
        // use get function of Supplier dictionary
        return this.hashTable[address].get(key);


//        // iterate thru dictionary via iterator
//        for (K existingKey : this.hashTable[address]) {
//            // check for key equality; not sorted, so no comparison here
//            if (existingKey.equals(key)) {
//                // this get is the Supplier dict's get function; not the chainingHashDictionary
//                return this.hashTable[address].get(existingKey);
//            }
//        }
        // if get here, key doesn't exist in separate chained dictionary
//        return null;

        }


    @Override
    public V remove(K key) {
        // for return
        V value = this.get(key);

        // don't go further if the key doesn't exist
        if (value == null) {
            return null;
        }

        // key must exist
        // get table address via hashcode
        int address = Math.abs(key.hashCode() % this.capacity);

        // remove using Supplier's remove function
        this.hashTable[address].remove(key);

        // decrement size
        this.size--;

        return value;

    }

    private void resize() {
        // lambda = size / capacity; resize when lambda >= 1
        if (this.size / this.capacity >= 1) {
            // what should our next capacity be? (make sure prime, > 2 * current capacity + 1
            int nextCapacity = this.capacity;

            // iterate thru our table sizes
            for (int tableSize : this.tableSizes) {
                // must be greater than 2 times our current table length
                // already hard coded (as in, only double + 1 in the table, but make sure twice)
                if (tableSize > 2 * this.capacity + 1) {
                    nextCapacity = tableSize;
                    break;
                }
            }

            // create new hashTable as an array of iDictionary
            IDictionary<K, V>[] newHashTable = new IDictionary[nextCapacity];
            // iterate thru previous hash table and separate chains to put into new hash table
            for (int i = 0; i < this.capacity; i++) { // this.capacity is the previous capacity
                // only add if there exists a separate chain within the table cell
                if (this.hashTable[i] != null) { // see if there is null pointer exception
                    for (K key : this.hashTable[i]) {
                        int newAddress =Math.abs(key.hashCode() % nextCapacity);
                        // insert into the new hash table
                        // check if dictionary already exists there
                        if (newHashTable[newAddress] == null) {
                            // if not, use supplier to initialize a new one
                            newHashTable[newAddress] = chain.get();
                        }
                        // add key / value into new dictionary
                        newHashTable[newAddress].put(key, this.hashTable[i].get(key));
                    }
                }
            }

            // make the this.hashTable the one we just created
            this.hashTable = newHashTable;
            this.capacity = nextCapacity;
            // none of the other fields should change



        }
    }

    @Override
    public V put(K key, V value) {
        this.resize();
        // see if previously had a value for return
        // will only decrement if key previously existed
        V prevValue = this.remove(key);

        // get table address via hashcode
        int address = Math.abs(key.hashCode() % this.capacity);

        // if no dictionary is there, make one
        if (this.hashTable[address] == null) {
            this.hashTable[address] = chain.get();
        }

        // go to address and add key/value pair
        this.hashTable[address].put(key, value);
        this.size++;

        return prevValue;
    }

    @Override
    public boolean containsKey(K key) {

        return this.get(key) != null;
    }

    /**
     * @param value to check
     * @return true if the HashDictionary contains a key-value pair with
     * this value, and false otherwise
     */
    @Override
    public boolean containsValue(V value) {
        return this.values().contains(value);
    }

    /**
     * @return number of key-value pairs in the HashDictionary
     */
    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keys() {
        // declare / initialize keys deque
        LinkedDeque<K> keysDeque = new LinkedDeque<>();

        // go thru each cell in the table
        for (int address = 0; address < this.capacity; address++) {
            // make sure not null and not empty
            if (this.hashTable[address] != null && !this.hashTable[address].isEmpty()) {
                // add all keys in a cell at once
                keysDeque.addAll(this.hashTable[address].keys());
            }
        }
        return keysDeque;
    }

    @Override
    public ICollection<V> values() {
        // declare / initialize keys deque
        LinkedDeque<V> valuesDeque = new LinkedDeque<>();

        // go thru each cell in the table
        for (int address = 0; address < this.capacity; address++) {
            // make sure not null and not empty
            if (this.hashTable[address] != null && !this.hashTable[address].isEmpty()) {
                // add all keys in a cell at once
                valuesDeque.addAll(this.hashTable[address].values());
            }
        }
        return valuesDeque;
    }

    /**
     * @return An iterator for all entries in the HashDictionary
     */
    @Override
    public Iterator<K> iterator() {
        return this.keys().iterator();
    }
}
