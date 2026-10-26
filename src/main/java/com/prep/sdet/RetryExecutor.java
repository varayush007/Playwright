package com.prep.sdet;

import java.util.concurrent.Callable;

/**
 * Snippet 10: retry with exponential backoff.
 * Runs the task up to maxAttempts times. Between attempts it sleeps baseDelayMs * 2^(attempt-1).
 * No sleep after the final failed attempt. Returns on first success.
 * If all attempts fail, rethrows the last exception.
 * maxAttempts < 1, baseDelayMs < 0 or null task throws IAE.
 */
public class RetryExecutor {
    private final Sleeper sleeper;

    public RetryExecutor(Sleeper sleeper) { this.sleeper = sleeper; }

    public <T> T execute(Callable<T> task, int maxAttempts, long baseDelayMs) throws Exception {
        if (task == null || maxAttempts < 1 || baseDelayMs < 0) throw new IllegalArgumentException("bad input");
        Exception last = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return task.call();
            } catch (Exception e) {
                last = e;
                if (attempt < maxAttempts) sleeper.sleep(baseDelayMs * (1L << (attempt - 1)));
            }
        }
        throw last;
    }
}
