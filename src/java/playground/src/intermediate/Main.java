package intermediate;

public class Main {
    public static void main(String[] args) {
        int[] arr = new int[] { 1, 2, 4, 5, 6 };

        int midIndex = arr.length / 2;

        char initial = 'A';

        System.out.println((int)initial);
        System.out.println("A = " + initial);
        System.out.println("A numeric = " + Character.getNumericValue(initial));

        System.out.println(midIndex);
    }
}
