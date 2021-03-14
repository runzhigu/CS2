package edu.caltech.cs2.lab08;

import org.junit.jupiter.api.*;

import edu.caltech.cs2.lab08.PebblingGraph.PebblingNode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PebblesTests {
    private static final double EDGE_PROBABILITY = 0.7;

    private static Map<Integer, List<Integer>> makeDAG(Random r, int n) {
        List<Integer> nums = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            nums.add(i);
        }
        Collections.shuffle(nums, r);
        Map<Integer, List<Integer>> adjacencyLists = new HashMap<>();
        for (int i = 0; i < n; i++) {
            List<Integer> adjacencyList = new ArrayList<>();
            for (int j = 0; j < i; j++) {
                if (r.nextDouble() < EDGE_PROBABILITY) {
                    adjacencyList.add(nums.get(j));
                }
            }
            adjacencyLists.put(nums.get(i), adjacencyList);
        }
        return adjacencyLists;
    }

    private static void checkValidTopologicalSort(Map<Integer, List<Integer>> dag, List<Integer> topoSort) {
        Map<Integer, Integer> topoSortIndices = new HashMap<>();
        for (int i = 0; i < topoSort.size(); i++) {
            topoSortIndices.put(topoSort.get(i), i);
        }
        Assertions.assertEquals(dag.keySet(), topoSortIndices.keySet(), "Topo sort does not contain correct vertices");

        for (int a : dag.keySet()) {
            for (int b : dag.get(a)) {
                Assertions.assertTrue(
                    topoSortIndices.get(a) < topoSortIndices.get(b),
                    "Vertex " + a + " does not come before vertex " + b + " in topo sort"
                );
            }
        }
    }


    @Tag("A")
    @Order(1)
    @DisplayName("Test toposort")
    @ParameterizedTest(name = "seed = {0}, num_vertices = {1}")
    @CsvSource({
            "1000,1", "1000,2", "1000,8", "1000,32", "1000, 128", "1000, 512", "1000, 700",
            "1001,1", "1001,2", "1001,8", "1001,32", "1001, 128", "1001, 512", "1001, 700",
            "1010,1", "1000,2", "1000,8", "1000,32", "1000, 128", "1000, 512", "1000, 700",
            "1011,1", "1011,2", "1011,8", "1011,32", "1011, 128", "1011, 512", "1011, 700",
            "1100,1", "1100,2", "1100,8", "1100,32", "1100, 128", "1100, 512", "1100, 700",
            "1101,1", "1101,2", "1101,8", "1101,32", "1101, 128", "1101, 512", "1101, 700",
            "1110,1", "1110,2", "1110,8", "1110,32", "1110, 128", "1110, 512", "1110, 700",
            "1111,1", "1111,2", "1111,8", "1111,32", "1111, 128", "1111, 512", "1111, 700",
            "1337,1", "1337,2", "1337,8", "1337,32", "1337, 128", "1337, 512", "1337, 700"
    })
    public void testTopoSort(int seed, int n) {
        Map<Integer, List<Integer>> dag = makeDAG(new Random(seed), n);
        PebblingGraph g = new PebblingGraph();
        Map<Integer, PebblingNode> vertices = new HashMap<>();
        for (int id : dag.keySet()) {
            vertices.put(id, g.addVertex(id));
        }
        for (int id : dag.keySet()) {
            PebblingNode v = vertices.get(id);
            for (int neighborID : dag.get(id)) {
                g.addEdge(v, vertices.get(neighborID));
            }
        }
        checkValidTopologicalSort(dag, g.toposort());
    }
}
