import java.util.*;
import java.util.stream.IntStream;

public class TwoSum {
    public static void main(String[] args) {
        List<Integer> values = Arrays.asList(255,7,11,55,15,17,6);
        var result = twoSumMap(
            values.stream().mapToInt(Integer::intValue).toArray(),
            13
        );

        result.forEach(i -> {
            System.out.println("index: " + i + ", value: " + values.get(i));
        });
    }

    private static List<Integer> twoSumMap(int[] nums, int target) {
        Map<Integer, Integer> numMap = new HashMap<>();
        
        for(int i = 0; i < nums.length; i++) {
            //if the target is equals the nums[i], return the list with the position (i)
            if (nums[i] == target) {
                return Arrays.asList(i);
            }

            //if not, calculate the complement
            //complement = how much do we need to reach the target from the current number?
            int complement = target - nums[i];

            if (numMap.containsKey(complement)) {
                return Arrays.asList(numMap.get(complement), i);
            }

            numMap.put(nums[i], i);
        }

        return new ArrayList<>();
    }
}