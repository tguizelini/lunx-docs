import java.io.*;
import java.util.*;
import java.util.stream.*;

class Result {

    /*
     * Complete the 'countStudents' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts INTEGER_ARRAY height as parameter.
     */
    public static int countStudents(List<Integer> height) {
        var sorted = height.stream().sorted().toList(); // Java 16+ toList()

        var count = 0;
        for (var i = 0; i < height.size(); i++) {
            if (!height.get(i).equals(sorted.get(i))) {
                count++;
            }
        }

        return count;
    }

}

public class Solution {
    public static void main(String[] args) throws IOException {
        try (var bufferedReader = new BufferedReader(new InputStreamReader(System.in));
             var bufferedWriter = new BufferedWriter(new FileWriter(
                     Optional.ofNullable(System.getenv("OUTPUT_PATH")).orElse("output.txt")))) {

            var heightCount = Integer.parseInt(bufferedReader.readLine().trim());

            var height = IntStream.range(0, heightCount)
                    .mapToObj(i -> {
                        try {
                            return bufferedReader.readLine().strip();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    })
                    .map(Integer::parseInt)
                    .toList(); // Java 16+ toList()

            var result = Result.countStudents(height);

            bufferedWriter.write(String.valueOf(result));
            bufferedWriter.newLine();
        }
    }
}
