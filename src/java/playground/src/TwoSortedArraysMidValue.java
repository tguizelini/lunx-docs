import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class TwoSortedArraysMidValue {
    public static void main(String[] args) {
        /*
            You are given two sorted integer arrays nums1 and nums2 of sizes m and n respectively.
            Return the median of the two sorted arrays.Input:
            nums1 = [1, 3], nums2 = [2] Output: 2.0
            Input: nums1 = [1, 2], nums2 = [3, 4] Output: 2.5
            [1,2,3,4]
            2+3/2 = 2.5
        */

        List<Integer> arr1 = List.of(1,3);
        List<Integer> arr2 = List.of(2);

        var merged = Stream.concat(arr1.stream(), arr2.stream())
                .sorted()
                .toList();

        int pos1 = (merged.size() - 1) / 2;
        var midValue = merged.get(pos1);

        if (merged.size() % 2 == 0) {
            int pos2 = merged.size() / 2;
            int midValuePair = merged.get(pos2);
            System.out.println((midValue + midValuePair) / 2.0);
        } else {
            System.out.println(midValue);
        }
    }
}
