package edu.caltech.cs2.lab01;

public class GradeHistogram {
    /**
     * Counts the number of occurrences of each grade.
     * grades[i] is the number of times grade i was received,
     * for i between 0 (inclusive) and 10 (exclusive).
     */
    private int[] grades = new int[10];

    public void addGrade(int grade) {
        if (grade < 0 || grade >= 10) {
            throw new IllegalArgumentException("Grades must be between 0 and 10");
        }
        this.grades[grade]+=1;
    }

    public int getFrequency(int grade) {
        if (grade < 0 || grade >= 10) {
            throw new IllegalArgumentException("Grades must be between 0 and 10");
        }
        return this.grades[grade];
    }


    @Override
    public String toString() {
        // Find the maximum height of the bars
        int maxIdx = 0;
        for (int i = 0; i < this.grades.length; i++) {
            if (this.grades[i] > this.grades[maxIdx]) {
                maxIdx = i;
            }
        }

        String result = "";

        // Loop through every height
        for (int i = this.grades[maxIdx]; i > 0; i--) {
            // Determine if this spot should be a * or a space
            for (int j = 0; j < this.grades.length; j++) {
                if (this.grades[j] >= i) {
                    result += "*";
                } else {
                    result += " ";
                }
            }
            result += "\n";
        }
        System.out.println(result);
        return result;
    }
}