/*
    Share your screen, open an online Java compiler, and solve the problem. Task is to assign the values 1 to 26 to all
    the alphabets in order, like a=1, b=2, c=3, …………. z= 26,
     numeric are equal to their number value, and then Input an alphanumeric string and calculate the sum of that string?
    Example:
    Input: hello123
    Output: 58
    Explanation: (Where h=8, e=5, l=12, l=12, o=15, 1=1, 2=2, 3=3)
*/

public class AlphabetValueSum {
    public static void main(String[] args) {
        String input = "hello123";
        var total = wordLettersSum(input);
        System.out.println("input = " + input + ", total = " + total);
    }

    static int wordLettersSum(String input) {
        char firstLetter = 'A';

        int total = input.toUpperCase().chars()
                .map(c -> {
                    if (Character.isDigit(c)) {
                        return Character.getNumericValue(c);
                    }
                    return c - firstLetter + 1;
                })
                .sum();

        return total;
    }
}