package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IGraph;
import edu.caltech.cs2.interfaces.ISet;

public class Graph<V, E> implements IGraph<V, E> {
    ChainingHashDictionary<V, ChainingHashDictionary<V, E>> backingDict = new ChainingHashDictionary<>(
            MoveToFrontDictionary :: new
    );

    @Override
    public boolean addVertex(V vertex) {
        // check if vertex already exists?
        if (backingDict.containsKey(vertex)) {
            return false;
        }
        // add vertex
        backingDict.put(vertex, new ChainingHashDictionary<V, E>(MoveToFrontDictionary :: new));
        return true;
    }

    @Override
    public boolean addEdge(V src, V dest, E e) {
        // if one of the vertices isn't present
        if (!backingDict.containsKey(src) || !backingDict.containsKey(dest)) {
            throw new IllegalArgumentException("vertices not present in backingDict");
        }

        return backingDict.get(src).put(dest, e) == null;

    }

    @Override
    public boolean addUndirectedEdge(V n1, V n2, E e) {
        boolean didNotExist1 = addEdge(n1, n2, e);
        boolean didNotExist2 = addEdge(n2, n1, e);
        return didNotExist1 && didNotExist2;
    }

    @Override
    public boolean removeEdge(V src, V dest) {
        if (!backingDict.containsKey(src) || !backingDict.containsKey(dest)) {
            throw new IllegalArgumentException("vertices not present in backingDict");
        }

//        if (!backingDict.get(src).containsKey(dest)) {
//            return false;
//        }

        return backingDict.get(src).remove(dest) != null;
    }

    @Override
    public ISet<V> vertices() {
        return backingDict.keySet();
    }

    @Override
    public E adjacent(V i, V j) {

        if (!backingDict.containsKey(i) || !backingDict.containsKey(j)) {
            throw new IllegalArgumentException("vertices not present in backingDict");
        }

        if (backingDict.get(i).containsKey(j)) {
            return backingDict.get(i).get(j);
        }

        return null;
    }

    @Override
    public ISet<V> neighbors(V vertex) {
        return backingDict.get(vertex).keySet();
    }
}