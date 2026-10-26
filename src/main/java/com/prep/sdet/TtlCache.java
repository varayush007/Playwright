package com.prep.sdet;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Snippet 5b: TTL cache. An entry is valid strictly before its expiry instant,
 * i.e. at exactly put-time + ttl it is already expired.
 * ttl must be positive (IAE). Expired entries are removed on access.
 */
public class TtlCache<K, V> {
    private record Entry<V>(V value, Instant expiresAt) {}

    private final Clock clock;
    private final Map<K, Entry<V>> store = new HashMap<>();

    public TtlCache(Clock clock) { this.clock = clock; }

    public void put(K key, V value, Duration ttl) {
        if (ttl == null || ttl.isZero() || ttl.isNegative()) throw new IllegalArgumentException("ttl must be positive");
        store.put(key, new Entry<>(value, clock.instant().plus(ttl)));
    }

    public Optional<V> get(K key) {
        Entry<V> e = store.get(key);
        if (e == null) return Optional.empty();
        if (!clock.instant().isBefore(e.expiresAt())) {
            store.remove(key);
            return Optional.empty();
        }
        return Optional.of(e.value());
    }

    public int size() { return store.size(); }
}
