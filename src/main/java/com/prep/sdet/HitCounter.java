package com.prep.sdet;

/**
 * Snippet 15: concurrency. Counts hits; must be safe to call increment() from many threads.
 */
public class HitCounter {
    private int count;

    public void increment() { count++; }

    public int get() { return count; }
}
