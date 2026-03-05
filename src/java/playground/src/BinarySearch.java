import java.util.Random;

public class BinarySearch {
    public static void main(String[] args) {
        System.out.println("----------------------------------");
        System.out.println("---------- BinarySearch ----------");

        int[] numbers = new int[10];

        //generate a mock data for numbers array
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = new Random().nextInt(15);
        }

        int numberToFind = 4;
        int index = binarySearch(numbers, numberToFind);

        for (int i : numbers) {
            System.out.print(i + " ");
        }

        if (index == -1) {
            System.out.println("\nThe number " + numberToFind + " was not found");
        } else {
            System.out.println("\nThe number " + numberToFind + " is at position " + index);
        }

        System.out.println("----------------------------------");
    }

    public static int binarySearch(int[] numbers, int numberToFind) {
        int lowPosition = 0;
        int highPosition = numbers.length - 1;

        while (lowPosition <= highPosition) {
            int midPosition = (lowPosition + highPosition) / 2;
            int midNumber = numbers[midPosition];

            if (numberToFind == midNumber) {
                return midPosition;
            }

            if (numberToFind < midNumber) {
                highPosition = midPosition - 1;
            } else {
                lowPosition = midPosition + 1;
            }
        }

        return -1;
    }
}
