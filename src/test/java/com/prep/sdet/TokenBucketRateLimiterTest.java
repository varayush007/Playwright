package com.prep.sdet;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.time.Duration;
import java.time.Instant;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import testutil.MutableClock;

public class TokenBucketRateLimiterTest {
    private MutableClock clock;
    private TokenBucketRateLimiter limiter;

    @BeforeMethod
    public void setUp() {
        clock = new MutableClock(Instant.parse("2026-01-01T00:00:00Z"));
        limiter = new TokenBucketRateLimiter(3, 1.0, clock);   // capacity 3, 1 token/sec
    }

    @Test
    public void startsFull_allowsCapacityThenBlocks() {
        assertTrue(limiter.tryAcquire());
        assertTrue(limiter.tryAcquire());
        assertTrue(limiter.tryAcquire());
        assertFalse(limiter.tryAcquire());
    }

    @Test
    public void refillsOneTokenAfterOneSecond() {
        for (int i = 0; i < 3; i++) limiter.tryAcquire();

        clock.advance(Duration.ofMillis(999));
        assertFalse(limiter.tryAcquire());   // boundary: just under 1s

        clock.advance(Duration.ofMillis(1));
        assertTrue(limiter.tryAcquire());    // exactly 1s since start of drain
    }

    @Test
    public void refillIsCappedAtCapacity() {
        clock.advance(Duration.ofHours(1));
        for (int i = 0; i < 3; i++) assertTrue(limiter.tryAcquire());
        assertFalse(limiter.tryAcquire());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void constructor_zeroCapacity_throws() {
        new TokenBucketRateLimiter(0, 1.0, clock);
    }
}
