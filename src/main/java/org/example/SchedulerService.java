package org.example;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * SchedulerService wraps a ScheduledExecutorService and allows registering periodic tasks.
 * Tasks are persisted to a simple in-memory map; persistence to disk can be added.
 */
public class SchedulerService {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final Map<String, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();

    public void scheduleAtFixedRate(String name, Runnable task, long initialDelay, long period, TimeUnit unit) {
        ScheduledFuture<?> f = scheduler.scheduleAtFixedRate(() -> {
            Instant start = Instant.now();
            try { task.run(); } finally {}
        }, initialDelay, period, unit);
        tasks.put(name, f);
    }

    public void cancel(String name) {
        ScheduledFuture<?> f = tasks.remove(name);
        if (f != null) f.cancel(false);
    }

    public void shutdown() {
        scheduler.shutdown();
        try { scheduler.awaitTermination(5, TimeUnit.SECONDS); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }
}
