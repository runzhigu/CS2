package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.IQueue;
//import edu.caltech.cs2.misc.primeTable;

import java.lang.Math;
import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ChainingHashDictionary<K, V> implements IDictionary<K, V> {
    private final int INITIAL_TABLE_SIZE = 7;
    //    // theoretically no upper bound, but just so our table doesn't go berserk
//    private final int UPPER_BOUND = 900000;
    // tableSizes should come from primeTable
    private final int[] tableSizes = new int[] {
            7,
            17,
            37,
            79,
            163,
            331,
            673,
            1361,
            2729,
            5471,
            10949,
            21911,
            43853,
            87719,
            175447,
            350899,
            701819
    };


    // use chain.get() every time you want to make a chain; functional programming
    private Supplier<IDictionary<K, V>> chain;

    // size and capacity together compute lambda
    private int size = 0;
    private int capacity = INITIAL_TABLE_SIZE;

    // use array of dictionaries; the dictionaries represent the separate chains
    private IDictionary<K, V>[] hashTable = new IDictionary[INITIAL_TABLE_SIZE];


    public ChainingHashDictionary(Supplier<IDictionary<K, V>> chain) {
//        primeTable primeStructure = new primeTable(INITIAL_TABLE_SIZE, UPPER_BOUND);
//        this.tableSizes = primeStructure.getTableSizes();
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



//package edu.caltech.cs2.datastructures;
//
//import edu.caltech.cs2.interfaces.ICollection;
//import edu.caltech.cs2.interfaces.IDeque;
//import edu.caltech.cs2.interfaces.IDictionary;
//
//import java.util.Iterator;
//import java.util.function.Supplier;
//
//public class ChainingHashDictionary<K, V> implements IDictionary<K, V> {
//    private Supplier<IDictionary<K, V>> chain;
//    private int size;
//    private int T;
//    private static final double MAX_LOAD_FACTOR = .7;
//    private primes PRIMES;
//    private IDictionary<K,V>[] data;
//
//
//    //Implementation of Sieve of Atkin algorithm for generating primes
//    private static class primes {
//
//        private int currentPrime;
//
//        private static final int MAX_VALUE = 40000;
//        private int[] primesArray;
//
//        public primes() {
//            this(MAX_VALUE);
//        }
//        //generates prime numbers
//        public primes(int max) {
//            this.currentPrime = 5;
//            this.primesArray = generate(max);
//        }
//        //generation algorithm for prime numbers up to the argument max
//        public int[] generate(int max) {
//            boolean[] isPrime = new boolean[max];
//
//            //making an array
//            for (int i = 0; i < max; i++)
//                isPrime[i] = false;
//
//            // A number is marked as potentially prime if when meeting any of the following conditions, the
//            // corresponding boolean results in
//            // true.
//            // For a remainder mod 60 denoted r:
//            // If r is 1, 13, 17, 29, 37, 41, 49, or 53, flip boolean for each solution to 4x^2 + y^2 = n
//            // If r is 7, 19, 31, or 43, flip boolean for each possible solution to 3x^2 + y^2 = n
//            // If r is 11, 23, 47, or 59, flip the entry for each possible solution to 3x^2 – y^2 = n when x > y
//            // Else, ignore the value - it is not prime
//            for (int x = 1; x * x < max; x++) {
//                for (int y = 1; y * y < max; y++) {
//                    int n = (4 * x * x) + (y * y);
//                    if (n <= max && (n % 12 == 1 || n % 12 == 5))
//
//                        isPrime[n] ^= true;
//
//                    n = (3 * x * x) + (y * y);
//                    if (n <= max && n % 12 == 7)
//                        isPrime[n] ^= true;
//
//                    n = (3 * x * x) - (y * y);
//                    if (x > y && n <= max && n % 12 == 11)
//                        isPrime[n] ^= true;
//                }
//            }
//            // setting 2, 3, and 5 set to true
//            isPrime[2] = true;
//            isPrime[3] = true;
//            isPrime[5] = true;
//            // Narrow down the list of primes by marking multiples of squares as not prime
//            for (int i = 0; i * i < max; i++) {
//                if (isPrime[i]) {
//                    for (int j = i * i; j < max; j += i * i) {
//                        isPrime[j] = false;
//                    }
//                }
//            }
//            int count = 0;
//            for (int i = 0; i < max; i++) {
//                if (isPrime[i]) {
//                    count++;
//                }
//            }
//            int[] primesArr = new int[count];
//            int index = 0;
//            for (int i = 0; i < max; i++) {
//                if (isPrime[i]) {
//                    primesArr[index] = i;
//                    index++;
//                }
//            }
//            return primesArr;
//        }
//        //passing an already-generated primes list -
//        //no prime number generation needed
//        public primes(int[] primesArray) {
//            this.currentPrime = 1;
//            this.primesArray = primesArray;
//        }
//        //returns next prime from primes array, returns 0 if currentPrime is larger than the array size
//        public int getNextPrime() {
//            if (this.currentPrime < this.primesArray.length) {
//                int prime = this.primesArray[currentPrime];
//                this.currentPrime++;
//                return prime;
//            }
//            return this.primesArray[this.currentPrime];
//        }
//        public boolean hasNext() {
//            return this.currentPrime < this.primesArray.length;
//        }
//    }
//
//    public ChainingHashDictionary(Supplier<IDictionary<K, V>> chain) {
//        this.size = 0;
//        this.PRIMES = new primes(400000);
//        this.chain = chain;
//        this.T = this.PRIMES.getNextPrime();
//        this.data = new IDictionary[this.T];
//    }
//
//    /**
//     * @param key
//     * @return value corresponding to key
//     */
//    @Override
//    public V get(K key) {
//        // use absolute value in the case of sign overflow
//        int index = Math.abs(key.hashCode() % this.T);
//        if (this.data[index] != null) {
//            V old = this.data[index].get(key);
//            return old;
//        }
//        return null;
//    }
//
//    @Override
//    public V remove(K key) {
//        // use absolute value in the case of sign overflow
//        int index = Math.abs(key.hashCode() % this.T);
//        if (this.data[index] != null) {
//            V old = this.data[index].remove(key);
//            if (old != null) {
//                this.size--;
//            }
//            return old;
//        }
//        return null;
//    }
//
//    @Override
//    public V put(K key, V value) {
//        resize();
//        int index = Math.abs(key.hashCode() % this.T);
//        if (this.data[index] != null) {
//            V old = this.data[index].get(key);
//            this.data[index].put(key, value);
//            if (old == null) {
//                this.size++;
//            }
//            return old;
//        }
//        this.data[index] = chain.get();
//        this.data[index].put(key, value);
//        this.size++;
//        return null;
//    }
//
//    @Override
//    public boolean containsKey(K key) {
//        return this.get(key) != null;
//    }
//
//    /**
//     * @param value
//     * @return true if the HashDictionary contains a key-value pair with
//     * this value, and false otherwise
//     */
//    @Override
//    public boolean containsValue(V value) {
//        return this.values().contains(value);
//    }
//
//    /**
//     * @return number of key-value pairs in the HashDictionary
//     */
//    @Override
//    public int size() {
//        return this.size;
//    }
//
//    @Override
//    public ICollection<K> keys() {
//        IDeque<K> keys = new ArrayDeque<>();
//        for (IDictionary<K,V> dict : this.data) {
//            if (dict != null && !dict.isEmpty()) {
//                for (K key : dict.keys()) {
//                    keys.add(key);
//                }
//            }
//        }
//        return keys;
//    }
//
//    @Override
//    public ICollection<V> values() {
//        IDeque<V> values = new ArrayDeque<>();
//        for (IDictionary<K,V> dict : this.data) {
//            if (dict != null && !dict.isEmpty()) {
//                for (V value : dict.values()) {
//                    values.add(value);
//                }
//            }
//        }
//        return values;
//    }
//
//    private void resize() {
//        if (Math.abs(this.size/this.T * 1.0) > this.MAX_LOAD_FACTOR && this.PRIMES.hasNext()) {
//            while (this.T < 2 * this.size + 1 && this.PRIMES.hasNext()) {
//                this.T = this.PRIMES.getNextPrime();
//            }
//            IDictionary<K,V>[] newData = new IDictionary[this.T];
//            IDictionary<K,V>[] oldData = this.data;
//            this.data = newData;
//            for (IDictionary<K,V> dict : oldData) {
//                if (dict != null) {
//                    for (K key : dict.keys()) {
//                        this.size--;
//                        put(key, dict.get(key));
//
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * @return An iterator for all entries in the HashDictionary
//     */
//    @Override
//    public Iterator<K> iterator() {
//        return this.keys().iterator();
//    }
//}
