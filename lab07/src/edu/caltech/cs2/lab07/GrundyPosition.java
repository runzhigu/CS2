package edu.caltech.cs2.lab07;

import java.util.List;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map;
import java.util.HashMap;

public class GrundyPosition {
    /* 
     * Stores a mapping from the height of a pile to how many of those piles exist.
     * Does not include piles of size less than three.
     */
    private SortedMap<Integer, Integer> heapCounts;
    // boolean says whether or not it is p position; if it isn't p position, it must be n position
    private Map<GrundyPosition, Boolean> memo = new HashMap<>();

    /**
     * Initializes a GrundyPosition with a single heap of height heapHeight.
     **/
    public GrundyPosition(int heapHeight) {
        this.heapCounts = new TreeMap<>();
        if (heapHeight > 2) {
            this.heapCounts.put(heapHeight, 1);
        }
    }

    /**
     * Initializes a GrundyPosition with a map of heapCounts already supplied (i.e. intermediate steps).
     **/
    public GrundyPosition(SortedMap<Integer, Integer> heaps) {
        this.heapCounts = heaps;
    }


    /**
     * Returns a list of legal GrundyPositions that a single move of Grundy's Game
     * can get to.
     **/
    public List<GrundyPosition> getMoves() {

        // initialize list to contain all next moves, to return
        List<GrundyPosition> nextMoves = new ArrayList<>();

        // if terminal position, definitely no more moves
        if (this.isTerminalPosition()) {
            return nextMoves;
        }

        // iterate thru each pile size
        for (int prevSize : this.heapCounts.keySet()) {
            // iterate thru each possible split of pile; strictly less than to avoid splitting heaps equally
            for (int newSize = 1; newSize < (prevSize + 1) / 2; newSize++) { // might have issues with integer division

                // get current heap counts via deep copy
                SortedMap<Integer, Integer> heaps = new TreeMap<>();
                for (Integer key : this.heapCounts.keySet()) {
                    heaps.put(key, this.heapCounts.get(key));
                }

                if (heaps.containsKey(prevSize)) {
                    // if the number of heaps w/ size prevSize is at least one
                    if (heaps.get(prevSize) > 1) {
                        // subtract one heap of the heap count
                        heaps.put(prevSize, heaps.get(prevSize) - 1);
                    }
                    // else remove that key/value pair
                    else {
                        heaps.remove(prevSize);
                    }
                }

                // add the heaps representing the move
                // add the first heap, make sure heap size is at least 2
                if (newSize > 2) {
                    // make sure heap pile is already there
                    if (!heaps.containsKey(newSize)) {
                        heaps.put(newSize, 0);
                    }
                    heaps.put(newSize, heaps.get(newSize) + 1);
                }
                // add the second heap, make sure heap size is at least 2
                if (prevSize - newSize > 2) {
                    if (!heaps.containsKey(prevSize - newSize)) {
                        heaps.put(prevSize - newSize, 0);
                    }
                    heaps.put(prevSize - newSize, heaps.get(prevSize - newSize) + 1);
                }

                nextMoves.add(new GrundyPosition(heaps));
            }
        }

        return nextMoves;
    }

    public boolean isTerminalPosition() {
        return this.heapCounts.size() == 0;
    }

    public boolean isPPosition() {
        return isPPosition(this);
    }

    private boolean isPPosition(GrundyPosition currentPosition) {
        // base case
        // terminal position is a losing position, so previous person won
        if (currentPosition.isTerminalPosition()) {
            memo.put(currentPosition, true);
            return true;
        }

        else if (memo.containsKey(currentPosition)) {
            // value is whether current position is P position
            return memo.get(currentPosition);
        }

        // recursive case
        // iterate thru each next position
        for (GrundyPosition nextPosition : currentPosition.getMoves()) {
            // if p position, all next moves must be N-positions
            boolean nextIsN = isNPosition(nextPosition);
            // memo value is whether or not P position
            memo.put(nextPosition, !nextIsN);
            // even one position is not N position, then this is not a p-position
            // i.e. next person is not doomed
            if (!nextIsN) {
                return false;
            }
        }
        // if got here, all next positions are N, so current position is P
        memo.put(currentPosition, true);
        return true;
    }

    public boolean isNPosition()  {
        return isNPosition(this);
    }

    private boolean isNPosition(GrundyPosition currentPosition) {
        // base case
        // terminal position is a losing position, so next person loses
        if (currentPosition.isTerminalPosition()) {
            // memo value is whether or not P position, so return true
            memo.put(currentPosition, true);
            return false;
        }
        else if (memo.containsKey(currentPosition)){
            return !memo.get(currentPosition);
        }

        // recursive case
        // iterate thru each next position
        for (GrundyPosition nextPosition : currentPosition.getMoves()) {
            // if N position, at least one next position must be a P position
            boolean nextIsP = isPPosition(nextPosition);
            // memo value is whether or not P position
            memo.put(nextPosition, nextIsP);
            // if there is a single next position that is P position
            // then next player should be smart enough to take it
            if (nextIsP) {
                return true;
            }
        }
        // if got here, none of the next positions are P, so nothing we can do to win atm
        // meaning that the current position is P, and put true value into dict
        memo.put(currentPosition, true);
        return false;

    }
    
    /** 
     * Ignore everything below this point.
     **/

    @Override
    public int hashCode() {
       return this.heapCounts.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GrundyPosition)) {
            return false;
        }
        return this.heapCounts.equals(((GrundyPosition) o).heapCounts);
    }

    @Override
    public String toString() {
        return this.heapCounts.toString();
    }
}