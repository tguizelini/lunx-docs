/*
    Dada uma String "s", encontre o comprimento da substring mais  longa sem repetir
    caracteres.

    Uma substring é uma sequencia contígua e não vazia de caracteres dentro de uma string.

    Ex1:
    input -> s = "abcabcbb"
    output -> 3

    Ex2:
    input -> s = "bbbbb"
    output -> 1
 */

import java.util.HashMap;
import java.util.Map;

public class LongestSubstringNoRepeatChar {
    public static void main(String[] args) {
        String s = "abcabcbb";
        var original = validateString(s.toCharArray());
        var sw = slidingWindows(s);
        System.out.println("Original::output -> " + original);
        System.out.println("SlidingWindows::output -> " + sw);
    }

    public static int validateString(char[] s) {
        Map<Character, Integer> window = new HashMap(); // lista de sequencia
        int maxLength = 0; // maior sequencia

        //begin - percorrer caracteres da str
        for (int index = 0; index < s.length; index ++) {
            //verificar se caracter existe na lista
            if (window.containsKey((char) s[index])) {
                //se existir, verifico o size de ambas e atualizo o maxLength
                if (window.size() > maxLength) {
                    maxLength = window.size();
                }
                window.clear(); //reset a lista
            }
            window.put((char) s[index], index); //salvo o char + index na nova janela
        }
        //end - percorrer caracteres da str

        return maxLength;
    }

    public static int slidingWindows(String s) {
        Map<Character, Integer> window = new HashMap<>();

        int left = 0;
        int maxLength = 0;

        for (int right = 0; right < s.length(); right++) {

            char current = s.charAt(right);

            if (window.containsKey(current)) {
                left = Math.max(left, window.get(current) + 1);
            }

            window.put(current, right);

            maxLength = Math.max(maxLength, right - left + 1);
        }

        return maxLength;
    }
}
