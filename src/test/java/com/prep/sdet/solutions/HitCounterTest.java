package com.prep.sdet.solutions;

import static org.testng.Assert.*;

import com.prep.sdet.HitCounter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class HitCounterTest {
    private HitCounter counter;

    @BeforeMethod
    public void setUp() { counter = new HitCounter(); }

    @Test
    public void newCounter_startsAtZero() {
        assertEquals(counter.get(), 0);
    }

    @Test
    public void increment_singleThread() {
        for (int i = 0; i < 5; i++) counter.increment();
        assertEquals(counter.get(), 5);
    }

    // FAILS (non-deterministically): count++ is not atomic. Repeating the test raises the chance of catching it.
    @Test(invocationCount = 5, timeOut = 20_000)
    public void increment_manyThreads_noLostUpdates() throws Exception {
        int threads = 8, perThread = 50_000;
        HitCounter shared = new HitCounter();
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        for (int t = 0; t < threads; t++) {
            pool.submit(() -> {
                ready.countDown();
                try {
                    start.await();                           // all threads begin together
                    for (int i = 0; i < perThread; i++) shared.increment();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }
        ready.await();
        start.countDown();
        assertTrue(done.await(15, TimeUnit.SECONDS), "threads did not finish");
        pool.shutdownNow();

        assertEquals(shared.get(), threads * perThread);
    }
}
