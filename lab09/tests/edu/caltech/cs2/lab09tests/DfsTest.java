package edu.caltech.cs2.lab09tests;

import edu.caltech.cs2.helpers.Inspection;
import edu.caltech.cs2.lab09.Maze;
import edu.caltech.cs2.lab09.Point;
import edu.caltech.cs2.libraries.StdDraw;
import edu.caltech.cs2.helpers.Reflection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Paths;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.concurrent.ThreadLocalRandom;

public class DfsTest {
    @Tag("C")
    @Test
    @DisplayName("getChildren Test 1")
    public void testGetChildren1() throws IOException {
        int x = ThreadLocalRandom.current().nextInt(3, 28);
        int y = ThreadLocalRandom.current().nextInt(3, 28);
        String localDir = System.getProperty("user.dir");
        String mazePath = localDir + "/tests/edu/caltech/cs2/lab09tests/Data/Mazes/Maze1.txt";
        Maze testMaze = new Maze(30, mazePath);
        Point[] children;
        Point test1 = new Point(x, y);
        testMaze.north[x][y] = false;
        testMaze.east[x][y] = false;
        testMaze.south[x][y] = false;
        testMaze.west[x][y] = false;
        children = testMaze.getChildren(test1);
        assertEquals(4, children.length, "Expected 4 children. Found " + children.length);

        Point c1 = new Point(x + 1, y);
        Point c2 = new Point(x, y + 1);
        Point c3 = new Point(x - 1, y);
        Point c4 = new Point(x, y - 1);
        boolean c1pass = false, c2pass = false, c3pass = false, c4pass = false;
        boolean parents = true;
        for (Point curr : children) {
            if (c1.equals(curr)) c1pass = true;
            else if (c2.equals(curr)) c2pass = true;
            else if (c3.equals(curr)) c3pass = true;
            else if (c4.equals(curr)) c4pass = true;

            if (curr.parent == null || !curr.parent.equals(test1)) parents = false;
        }
        boolean allChildrenPresent = (c1pass && c2pass && c3pass && c4pass);
        assertTrue(allChildrenPresent, "Not all expected children present.");
        assertTrue(parents, "Not all children's parent set to parent node.");
    }

    @Tag("C")
    @Test
    @DisplayName("getChildren Test 2")
    public void testGetChildren2() throws IOException {
        // Generate a random point to avoid hard-coding to pass tests
        int x = ThreadLocalRandom.current().nextInt(3, 28);
        int y = ThreadLocalRandom.current().nextInt(3, 28);
        String localDir = System.getProperty("user.dir");
        String mazePath = localDir + "/tests/edu/caltech/cs2/lab09tests/Data/Mazes/Maze1.txt";
        Maze testMaze = new Maze(30, mazePath);
        Point[] children;
        Point test1 = new Point(x, y);
        testMaze.north[x][y] = true;
        testMaze.east[x][y] = false;
        testMaze.south[x][y] = true;
        testMaze.west[x][y] = false;
        children = testMaze.getChildren(test1);
        assertEquals(2, children.length, "Expected 2 children. Found " + children.length);

        Point c1 = new Point(x + 1, y);
        Point c2 = new Point(x - 1, y);
        boolean c1pass = false, c2pass = false;
        boolean parents = true;
        for (Point curr : children) {
            if (c1.equals(curr)) c1pass = true;
            else if (c2.equals(curr)) c2pass = true;

            if (curr.parent == null || !curr.parent.equals(test1)) parents = false;
        }
        boolean allChildrenPresent = (c1pass && c2pass);
        assertTrue(allChildrenPresent, "Not all expected children present.");
        assertTrue(parents, "Not all children's parent set to parent node.");
    }

