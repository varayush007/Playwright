package com.prep.sdet.solutions;

import static org.testng.Assert.*;

import com.prep.sdet.EventBus;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EventBusTest {
    private EventBus bus;
    private List<String> log;

    @BeforeMethod
    public void setUp() {
        bus = new EventBus();
        log = new ArrayList<>();
    }

    @Test
    public void publish_deliversToAllSubscribers_inSubscriptionOrder() {
        bus.subscribe("t", m -> log.add("first:" + m));
        bus.subscribe("t", m -> log.add("second:" + m));

        int delivered = bus.publish("t", "hello");

        assertEquals(delivered, 2);
        assertEquals(log, List.of("first:hello", "second:hello"));
    }

    @Test
    public void publish_noSubscribers_returnsZero() {
        assertEquals(bus.publish("nobody", "x"), 0);
    }

    @Test
    public void topics_areIsolated() {
        bus.subscribe("a", m -> log.add("a:" + m));
        bus.subscribe("b", m -> log.add("b:" + m));

        bus.publish("a", "1");

        assertEquals(log, List.of("a:1"));
    }

    @Test
    public void unsubscribe_stopsDelivery() {
        Runnable unsubscribe = bus.subscribe("t", m -> log.add(m));
        bus.publish("t", "before");
        unsubscribe.run();
        int delivered = bus.publish("t", "after");

        assertEquals(log, List.of("before"));
        assertEquals(delivered, 0);
    }

    @Test
    public void unsubscribe_calledTwice_isHarmless() {
        Runnable unsubscribe = bus.subscribe("t", m -> log.add(m));
        unsubscribe.run();
        unsubscribe.run();
        assertEquals(bus.publish("t", "x"), 0);
    }

    @Test
    public void throwingSubscriber_doesNotBlockOthers_andIsNotCounted() {
        bus.subscribe("t", m -> { throw new RuntimeException("boom"); });
        bus.subscribe("t", m -> log.add(m));

        int delivered = bus.publish("t", "msg");

        assertEquals(delivered, 1);
        assertEquals(log, List.of("msg"));
    }

    @Test
    public void subscriberUnsubscribingItselfDuringPublish_doesNotBreakIteration() {
        Runnable[] self = new Runnable[1];
        self[0] = bus.subscribe("t", m -> { log.add("one"); self[0].run(); });
        bus.subscribe("t", m -> log.add("two"));

        assertEquals(bus.publish("t", "x"), 2);
        assertEquals(log, List.of("one", "two"));
        assertEquals(bus.publish("t", "y"), 1);   // "one" is gone now
    }

    @Test
    public void nullMessage_isDeliveredAsIs() {
        bus.subscribe("t", m -> log.add(String.valueOf(m)));
        bus.publish("t", null);
        assertEquals(log, List.of("null"));
    }

    @Test
    public void nullArguments_throw() {
        assertThrows(IllegalArgumentException.class, () -> bus.subscribe(null, m -> {}));
        assertThrows(IllegalArgumentException.class, () -> bus.subscribe("t", null));
        assertThrows(IllegalArgumentException.class, () -> bus.publish(null, "x"));
    }
}
