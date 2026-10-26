package com.prep.sdet.solutions;

import static org.testng.Assert.*;

import com.prep.sdet.TtlCache;
import java.time.Duration;
import java.time.Instant;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import testutil.MutableClock;

public class TtlCacheTest {
    private MutableClock clock;
    private TtlCache<String, String> cache;

    @BeforeMethod
    public void setUp() {
        clock = new MutableClock(Instant.parse("2026-01-01T00:00:00Z"));
        cache = new TtlCache<>(clock);
    }

    @Test
    public void get_missingKey_isEmpty() {
        assertTrue(cache.get("x").isEmpty());
    }

    @Test
    public void get_beforeExpiry_returnsValue() {
        cache.put("k", "v", Duration.ofSeconds(10));
        clock.advance(Duration.ofMillis(9_999));
        assertEquals(cache.get("k").orElseThrow(), "v");
    }

    @Test
    public void get_exactlyAtExpiry_isExpired() {
        cache.put("k", "v", Duration.ofSeconds(10));
        clock.advance(Duration.ofSeconds(10));
        assertTrue(cache.get("k").isEmpty());
    }

    @Test
    public void get_afterExpiry_removesEntry() {
        cache.put("k", "v", Duration.ofSeconds(10));
        clock.advance(Duration.ofSeconds(11));
        assertTrue(cache.get("k").isEmpty());
        assertEquals(cache.size(), 0);
    }

    @Test
    public void put_sameKeyAgain_resetsTtl() {
        cache.put("k", "old", Duration.ofSeconds(10));
        clock.advance(Duration.ofSeconds(8));
        cache.put("k", "new", Duration.ofSeconds(10));
        clock.advance(Duration.ofSeconds(8));              // 16s since first put, 8s since second
        assertEquals(cache.get("k").orElseThrow(), "new");
    }

    @Test
    public void entries_haveIndependentTtls() {
        cache.put("short", "s", Duration.ofSeconds(5));
        cache.put("long", "l", Duration.ofSeconds(50));
        clock.advance(Duration.ofSeconds(10));
        assertTrue(cache.get("short").isEmpty());
        assertEquals(cache.get("long").orElseThrow(), "l");
    }

    @Test
    public void put_invalidTtl_throws() {
        assertThrows(IllegalArgumentException.class, () -> cache.put("k", "v", Duration.ZERO));
        assertThrows(IllegalArgumentException.class, () -> cache.put("k", "v", Duration.ofSeconds(-1)));
        assertThrows(IllegalArgumentException.class, () -> cache.put("k", "v", null));
    }
}
