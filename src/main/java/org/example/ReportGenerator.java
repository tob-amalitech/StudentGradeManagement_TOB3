package org.example;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates per-student reports concurrently using a fixed thread pool.
 */
public class ReportGenerator {
    private final DataStore store;

    public ReportGenerator(DataStore store) {
        this.store = store;
    }

    public interface ProgressListener {
        void onProgress(int completed, int total, String studentId);
    }

    public ReportResult generateReports(List<String> studentIds, int threads, Path outDir, AuditLogger logger, ProgressListener listener) throws InterruptedException {
        if (threads <= 0) threads = Math.max(1, Runtime.getRuntime().availableProcessors());
        ExecutorService ex = Executors.newFixedThreadPool(threads);
        try {
            Files.createDirectories(outDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        AtomicInteger completed = new AtomicInteger();
        ConcurrentHashMap<String,Long> timings = new ConcurrentHashMap<>();

        List<Callable<Void>> tasks = studentIds.stream().map(id -> (Callable<Void>) () -> {
            Instant start = Instant.now();
            try {
                Student s = store.getStudent(id);
                if (s == null) return null;
                Path out = outDir.resolve(id + ".report.txt");
                try (BufferedWriter w = Files.newBufferedWriter(out, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                    w.write("Report for " + s.getName() + " (" + s.getId() + ")\n");
                    w.write("GPA: " + s.computeGPA() + "\n");
                    w.write("Grades:\n");
                    for (Grade g : s.getGradeHistory()) {
                        w.write(String.format(" - %s (%s): %.2f\n", g.getCourseCode(), g.getCourseName(), g.getScore()));
                    }
                }
                long elapsed = Duration.between(start, Instant.now()).toMillis();
                timings.put(id, elapsed);
                if (logger != null) logger.log("ReportGen", "Generated report for " + id, elapsed, true);
            } catch (Exception e) {
                long elapsed = Duration.between(start, Instant.now()).toMillis();
                timings.put(id, elapsed);
                if (logger != null) logger.log("ReportGen", "Failed for " + id + " -> " + e.getMessage(), elapsed, false);
            } finally {
                int done = completed.incrementAndGet();
                if (listener != null) listener.onProgress(done, studentIds.size(), id);
            }
            return null;
        }).toList();

        Instant totalStart = Instant.now();
        try {
            List<Future<Void>> futures = ex.invokeAll(tasks);
            // wait for all
            for (Future<Void> f : futures) {
                try { f.get(); } catch (Exception ignored) {}
            }
        } finally {
            ex.shutdown();
        }

        long totalMs = Duration.between(totalStart, Instant.now()).toMillis();
        return new ReportResult(studentIds.size(), completed.get(), totalMs, timings);
    }

    public static class ReportResult {
        public final int requested;
        public final int completed;
        public final long totalMs;
        public final ConcurrentHashMap<String,Long> perReportMs;

        public ReportResult(int requested, int completed, long totalMs, ConcurrentHashMap<String,Long> perReportMs) {
            this.requested = requested;
            this.completed = completed;
            this.totalMs = totalMs;
            this.perReportMs = perReportMs;
        }
    }
}