    @Tag("C")
    @Test
    @DisplayName("getChildren Test 3")
    public void testGetChildren3() throws IOException {
        // Generate a random point to avoid hard-coding to pass tests
        int x = ThreadLocalRandom.current().nextInt(3, 28);
        int y = ThreadLocalRandom.current().nextInt(3, 28);
        String localDir = System.getProperty("user.dir");
        String mazePath = localDir + "/tests/edu/caltech/cs2/lab09tests/Data/Mazes/Maze1.txt";
        Maze testMaze = new Maze(30, mazePath);
        Point[] children;
        Point test1 = new Point(x, y);
        testMaze.north[x][y] = false;
        testMaze.east[x][y] = true;
        testMaze.south[x][y] = false;
        testMaze.west[x][y] = false;
        children = testMaze.getChildren(test1);
        assertEquals(3, children.length, "Expected 3 children. Found " + children.length);

        Point c1 = new Point(x, y + 1);
        Point c2 = new Point(x, y - 1);
        Point c3 = new Point(x - 1, y);
        boolean c1pass = false, c2pass = false, c3pass = false;
        boolean parents = true;
        for (Point curr : children) {
            if (c1.equals(curr)) c1pass = true;
            else if (c2.equals(curr)) c2pass = true;
            else if (c3.equals(curr)) c3pass = true;

            if (curr.parent == null || !curr.parent.equals(test1)) parents = false;
        }
        boolean allChildrenPresent = (c1pass && c2pass && c3pass);
        assertTrue(allChildrenPresent, "Not all expected children present.");
        assertTrue(parents, "Not all children's parent set to parent node.");
    }

    @Tag("C")
    @Test
    @DisplayName("getChildren Test 4")
    public void testGetChildren4() throws IOException {
        // Generate a random point to avoid hard-coding to pass tests
        int x = ThreadLocalRandom.current().nextInt(3, 28);
        int y = ThreadLocalRandom.current().nextInt(3, 28);
        String localDir = System.getProperty("user.dir");
        String mazePath = localDir + "/tests/edu/caltech/cs2/lab09tests/Data/Mazes/Maze1.txt";
        Maze testMaze = new Maze(30, mazePath);
        Point[] children;
        Point test1 = new Point(x, y);
        testMaze.north[x][y] = false;
        testMaze.east[x][y] = false;
        testMaze.south[x][y] = true;
        testMaze.west[x][y] = true;
        children = testMaze.getChildren(test1);
        assertEquals(2, children.length, "Expected 2 children. Found " + children.length);

        Point c1 = new Point(x, y + 1);
        Point c2 = new Point(x + 1, y);
        boolean c1pass = false, c2pass = false;
        boolean parents = true;
        for (Point curr : children) {
            if (c1.equals(curr)) c1pass = true;
            else if (c2.equals(curr)) c2pass = true;

            if (curr.parent == null || !curr.parent.equals(test1)) parents = false;
        }
        boolean allChildrenPresent = (c1pass && c2pass);
        assertTrue(allChildrenPresent, "Not all expected children present.");
        assertTrue(parents, "Not all children's parent set to parent node.");
    }

    @Tag("C")
    @Test
    @DisplayName("getChildren Test 5")
    public void testGetChildren5() throws IOException {
        // Generate a random point to avoid hard-coding to pass tests
        int x = ThreadLocalRandom.current().nextInt(3, 28);
        int y = ThreadLocalRandom.current().nextInt(3, 28);
        String localDir = System.getProperty("user.dir");
        String mazePath = localDir + "/tests/edu/caltech/cs2/lab09tests/Data/Mazes/Maze1.txt";
        Maze testMaze = new Maze(30, mazePath);
        Point[] children;
        Point test1 = new Point(x, y);
        testMaze.north[x][y] = true;
        testMaze.east[x][y] = true;
        testMaze.south[x][y] = false;
        testMaze.west[x][y] = false;
        children = testMaze.getChildren(test1);
        assertEquals(2, children.length, "Expected 2 children. Found " + children.length);

        Point c1 = new Point(x, y - 1);
        Point c2 = new Point(x - 1, y);
        boolean c1pass = false, c2pass = false;
        boolean parents = true;
        for (Point curr : children) {
            if (c1.equals(curr)) c1pass = true;
            else if (c2.equals(curr)) c2pass = true;

            if (curr.parent == null || !curr.parent.equals(test1)) parents = false;
        }
        boolean allChildrenPresent = (c1pass && c2pass);
        assertTrue(allChildrenPresent, "Not all expected children present.");
        assertTrue(parents, "Not all children's parent set to parent node.");
    }


