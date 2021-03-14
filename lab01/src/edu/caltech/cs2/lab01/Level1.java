package edu.caltech.cs2.lab01;

import java.util.ArrayList;
import java.util.List;

public class Level1 {
    /**
     * This function should test element-wise equality of
     * the provided two lists l1 and l2.
     * @param l1 the first list
     * @param l2 the second list
     * @return true iff l1 and l2 have exactly the same elements
     */
    public static boolean listsEqual(List<Integer> l1, List<Integer> l2) {

        if (l1.size() != l2.size()) {
            return false;
        }
        for (int i = 0; i < l1.size(); i++) {
            if (
                    !(l1.get(i).equals(l2.get(i)))
            ) {
                return false;
            }
        }

        return true;
    }

    /**
     * A very simple test of listsEqual.  This method
     * does not have a bug in it.
     */
    public static void main(String[] args) {
        int a = 5;
        int b = 1337;

        List<Integer> list1 = new ArrayList<>();
        list1.add(a);
        list1.add(b);

        List<Integer> list2 = List.of(5, 1337);

        System.out.println(listsEqual(list1, list2));
    }
}
