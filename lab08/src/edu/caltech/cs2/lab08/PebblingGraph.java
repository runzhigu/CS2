package edu.caltech.cs2.lab08;

import java.util.*;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class PebblingGraph {
    public final Set<PebblingNode> vertices;

    public static class PebblingNode {
        public final int id;
        public final Set<PebblingNode> neighbors;
        public int inDegree;

        public PebblingNode(int id) {
            this.id = id;
            this.neighbors = new HashSet<>();
            this.inDegree = 0;
        }

        public String toString() {
            return this.id + " -> {" + this.neighbors.stream().map(v -> "" + v.id).sorted().collect(Collectors.joining(", ")) + "}";
        }
    }

    public PebblingGraph() {
        this.vertices = new HashSet<>();
    }

    public PebblingNode addVertex(int id) {
        PebblingNode v = new PebblingNode(id);
        this.vertices.add(v);
        return v;
    }

    public void addEdge(PebblingNode fromVertex, PebblingNode toVertex) {
        if (!this.vertices.contains(fromVertex) || !this.vertices.contains(toVertex)) {
            throw new IllegalArgumentException("Vertices don't exist in graph");
        }

        fromVertex.neighbors.add(toVertex);
        toVertex.inDegree++;
    }

    public List<Integer> toposort() {
        // setup

        // output will be the order of IDs to take the pebbles out in
        List<Integer> output = new ArrayList<>();
        // what r the in degree counts of a given node key?
        Map<PebblingNode, Integer> deps = new HashMap<>();
        // pebbling nodes list; what r we working on?
        List<PebblingNode> workList = new ArrayList<>();

        for (PebblingNode v : this.vertices) {
            deps.put(v, v.inDegree);
            if (deps.get(v) == 0) {
                workList.add(v);
            }
        }

        // do work
        while (!workList.isEmpty()) {
            // get the first node in the worklist
            PebblingNode v = workList.remove(0);
            // add this node's ID to the output
            output.add(v.id);
            // for loop
            for (PebblingNode w : v.neighbors) {
                deps.put(w, deps.get(w) - 1);
                if (deps.get(w) == 0) {
                    workList.add(w);
                }
            }
        }

        // make sure all the nodes are there
        for (PebblingNode p : this.vertices) {
            if (!output.contains(p.id)) {
                return null;
            }
        }

        return output;
    }
}