    @Tag("B")
    @Test
    @DisplayName("DFS Recursive Check")
    public void RDfsCheck() {
        String localDir = System.getProperty("user.dir");
        String srcPath = localDir + "/src/edu/caltech/cs2/lab09/Maze.java";
        Method solveMethod = Reflection.getMethod(Maze.class, "solveDFSRecursive", Point.class);
        Inspection.assertRecursive(srcPath, solveMethod);
    }

    @Tag("B")
    @Test
    @DisplayName("DFS Recursive Solver Correctness Test 1")
    public void RDfsCorrectnessTest1 () throws IOException {
        String localDir = System.getProperty("user.dir");
        String outPath = localDir + "/tests/edu/caltech/cs2/lab09tests/Data/TestOut.txt";
        String mazePath = localDir + "/tests/edu/caltech/cs2/lab09tests/Data/Mazes/Maze1.txt";
        String acceptablePath = localDir +  "/tests/edu/caltech/cs2/lab09tests/Data/AcceptableOut1";
        System.setOut(new PrintStream(new File(outPath)));
        Maze maze = new Maze(30, mazePath);
        StdDraw.enableDoubleBuffering();
        maze.draw();

        maze.done = false;
        maze.solveDFSRecursiveStart();

        boolean matchFound = false;
        for (int i = 1; i <= 24; i++) {
            String tryPath = acceptablePath + "/out" + i + ".txt";
            String out = "";
            String tryString = "";
            out = new String(Files.readAllBytes(Paths.get(outPath)));
            tryString = new String(Files.readAllBytes(Paths.get(tryPath)));
            out = out.replace("\r\n", "\n").strip();
            tryString = tryString.replace("\r\n", "\n").strip();
            if (out.equals(tryString)) {
                matchFound = true;
                break;
            }
        }
        StdDraw.clear();
        assertTrue(matchFound, "Output log is not acceptable. Solver is either not correct or not DFS.");
    }

    @Tag("B")
    @Test
    @DisplayName("DFS Recursive Solver Correctness Test 2")
    public void RDfsCorrectnessTest2 () throws IOException {
        String localDir = System.getProperty("user.dir");
        String outPath = localDir + "/tests/edu/caltech/cs2/lab09tests/Data/TestOut.txt";
        String mazePath = localDir + "/tests/edu/caltech/cs2/lab09tests/Data/Mazes/Maze2.txt";
        String acceptablePath = localDir +  "/tests/edu/caltech/cs2/lab09tests/Data/AcceptableOut2";
        System.setOut(new PrintStream(new File(outPath)));
        Maze maze = new Maze(30, mazePath);
        StdDraw.enableDoubleBuffering();
        maze.draw();

        maze.done = false;
        maze.solveDFSRecursiveStart();


        boolean matchFound = false;
        for (int i = 1; i <= 24; i++) {
            String tryPath = acceptablePath + "/out" + i + ".txt";
            String out = "";
            String tryString = "";
            out = new String(Files.readAllBytes(Paths.get(outPath)));
            tryString = new String(Files.readAllBytes(Paths.get(tryPath)));
            out = out.replace("\r\n", "\n").strip();
            tryString = tryString.replace("\r\n", "\n").strip();
            if (out.equals(tryString)) {
                matchFound = true;
                break;
            }
        }
        StdDraw.clear();
        assertTrue(matchFound, "Output log is not acceptable. Solver is either not correct or not DFS.");
    }

