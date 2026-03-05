/*
    Design a data structure that follows the constraints of a Least Recently Used (LRU) cache.

    Implement the LRUCache class:

    1 - LRUCache(int capacity) Initialize the LRU cache with positive size capacity.
    2 - int get(T key) Return the value of the key if the key exists, otherwise return -1.
    3 - void put(T key, T value) Update the value of the key if the key exists.
    Otherwise, add the key-value pair to the cache.
    If the number of keys exceeds the capacity from this operation, evict the least recently used key.

    Example:
    Input
        ["LRUCache", "put", "put", "get", "put", "get", "put", "get", "get", "get"]
        [[2], [1, 1], [2, 2], [1], [3, 3], [2], [4, 4], [1], [3], [4]]
        Output
        [null, null, null, 1, null, -1, null, -1, 3, 4]

        Explanation
        LRUCache lRUCache = new LRUCache(2);
        lRUCache.put(1, 1); // cache is {1=1}
        lRUCache.put(2, 2); // cache is {1=1, 2=2}
        lRUCache.get(1);    // return 1
        lRUCache.put(3, 3); // LRU key was 2, evicts key 2, cache is {1=1, 3=3}
        lRUCache.get(2);    // returns -1 (not found)
        lRUCache.put(4, 4); // LRU key was 1, evicts key 1, cache is {4=4, 3=3}
        lRUCache.get(1);    // return -1 (not found)
        lRUCache.get(3);    // return 3
        lRUCache.get(4);    // return 4
*/

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class LRUCachePlayground {
    public static void main(String[] args) {
        var lRUCache = new LRUCache(2);
        lRUCache.put(1, 1); // cache is {1=1}
        lRUCache.put(2, 2); // cache is {1=1, 2=2}
        System.out.println(lRUCache.get(1));    // return 1
        lRUCache.put(3, 3); // LRU key was 2, evicts key 2, cache is {1=1, 3=3}
        System.out.println(lRUCache.get(2));    // returns -1 (not found)
        lRUCache.put(4, 4); // LRU key was 1, evicts key 1, cache is {4=4, 3=3}
        System.out.println(lRUCache.get(1));    // return -1 (not found)
        System.out.println(lRUCache.get(3));    // return 3
        System.out.println(lRUCache.get(4));    // return 4

        //result: 1, -1, -1, 3, 4
    }
}

class LRUCache {

    private int capacity;
    private Map<Integer, Integer> cacheValue;
    private int lastValue;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cacheValue = new LinkedHashMap<>();
    }

    //result: 1, -1, -1, 3, 4
    public int get(int key) {
        if (cacheValue.containsKey(key)) {
            lastValue = lastValue;
            return cacheValue.get(key);
        }

        lastValue = -1;
        return -1;
    }

    public void put(int key, int value) {
        if (cacheValue.size() == capacity) {
            cacheValue.remove(getLastCacheKey());
        }

        lastValue = key;
        cacheValue.put(key, value);
    }

    private int getLastCacheKey() {
        return cacheValue
                .entrySet()
                .stream()
                .findFirst()
                .get()
                .getKey();
    }
}
