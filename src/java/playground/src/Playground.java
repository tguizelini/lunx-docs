import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;

public class Playground {
    public static void main(String[] args) {
        System.out.println("------------------------");
        System.out.println("Playground");
        System.out.println("------------------------");


        String word = "Tiago";
        var chars = word.toCharArray();

        var left = 0;
        var right = chars.length - 1;

        System.out.println("chars[value] -> " + chars[chars.length - 1]);
        System.out.println("word -> " + word);
        System.out.println("chars.length -> " + chars.length);

        while(left < right) {
            var rightValue = chars[right];
            chars[right] = chars[left];
            chars[left] = rightValue;
            right--;
            left++;
        }

        System.out.println("Reverted -> " + String.valueOf(chars));
    }
}