    @Tag("B")
    @Test
    @DisplayName("DFS Recursive Solver Correctness Test 3")
    public void RDfsCorrectnessTest3 () throws IOException {
        String localDir = System.getProperty("user.dir");
        String outPath = localDir + "/tests/edu/caltech/cs2/lab09tests/Data/TestOut.txt";
        String mazePath = localDir + "/tests/edu/caltech/cs2/lab09tests/Data/Mazes/Maze3.txt";
        String acceptablePath = localDir +  "/tests/edu/caltech/cs2/lab09tests/Data/AcceptableOut3";
        System.setOut(new PrintStream(new File(outPath)));
        Maze maze = new Maze(30, mazePath);
        StdDraw.enableDoubleBuffering();
        maze.draw();

        maze.done = false;
        maze.solveDFSRecursiveStart();


        boolean matchFound = false;
        for (int i = 1; i <= 24; i++) {
            String tryPath = acceptablePath + "/out" + i + ".txt";
            String out = "";
            String tryString = "";
            out = new String(Files.readAllBytes(Paths.get(outPath)));
            tryString = new String(Files.readAllBytes(Paths.get(tryPath)));
            out = out.replace("\r\n", "\n").strip();
            tryString = tryString.replace("\r\n", "\n").strip();
            if (out.equals(tryString)) {
                matchFound = true;
                break;
            }
        }
        StdDraw.clear();
        assertTrue(matchFound, "Output log is not acceptable. Solver is either not correct or not DFS.");
    }

    @Tag("B")
    @Test
    @DisplayName("DFS Recursive Solver Correctness Test 4")
    public void RDfsCorrectnessTest4 () throws IOException {
        String localDir = System.getProperty("user.dir");
        String outPath = localDir + "/tests/edu/caltech/cs2/lab09tests/Data/TestOut.txt";
        String mazePath = localDir + "/tests/edu/caltech/cs2/lab09tests/Data/Mazes/Maze4.txt";
        String acceptablePath = localDir +  "/tests/edu/caltech/cs2/lab09tests/Data/AcceptableOut4";
        System.setOut(new PrintStream(new File(outPath)));
        Maze maze = new Maze(30, mazePath);
        StdDraw.enableDoubleBuffering();
        maze.draw();

        maze.done = false;
        maze.solveDFSRecursiveStart();


        boolean matchFound = false;
        for (int i = 1; i <= 24; i++) {
            String tryPath = acceptablePath + "/out" + i + ".txt";
            String out = "";
            String tryString = "";
            out = new String(Files.readAllBytes(Paths.get(outPath)));
            tryString = new String(Files.readAllBytes(Paths.get(tryPath)));
            out = out.replace("\r\n", "\n").strip();
            tryString = tryString.replace("\r\n", "\n").strip();
            if (out.equals(tryString)) {
                matchFound = true;
                break;
            }
        }
        StdDraw.clear();
        assertTrue(matchFound, "Output log is not acceptable. Solver is either not correct or not DFS.");
    }

    @Tag("B")
    @Test
    @DisplayName("DFS Recursive Solver Correctness Test 5")
    public void RDfsCorrectnessTest5 () throws IOException {
        String localDir = System.getProperty("user.dir");
        String outPath = localDir + "/tests/edu/caltech/cs2/lab09tests/Data/TestOut.txt";
        String mazePath = localDir + "/tests/edu/caltech/cs2/lab09tests/Data/Mazes/Maze5.txt";
        String acceptablePath = localDir +  "/tests/edu/caltech/cs2/lab09tests/Data/AcceptableOut5";
        System.setOut(new PrintStream(new File(outPath)));
        Maze maze = new Maze(30, mazePath);
        StdDraw.enableDoubleBuffering();
        maze.draw();

        maze.done = false;
        maze.solveDFSRecursiveStart();


        boolean matchFound = false;
        for (int i = 1; i <= 24; i++) {
            String tryPath = acceptablePath + "/out" + i + ".txt";
            String out = "";
            String tryString = "";
            out = new String(Files.readAllBytes(Paths.get(outPath)));
            tryString = new String(Files.readAllBytes(Paths.get(tryPath)));
            out = out.replace("\r\n", "\n").strip();
            tryString = tryString.replace("\r\n", "\n").strip();
            if (out.equals(tryString)) {
                matchFound = true;
                break;
            }
        }
        StdDraw.clear();
        assertTrue(matchFound, "Output log is not acceptable. Solver is either not correct or not DFS.");
    }

