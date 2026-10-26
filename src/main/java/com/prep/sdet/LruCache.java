package com.prep.sdet;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Snippet 4b: LRU cache. Capacity must be >= 1 (IAE otherwise).
 * get() returns null when absent and marks the key as most recently used.
 * put() on an existing key updates the value and marks it most recently used.
 * When over capacity, the least recently used entry is evicted.
 */
public class LruCache<K, V> {
    private final int capacity;
    private final LinkedHashMap<K, V> map;

    public LruCache(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("capacity must be >= 1");
        this.capacity = capacity;
        this.map = new LinkedHashMap<>(16, 0.75f, true) {
            @Override protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > LruCache.this.capacity;
            }
        };
    }

    public V get(K key) { return map.get(key); }
    public void put(K key, V value) { map.put(key, value); }
    public int size() { return map.size(); }
    public boolean containsKey(K key) { return map.containsKey(key); }
}
