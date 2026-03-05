import java.util.Random;

public class MergeSort {
    public static void main(String[] args) {
        System.out.println("-------------------------------------");
        System.out.println("------------- MergeSort -------------");

        // Generate random numbers
        int[] numbers = new int[7];

        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = new Random().nextInt(30);
        }

        for (int i : numbers) {
            System.out.print(i + " ");
        }
        
        System.out.println("\n---------");

        // Sort the numbers using merge sort
        mergeSort(numbers);

        // Print the sorted numbers
        for (int i : numbers) {
            System.out.print(i + " ");
        }

        System.out.println("\n-------------------------------------");
    }

    private static void mergeSort(int[] inputArray) {
        int iputLength = inputArray.length;

        if (iputLength < 2) return;

        int midIndex = iputLength / 2;
        int[] leftHalf = new int[midIndex];
        int[] rightHalf = new int[iputLength - midIndex];

        for(int i = 0; i < midIndex; i++) {
            leftHalf[i] = inputArray[i];
        }

        for(int i = midIndex; i < iputLength; i++) {
            rightHalf[i - midIndex] = inputArray[i];
        }

        mergeSort(leftHalf);
        mergeSort(rightHalf);

        merge(inputArray, leftHalf, rightHalf);
    }

    private static void merge(int[] inputArray, int[] left, int[] right) {
        int leftSize = left.length;
        int rightSize = right.length;

        int l = 0, r = 0, k = 0;

        while (l < leftSize && r < rightSize) {
            if (left[l] <= right[r]) {
                inputArray[k] = left[l];
                l++;
            } else {
                inputArray[k] = right[r];
                r++;
            }
            k++;
        }

        while (l < leftSize) {
            inputArray[k] = left[l];
            l++;
            k++;
        }

        while (r < rightSize) {
            inputArray[k] = right[r];
            r++;
            k++;
        }
    }
}