    @Tag("A")
    @Test
    @DisplayName("DFS Iterative Check")
    public void IDfsCheck() {
        String localDir = System.getProperty("user.dir");
        String srcPath = localDir + "/src/edu/caltech/cs2/lab09/Maze.java";
        Method solveMethod = Reflection.getMethod(Maze.class, "solveDFSIterative");
        Inspection.assertIterative(srcPath, solveMethod);
    }

    @Tag("A")
    @Test
    @DisplayName("DFS Iterative Solver Correctness Test 1")
    public void IDfsCorrectnessTest1 () throws IOException {
        String localDir = System.getProperty("user.dir");
        String outPath = localDir + "/tests/edu/caltech/cs2/lab09tests/Data/TestOut.txt";
        String mazePath = localDir + "/tests/edu/caltech/cs2/lab09tests/Data/Mazes/Maze5.txt";
        String acceptablePath = localDir +  "/tests/edu/caltech/cs2/lab09tests/Data/AcceptableOut5";
        System.setOut(new PrintStream(new File(outPath)));
        Maze maze = new Maze(30, mazePath);
        StdDraw.enableDoubleBuffering();
        maze.draw();

        maze.done = false;
        maze.solveDFSIterative();

        boolean matchFound = false;
        for (int i = 1; i <= 24; i++) {
            String tryPath = acceptablePath + "/out" + i + ".txt";
            String out = "";
            String tryString = "";
            out = new String(Files.readAllBytes(Paths.get(outPath)));
            tryString = new String(Files.readAllBytes(Paths.get(tryPath)));
            out = out.replace("\r\n", "\n").strip();
            tryString = tryString.replace("\r\n", "\n").strip();
            if (out.equals(tryString)) {
                matchFound = true;
                break;
            }
        }
        StdDraw.clear();
        assertTrue(matchFound, "Output log is not acceptable. Solver is either not correct or not DFS.");
    }

    @Tag("A")
    @Test
    @DisplayName("DFS Iterative Solver Correctness Test 2")
    public void IDfsCorrectnessTest2 () throws IOException {
        String localDir = System.getProperty("user.dir");
        String outPath = localDir + "/tests/edu/caltech/cs2/lab09tests/Data/TestOut.txt";
        String mazePath = localDir + "/tests/edu/caltech/cs2/lab09tests/Data/Mazes/Maze6.txt";
        String acceptablePath = localDir +  "/tests/edu/caltech/cs2/lab09tests/Data/AcceptableOut6";
        System.setOut(new PrintStream(new File(outPath)));
        Maze maze = new Maze(30, mazePath);
        StdDraw.enableDoubleBuffering();
        maze.draw();

        maze.done = false;
        maze.solveDFSIterative();

        boolean matchFound = false;
        for (int i = 1; i <= 24; i++) {
            String tryPath = acceptablePath + "/out" + i + ".txt";
            String out = "";
            String tryString = "";
            out = new String(Files.readAllBytes(Paths.get(outPath)));
            tryString = new String(Files.readAllBytes(Paths.get(tryPath)));
            out = out.replace("\r\n", "\n").strip();
            tryString = tryString.replace("\r\n", "\n").strip();
            if (out.equals(tryString)) {
                matchFound = true;
                break;
            }
        }
        StdDraw.clear();
        assertTrue(matchFound, "Output log is not acceptable. Solver is either not correct or not DFS.");
    }

