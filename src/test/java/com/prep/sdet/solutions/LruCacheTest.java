package com.prep.sdet.solutions;

import static org.testng.Assert.*;

import com.prep.sdet.LruCache;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LruCacheTest {
    private LruCache<String, Integer> cache;

    @BeforeMethod
    public void setUp() { cache = new LruCache<>(2); }

    @Test
    public void get_missingKey_returnsNull() {
        assertNull(cache.get("x"));
    }

    @Test
    public void putThenGet_returnsValue() {
        cache.put("a", 1);
        assertEquals(cache.get("a"), Integer.valueOf(1));
        assertEquals(cache.size(), 1);
    }

    @Test
    public void put_overCapacity_evictsLeastRecentlyUsed() {
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);
        assertFalse(cache.containsKey("a"));
        assertTrue(cache.containsKey("b"));
        assertTrue(cache.containsKey("c"));
        assertEquals(cache.size(), 2);
    }

    @Test
    public void get_marksKeyAsRecentlyUsed() {
        cache.put("a", 1);
        cache.put("b", 2);
        cache.get("a");              // a is now most recent, so b is the LRU
        cache.put("c", 3);
        assertTrue(cache.containsKey("a"));
        assertFalse(cache.containsKey("b"));
    }

    @Test
    public void put_existingKey_updatesValueAndRefreshesRecency() {
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("a", 10);          // update, a becomes most recent
        cache.put("c", 3);           // evicts b
        assertEquals(cache.get("a"), Integer.valueOf(10));
        assertFalse(cache.containsKey("b"));
        assertEquals(cache.size(), 2);
    }

    @Test
    public void capacityOne_keepsOnlyLatest() {
        LruCache<String, Integer> one = new LruCache<>(1);
        one.put("a", 1);
        one.put("b", 2);
        assertNull(one.get("a"));
        assertEquals(one.get("b"), Integer.valueOf(2));
    }

    @Test
    public void size_neverExceedsCapacity() {
        for (int i = 0; i < 100; i++) cache.put("k" + i, i);
        assertEquals(cache.size(), 2);
        assertEquals(cache.get("k99"), Integer.valueOf(99));
        assertEquals(cache.get("k98"), Integer.valueOf(98));
    }

    @DataProvider(name = "badCapacity")
    public Object[][] badCapacity() { return new Object[][]{{0}, {-1}}; }

    @Test(dataProvider = "badCapacity", expectedExceptions = IllegalArgumentException.class)
    public void constructor_invalidCapacity_throws(int capacity) {
        new LruCache<String, Integer>(capacity);
    }
}
