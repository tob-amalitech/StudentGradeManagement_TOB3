package org.example;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Background statistics calculator and dashboard data provider. Runs every intervalSeconds.
 */
public class StatsService {
    private final DataStore store;
    private final LRUCache<String, Object> cache;
    private final AuditLogger logger;
    private final ScheduledExecutorService svc = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "StatsService-Worker");
        t.setDaemon(true);
        return t;
    });
    private final Map<String, Object> latest = new ConcurrentHashMap<>();
    private volatile boolean paused = false;
    private volatile boolean loading = false;

    public StatsService(DataStore store, LRUCache<String,Object> cache, AuditLogger logger) {
        this.store = store;
        this.cache = cache;
        this.logger = logger;
    }

    public void start(int intervalSeconds) {
        svc.scheduleAtFixedRate(this::compute, 0, intervalSeconds, TimeUnit.SECONDS);
    }

    public void compute() {
        if (paused) return;
        loading = true;
        Instant start = Instant.now();
        try {
            double avg = store.getAllStudents().stream().mapToDouble(Student::computeGPA).average().orElse(0.0);
            long totalStudents = store.getAllStudents().size();
            latest.put("averageGPA", avg);
            latest.put("totalStudents", totalStudents);
            latest.put("lastUpdate", Instant.now().toString());
            latest.put("cacheHits", cache.getHits());
            latest.put("cacheMisses", cache.getMisses());
            if (logger != null) logger.log("StatsCalc", "Computed stats", 0, true);
        } catch (Exception e) {
            if (logger != null) logger.log("StatsCalc", "Failed to compute stats: " + e.getMessage(), 0, false);
        } finally {
            loading = false;
        }
    }

    public Map<String,Object> snapshot() { return Map.copyOf(latest); }

    public void pause() { paused = true; }
    public void resume() { paused = false; }
    public boolean isPaused() { return paused; }

    public boolean isLoading() { return loading; }

    public void shutdown() {
        svc.shutdown();
    }

    /**
     * Returns true if the internal scheduler is shutdown or terminated.
     */
    public boolean isShutdown() {
        return svc.isShutdown() || svc.isTerminated();
    }
}
