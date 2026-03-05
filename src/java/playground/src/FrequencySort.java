/*
    Given a string s, sort it in decreasing order based on the frequency of the characters. The frequency of a character is the number of times it appears in the string.

    Return the sorted string. If there are multiple answers, return any of them.

    Example 1:

    Input: s = "tree"
    Output: "eert"
    Explanation: 'e' appears twice while 'r' and 't' both appear once.
    So 'e' must appear before both 'r' and 't'. Therefore "eetr" is also a valid answer.
    Example 2:

    Input: s = "cccaaa"
    Output: "aaaccc"
    Explanation: Both 'c' and 'a' appear three times, so both "cccaaa" and "aaaccc" are valid answers.
    Note that "cacaca" is incorrect, as the same characters must be together.
    Example 3:

    Input: s = "Aabb"
    Output: "bbAa"
    Explanation: "bbaA" is also a valid answer, but "Aabb" is incorrect.
    Note that 'A' and 'a' are treated as two different characters.

    Constraints:

    1 <= s.length <= 5 * 105
    s consists of uppercase and lowercase English letters and digits.
 */

import java.util.*;

public class FrequencySort {
    public static void main(String[] args) {
        var result = frequencySort("Aabb");
        System.out.println("Result: " + result);
    }

    public static String frequencySort(String s) {
        //word: tree - {t: 1, r: 1, e: 2}
        Map<Character, Integer> charCount = new HashMap<>();

        s.chars().forEach(i -> {
            var ch = (char) i;
            var count = charCount.getOrDefault(ch, 0) + 1;
            charCount.remove(ch);
            charCount.put(ch, count);
        });

        List<Map.Entry<Character, Integer>> items = charCount.entrySet().stream().toList();

        StringBuilder sb = new StringBuilder();

        items.stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(i -> {
                    sb.append(String.valueOf(i.getKey()).repeat(i.getValue()));
                });

        return sb.toString();
    }
}
