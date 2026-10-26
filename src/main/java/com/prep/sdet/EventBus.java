package com.prep.sdet;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Snippet 12: pub-sub.
 * subscribe returns a Runnable that unsubscribes. Subscribers are called in subscription order.
 * publish returns the number of subscribers that handled the message successfully.
 * A throwing subscriber does not prevent others from receiving the message.
 * null topic or handler throws IAE.
 */
public class EventBus {
    private final Map<String, List<Consumer<String>>> subscribers = new ConcurrentHashMap<>();

    public Runnable subscribe(String topic, Consumer<String> handler) {
        if (topic == null || handler == null) throw new IllegalArgumentException("null");
        List<Consumer<String>> list = subscribers.computeIfAbsent(topic, t -> new CopyOnWriteArrayList<>());
        list.add(handler);
        return () -> list.remove(handler);
    }

    public int publish(String topic, String message) {
        if (topic == null) throw new IllegalArgumentException("null");
        int delivered = 0;
        for (Consumer<String> h : subscribers.getOrDefault(topic, List.of())) {
            try {
                h.accept(message);
                delivered++;
            } catch (RuntimeException ignored) {
                // isolate faulty subscribers
            }
        }
        return delivered;
    }
}
