import java.util.Arrays;

public class ReverseString {
    public static void main(String args[]) {
        String wordStr = "o11eh";
        char[] word = wordStr.toCharArray();

        reverseString(word);

        System.out.println("Original: " + wordStr);
        System.out.println("Reverted: " + String.valueOf(word));
    }

    public static void reverseString(char[] s) {
        int left = 0;
        int right = s.length - 1;

        while(left < right) {
            var rightValue = s[right];
            s[right] = s[left];
            s[left] = rightValue;
            left++;
            right--;
        }
    }
}