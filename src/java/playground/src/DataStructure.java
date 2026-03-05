import java.sql.Array;
import java.util.*;

public class DataStructure {
    public static void main(String[] args) throws InterruptedException {
        String name = "Na1ruto";

        for (char i : name.toCharArray()) {
            if (Character.isDigit(i)) {
                System.out.println(i + " is a digit");
            } else if (Character.isLetter(i)) {
                System.out.println(i + " is a letter");
            }

            String str = String.valueOf(i);
            String str2 = Character.toString(i);
        }

        Map<String, String> myMap = new HashMap<>();
        myMap.put("key1", "value1");
        myMap.put("key2", "value2");
        myMap.put("key3", "value3");

        for (String key: myMap.keySet()) {
            System.out.println(key);
        }

        for (Map.Entry<String, String> entry: myMap.entrySet()) {
            System.out.println("key = " + entry.getKey() + ", value = " + entry.getValue());
        }

        for (String value : myMap.values()) {
            var key = myMap.entrySet().stream()
                    .filter(map -> map.getValue().equals(value)).findFirst();

            System.out.println("value = " + value + ", key = " + Optional.ofNullable(key.get().getKey()));
        }

        List<String> myList = new ArrayList<>();
        myList.add("item1");
        myList.add("item3");
        myList.add("item2");

        myList.stream().forEach(i -> {
            System.out.println("ListItem: " + i + ", index: " + myList.indexOf(i));
        });

        int type = 2;

        String msg = switch (type) {
            case 1:
                System.out.println("type is 1");
                yield "A";
            case 2:
                System.out.println("type is 2");
                yield "B";
             default:
                System.out.println("type is unknown");
                 yield "C";
        };
        System.out.println(type + " is " + msg);

        List<String> listStr = new ArrayList<>();
        listStr.add("item1");
        listStr.add("item2");
        listStr.add("item3");
        listStr.add("item4");
        listStr.add("item5");
        listStr.add("item6");

        for (String i : listStr) {
            System.out.println("ListItem: " + i + ", index: " + listStr.indexOf(i));
            if (i.contains("2")) break;
        };

        Set<String> mySet = new HashSet<>();
        mySet.add("item1");
        mySet.add("item2");
        mySet.add("item3");
        mySet.add("item2");
        mySet.add("item2");
        mySet.add("item2");

        for (String item : mySet) {
            System.out.println(item);
        }

        mySet.forEach(it -> {
            System.out.println("SetItem: " + it);
        });

        Map<String, String> myMap2 = new HashMap<>();
        myMap2.put("key1", "value1");
        myMap2.put("key2", "value2");
        myMap2.put("key3", "value3");

        myMap2.forEach((k, v) -> {
            System.out.println("myMap2::key = " + k + ", value = " + v);
        });

        myMap2.keySet().forEach(i -> System.out.println("myMap2::key = " + i));
        myMap2.values().forEach(i -> System.out.println("myMap2::value = " + i));
        String findKey = "key2";
        if (myMap2.containsKey(findKey)) {
            System.out.println("myMap2 contains " + findKey);
        } else {
            System.out.println("myMap2 does not contain " + findKey);
        }

        Runnable myThread1 = () -> {
            for (int i = 0; i< 10; i++) {
                System.out.println("Thread 1 is running: " + i);
            }
        };

        Thread t1 = new Thread(myThread1);


        Runnable myThread2 = () -> {
            for (int i = 0; i< 10; i++) {
                System.out.println("Thread 2 is running: " + i);
            }
        };

        Thread t2 = new Thread(myThread2);

        new Thread(() -> {
            for (int i = 0; i< 10; i++) {
                System.out.println("Thread FREE is running: " + i);
            }
        }).start();

        t1.start();
        t1.join();
        t2.start();

        Set<Thread> threads = Thread.getAllStackTraces().keySet();
        threads.forEach(thread -> {
            System.out.println("Thread name: " + thread.getName() + ", state: " + thread.getState());
        });

        String word1 = "rat";
        String word2 = "art";
        char[] a1 = word1.toCharArray();
        Arrays.sort(a1);
        char[] a2 = word2.toCharArray();
        Arrays.sort(a2);
        System.out.println(String.valueOf(a1));
        System.out.println(String.valueOf(a2));
        System.out.println(String.valueOf(a1).equals(String.valueOf(a2)));
    }
}
