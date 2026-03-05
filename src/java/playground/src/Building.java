/*
// Online Java Compiler
// Use this editor to write, compile and run your Java code online

/*
There are n buildings in a line. You are given an integer array heights of size n that represents the heights of the buildings in the line.

The ocean is to the right of the buildings. A building has an ocean view if the building can see the ocean without obstructions. Formally, a building has an ocean view if all the buildings to its right have a smaller height.

Return a list of indices (0-indexed) of buildings that have an ocean view, sorted in increasing order.

Test cases:

Input1.      4,2,3,1
Output1      0,2,3

Input2.      4,3,2,1
Output2.   0,1,2,3

Input3.      1,3,2,4
Output3.      3

*/

import java.util.ArrayList;
import java.util.List;

public class Building {
    public static void main(String[] args) {
        int[] input = new int[] { 4,2,3,1 };

        //input: 4 (0), 2 (1), 3 (2), 1 (3)
        //output: 0, 2, 3

        //result
        var response = getBuilding(input);
        System.out.println(response);
    }

    public static List<Integer> getBuilding(int[] input) {
        List<Integer> result = new ArrayList<>();


        for (int index = input.length - 1; index >= 0; index--) {
            int currentValue = input[index];

            if (index == (input.length - 1)) {
                result.add(index);
            } else {
                int nextIndex = index + 1;
                int nextValue = input[nextIndex];

                if (nextValue < currentValue) {
                    result.add(index);
                }
            }
        }

        return result;
    }
}
