package com.prep.sdet;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

/**
 * Snippet 5a: time-dependent logic (injected Clock).
 * Bucket starts full. Each tryAcquire() consumes one token if available.
 * Tokens refill continuously at refillPerSecond, never above capacity.
 */
public class TokenBucketRateLimiter {
    private final int capacity;
    private final double refillPerSecond;
    private final Clock clock;
    private double tokens;
    private Instant last;

    public TokenBucketRateLimiter(int capacity, double refillPerSecond, Clock clock) {
        if (capacity < 1 || refillPerSecond <= 0) throw new IllegalArgumentException("bad config");
        this.capacity = capacity;
        this.refillPerSecond = refillPerSecond;
        this.clock = clock;
        this.tokens = capacity;
        this.last = clock.instant();
    }

    public synchronized boolean tryAcquire() {
        refill();
        if (tokens >= 1) {
            tokens -= 1;
            return true;
        }
        return false;
    }

    private void refill() {
        Instant now = clock.instant();
        double elapsedSec = Duration.between(last, now).toMillis() / 1000.0;
        tokens = Math.min(capacity, tokens + elapsedSec * refillPerSecond);
        last = now;
    }
}
