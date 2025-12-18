package org.example;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe LRU cache with ConcurrentHashMap storage and a synchronized access-order LinkedHashMap for eviction.
 */
public class LRUCache<K,V> {
    private final int maxSize;
    private final ConcurrentHashMap<K,V> store = new ConcurrentHashMap<>();
    private final LinkedHashMap<K,Instant> accessOrder = new LinkedHashMap<>(16, 0.75f, true);
    private final Object orderLock = new Object();

    private final AtomicLong hits = new AtomicLong();
    private final AtomicLong misses = new AtomicLong();
    private final AtomicLong evictions = new AtomicLong();

    public LRUCache(int maxSize) {
        if (maxSize <= 0) throw new IllegalArgumentException("maxSize must be > 0");
        this.maxSize = maxSize;
    }

    public V get(K key) {
        V v = store.get(key);
        if (v != null) {
            hits.incrementAndGet();
            touch(key);
        } else {
            misses.incrementAndGet();
        }
        return v;
    }

    public void put(K key, V value) {
        store.put(key, value);
        touch(key);
        evictIfNeeded();
    }

    private void touch(K key) {
        synchronized (orderLock) {
            accessOrder.put(key, Instant.now());
        }
    }

    private void evictIfNeeded() {
        synchronized (orderLock) {
            while (accessOrder.size() > maxSize) {
                K eldest = accessOrder.keySet().iterator().next();
                accessOrder.remove(eldest);
                if (eldest != null) {
                    store.remove(eldest);
                    evictions.incrementAndGet();
                }
            }
        }
    }

    public void invalidate(K key) {
        store.remove(key);
        synchronized (orderLock) { accessOrder.remove(key); }
    }

    public void clear() {
        store.clear();
        synchronized (orderLock) { accessOrder.clear(); }
    }

    public long getHits() { return hits.get(); }
    public long getMisses() { return misses.get(); }
    public long getEvictions() { return evictions.get(); }
    public int size() { return store.size(); }

    public Map<K,Instant> snapshotAccessOrder() {
        synchronized (orderLock) { return Collections.unmodifiableMap(new LinkedHashMap<>(accessOrder)); }
    }
}
