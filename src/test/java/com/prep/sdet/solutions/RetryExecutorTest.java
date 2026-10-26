package com.prep.sdet.solutions;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import com.prep.sdet.RetryExecutor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RetryExecutorTest {
    private List<Long> sleeps;          // fake Sleeper: records delays, never really sleeps
    private RetryExecutor executor;
    private Callable<String> task;

    @SuppressWarnings("unchecked")
    @BeforeMethod
    public void setUp() {
        sleeps = new ArrayList<>();
        executor = new RetryExecutor(sleeps::add);
        task = mock(Callable.class);
    }

    @Test
    public void succeedsFirstTry_noRetryNoSleep() throws Exception {
        when(task.call()).thenReturn("ok");

        assertEquals(executor.execute(task, 3, 100), "ok");

        verify(task, times(1)).call();
        assertTrue(sleeps.isEmpty());
    }

    @Test
    public void failsTwiceThenSucceeds_backsOffExponentially() throws Exception {
        when(task.call())
                .thenThrow(new IOException("fail-1"))
                .thenThrow(new IOException("fail-2"))
                .thenReturn("ok");

        assertEquals(executor.execute(task, 5, 100), "ok");

        verify(task, times(3)).call();
        assertEquals(sleeps, List.of(100L, 200L));
    }

    @Test
    public void allAttemptsFail_rethrowsLastException_noSleepAfterLast() throws Exception {
        when(task.call()).thenThrow(new IOException("fail-1"),
                new IOException("fail-2"), new IOException("fail-3"));

        IOException ex = expectThrows(IOException.class, () -> executor.execute(task, 3, 100));

        assertEquals(ex.getMessage(), "fail-3");
        verify(task, times(3)).call();
        assertEquals(sleeps, List.of(100L, 200L));
    }

    @Test
    public void delaysDoubleEachAttempt() throws Exception {
        when(task.call()).thenThrow(new IOException("x"));

        expectThrows(IOException.class, () -> executor.execute(task, 5, 10));

        assertEquals(sleeps, List.of(10L, 20L, 40L, 80L));
    }

    @Test
    public void singleAttempt_failure_noSleep() throws Exception {
        when(task.call()).thenThrow(new IOException("only"));

        expectThrows(IOException.class, () -> executor.execute(task, 1, 100));

        verify(task, times(1)).call();
        assertTrue(sleeps.isEmpty());
    }

    @Test // characterization: runtime exceptions are retried too. Ask whether that is intended
    public void runtimeExceptions_areAlsoRetried() throws Exception {
        when(task.call()).thenThrow(new IllegalStateException("x")).thenReturn("ok");
        assertEquals(executor.execute(task, 3, 50), "ok");
    }

    @Test
    public void interruptedWhileSleeping_propagatesAndStopsRetrying() throws Exception {
        RetryExecutor interrupted = new RetryExecutor(ms -> { throw new InterruptedException("stop"); });
        when(task.call()).thenThrow(new IOException("fail"));

        expectThrows(InterruptedException.class, () -> interrupted.execute(task, 3, 100));

        verify(task, times(1)).call();
    }

    @Test
    public void invalidArguments_throwIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> executor.execute(task, 0, 100));
        assertThrows(IllegalArgumentException.class, () -> executor.execute(task, -1, 100));
        assertThrows(IllegalArgumentException.class, () -> executor.execute(task, 3, -1));
        assertThrows(IllegalArgumentException.class, () -> executor.execute(null, 3, 100));
    }
}
