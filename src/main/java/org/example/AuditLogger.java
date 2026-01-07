package org.example;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Simple asynchronous audit logger. Entries are written to file by a background
 * thread.
 * Rotation occurs when file size exceeds 10MB.
 */
public class AuditLogger {
    private static final long MAX_BYTES = 10 * 1024 * 1024L; // 10MB
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_INSTANT;

    private final Path logFile;
    private final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean running = new AtomicBoolean(true);

    public AuditLogger(Path logFile) {
        this.logFile = logFile;
        Thread writer = new Thread(this::writerLoop, "AuditLogger-Writer");
        writer.setDaemon(true);
        writer.start();
    }

    public void log(String operationType, String userAction, long durationMs, boolean success) {
        String entry = String.format("%s | TID:%s | %s | %s | %dms | %s",
                ISO.format(Instant.now().atZone(ZoneOffset.UTC)),
                Thread.currentThread().threadId(),
                operationType,
                userAction.replaceAll("\n", " "),
                durationMs,
                success ? "SUCCESS" : "FAIL");
        queue.add(entry);
    }

    private void writerLoop() {
        while (running.get()) {
            try {
                flushOnce();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                // swallow to keep logger alive
            }
        }
        // final flush
        try {
            flushOnce();
        } catch (Exception ignored) {
        }
    }

    private void flushOnce() throws IOException {
        if (queue.isEmpty())
            return;
        List<String> batch = new ArrayList<>();
        String s;
        while ((s = queue.poll()) != null)
            batch.add(s);

        // rotate if needed
        try {
            if (Files.exists(logFile) && Files.size(logFile) > MAX_BYTES) {
                Path rotated = logFile.resolveSibling(logFile.getFileName() + ".1");
                Files.move(logFile, rotated);
            }
        } catch (IOException ex) {
            // continue to try writing
        }

        try (BufferedWriter w = Files.newBufferedWriter(logFile, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            for (String line : batch) {
                w.write(line);
                w.write('\n');
            }
        }
    }

    public void shutdown() {
        running.set(false);
    }
}
