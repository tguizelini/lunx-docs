import java.util.ArrayList;
import java.util.List;

public class Fibonnaci {
    public static void main(String[] args) {
        System.out.println("----------------------------------");
        System.out.println("---------- BinarySearch ----------");

        var fiboNumbers = fibonnaci(10);

        fiboNumbers.forEach(it -> System.out.print(it + ", "));


        System.out.println("\n----------------------------------");
    }

    public static List<Integer> fibonnaci(int generateNumber) {
        var list = new ArrayList<Integer>();

        for (int i = 0; i < generateNumber; i++) {
            if (list.isEmpty() || list.size() == 1) {
                list.add(i);
            } else {
                int prevNumber1 = list.get(list.size() - 1);
                int prevNumber2 = list.get(list.size() - 2);
                list.add(prevNumber1 + prevNumber2);
            }
        }

        return list;
    }
}
