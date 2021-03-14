package edu.caltech.cs2.interfaces;

/**
 * This interface represents a Priority Queue - a data structure that is very similar to a queue but
 * stores values in ascending order based on their priority.
 * @param <E> Element type
 */
public interface IPriorityQueue<E> extends IQueue<IPriorityQueue.PQElement<E>> {
    public static class PQElement<E> {
        public final E data;
        public final double priority;

        public PQElement(E data, double priority) {
            this.data = data;
            this.priority = priority;
        }

        @Override
        public int hashCode() {
            return this.data.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof PQElement)) {
                return false;
            }
            return this.data.equals(((PQElement)o).data);
        }

        @Override
        public String toString() {
            return "(" + this.data + ", " + this.priority + ")";
        }
    }


    /**
     * Increase the priority of the element in the queue with data equal to key.data
     * @param key - the PQElement with data, and new priority for that data
     * @throws IllegalArgumentException if key.data is not an element in the queue
     */
    public void increaseKey(PQElement<E> key);

    /**
     * Decrease the priority of the element in the queue with data equal to key.data
     * @param key - the PQElement with data, and new priority for that data
     * @throws IllegalArgumentException if key.data is not an element in the queue
     */
    public void decreaseKey(PQElement<E> key);

}
