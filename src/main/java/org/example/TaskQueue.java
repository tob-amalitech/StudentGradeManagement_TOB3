package org.example;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple Priority-based task queue backed by a PriorityQueue and an ExecutorService.
 */
public class TaskQueue {
    private final PriorityQueue<PrioritizedTask> queue = new PriorityQueue<>(Comparator.reverseOrder());
    private final ExecutorService ex;
    private final AtomicInteger running = new AtomicInteger(0);

    public TaskQueue(int threads) {
        this.ex = Executors.newFixedThreadPool(Math.max(1, threads));
    }

    public void submit(Runnable r, int priority) {
        PrioritizedTask t = new PrioritizedTask(priority, r);
        synchronized (queue) {
            queue.add(t);
        }
        ex.submit(() -> {
            PrioritizedTask next;
            synchronized (queue) { next = queue.poll(); }
            if (next != null) {
                running.incrementAndGet();
                try { next.r.run(); } finally { running.decrementAndGet(); }
            }
        });
    }

    public int activeCount() { return running.get(); }

    public void shutdown() {
        ex.shutdown();
    }

    private static class PrioritizedTask implements Comparable<PrioritizedTask> {
        final int priority;
        final Runnable r;

        PrioritizedTask(int priority, Runnable r) { this.priority = priority; this.r = r; }

        @Override
        public int compareTo(PrioritizedTask o) { return Integer.compare(this.priority, o.priority); }
    }
}