    @Tag("A")
    @Test
    @DisplayName("DFS Iterative Solver Correctness Test 3")
    public void IDfsCorrectnessTest3 () throws IOException {
        String localDir = System.getProperty("user.dir");
        String outPath = localDir + "/tests/edu/caltech/cs2/lab09tests/Data/TestOut.txt";
        String mazePath = localDir + "/tests/edu/caltech/cs2/lab09tests/Data/Mazes/Maze7.txt";
        String acceptablePath = localDir +  "/tests/edu/caltech/cs2/lab09tests/Data/AcceptableOut7";
        System.setOut(new PrintStream(new File(outPath)));
        Maze maze = new Maze(30, mazePath);
        StdDraw.enableDoubleBuffering();
        maze.draw();

        maze.done = false;
        maze.solveDFSIterative();

        boolean matchFound = false;
        for (int i = 1; i <= 24; i++) {
            String tryPath = acceptablePath + "/out" + i + ".txt";
            String out = "";
            String tryString = "";
            out = new String(Files.readAllBytes(Paths.get(outPath)));
            tryString = new String(Files.readAllBytes(Paths.get(tryPath)));
            out = out.replace("\r\n", "\n").strip();
            tryString = tryString.replace("\r\n", "\n").strip();
            if (out.equals(tryString)) {
                matchFound = true;
                break;
            }
        }
        StdDraw.clear();
        assertTrue(matchFound, "Output log is not acceptable. Solver is either not correct or not DFS.");
    }

    @Tag("A")
    @Test
    @DisplayName("DFS Iterative Solver Correctness Test 4")
    public void IDfsCorrectnessTest4 () throws IOException {
        String localDir = System.getProperty("user.dir");
        String outPath = localDir + "/tests/edu/caltech/cs2/lab09tests/Data/TestOut.txt";
        String mazePath = localDir + "/tests/edu/caltech/cs2/lab09tests/Data/Mazes/Maze8.txt";
        String acceptablePath = localDir +  "/tests/edu/caltech/cs2/lab09tests/Data/AcceptableOut8";
        System.setOut(new PrintStream(new File(outPath)));
        Maze maze = new Maze(30, mazePath);
        StdDraw.enableDoubleBuffering();
        maze.draw();

        maze.done = false;
        maze.solveDFSIterative();

        boolean matchFound = false;
        for (int i = 1; i <= 24; i++) {
            String tryPath = acceptablePath + "/out" + i + ".txt";
            String out = "";
            String tryString = "";
            out = new String(Files.readAllBytes(Paths.get(outPath)));
            tryString = new String(Files.readAllBytes(Paths.get(tryPath)));
            out = out.replace("\r\n", "\n").strip();
            tryString = tryString.replace("\r\n", "\n").strip();
            if (out.equals(tryString)) {
                matchFound = true;
                break;
            }
        }
        StdDraw.clear();
        assertTrue(matchFound, "Output log is not acceptable. Solver is either not correct or not DFS.");
    }

    @Tag("A")
    @Test
    @DisplayName("DFS Iterative Solver Correctness Test 5")
    public void IDfsCorrectnessTest5 () throws IOException {
        String localDir = System.getProperty("user.dir");
        String outPath = localDir + "/tests/edu/caltech/cs2/lab09tests/Data/TestOut.txt";
        String mazePath = localDir + "/tests/edu/caltech/cs2/lab09tests/Data/Mazes/Maze9.txt";
        String acceptablePath = localDir +  "/tests/edu/caltech/cs2/lab09tests/Data/AcceptableOut9";
        System.setOut(new PrintStream(new File(outPath)));
        Maze maze = new Maze(30, mazePath);
        StdDraw.enableDoubleBuffering();
        maze.draw();

        maze.done = false;
        maze.solveDFSIterative();

        boolean matchFound = false;
        for (int i = 1; i <= 24; i++) {
            String tryPath = acceptablePath + "/out" + i + ".txt";
            String out = "";
            String tryString = "";
            out = new String(Files.readAllBytes(Paths.get(outPath)));
            tryString = new String(Files.readAllBytes(Paths.get(tryPath)));
            out = out.replace("\r\n", "\n").strip();
            tryString = tryString.replace("\r\n", "\n").strip();
            if (out.equals(tryString)) {
                matchFound = true;
                break;
            }
        }
        StdDraw.clear();
        assertTrue(matchFound, "Output log is not acceptable. Solver is either not correct or not DFS.");
    }
}