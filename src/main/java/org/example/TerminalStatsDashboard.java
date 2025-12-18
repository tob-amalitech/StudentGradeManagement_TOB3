package org.example;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Simple terminal-based real-time statistics dashboard.
 * - Auto-refreshes every `refreshSeconds`
 * - Supports: pause/resume stats calculation, manual refresh, quit
 */
public class TerminalStatsDashboard {
    private final StatsService stats;
    private final DataStore store;
    private final AuditLogger audit;
    private final ScheduledExecutorService refresher = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "TerminalStats-Refresher");
        t.setDaemon(true);
        return t;
    });
    private volatile boolean running = false;
    private final char[] spinner = new char[] {'|','/','-','\\'};
    private int spinIdx = 0;
    private int loadingProgress = 0;

    public TerminalStatsDashboard(StatsService stats, DataStore store, AuditLogger audit) {
        this.stats = stats;
        this.store = store;
        this.audit = audit;
    }

    public void start(int refreshSeconds) {
        if (running) return;
        running = true;
        // schedule periodic redraw
        refresher.scheduleAtFixedRate(this::render, 0, refreshSeconds, TimeUnit.SECONDS);
        // input thread (non-blocking)
        Thread input = new Thread(this::inputLoop, "TerminalStats-Input");
        input.setDaemon(true);
        input.start();
    }

    /**
     * Run interactive dashboard in blocking mode: schedules refresh and then runs input loop
     * on the current thread until the user quits.
     */
    public void runInteractive(int refreshSeconds) {
        if (running) return;
        running = true;
        refresher.scheduleAtFixedRate(this::render, 0, refreshSeconds, TimeUnit.SECONDS);
        // run input loop on calling thread (blocks until quit)
        inputLoop();
        stop();
    }

    private void inputLoop() {
        Scanner sc = new Scanner(System.in);
        while (running) {
            System.out.println("\nCommands: [p]ause/resume  [r]efresh  [q]uit");
            System.out.print("Enter command: ");
            String cmd = sc.nextLine().trim().toLowerCase();
            switch (cmd) {
                case "p":
                    if (stats.isPaused()) { stats.resume(); System.out.println("Resumed stats computation."); }
                    else { stats.pause(); System.out.println("Paused stats computation."); }
                    break;
                case "r":
                    render();
                    break;
                case "q":
                    stop();
                    return;
                default:
                    System.out.println("Unknown command");
            }
        }
    }

    public void stop() {
        running = false;
        try { refresher.shutdownNow(); } catch (Exception ignored) {}
        // do not shutdown stats service here (caller owns it), but record audit
        if (audit != null) audit.log("Dashboard", "Stopped dashboard", 0, true);
    }

    private void render() {
        clearScreen();
        // header
        System.out.println("\u001B[1;36m+------------------------------------------------------+\u001B[0m");
        System.out.println("\u001B[1;36m|\u001B[0m \u001B[1;33mReal-Time Statistics Dashboard\u001B[0m                            \u001B[1;36m|\u001B[0m");
        System.out.println("\u001B[1;36m+------------------------------------------------------+\u001B[0m\n");

        // status
        String status = stats.isPaused() ? "PAUSED" : (stats.isShutdown() ? "STOPPED" : "RUNNING");
        String statusColor = status.equals("RUNNING") ? "\u001B[32m" : status.equals("PAUSED") ? "\u001B[33m" : "\u001B[31m";
        System.out.println("Status: " + statusColor + status + "\u001B[0m    " + "Spinner: " + "\u001B[33m" + spinner[spinIdx % spinner.length] + "\u001B[0m");
        spinIdx++;

        // snapshot
        Map<String,Object> snap = stats.snapshot();
        Object lastUpdate = snap.getOrDefault("lastUpdate", "-");
        System.out.println("Last update: \u001B[2m" + lastUpdate + "\u001B[0m");

        // loading indicator with animated bar
        boolean loading = stats.isLoading();
        if (loading) {
            loadingProgress = (loadingProgress + 3) % 101; // 0-100
            int barLen = 30;
            int filled = (int) Math.round((loadingProgress / 100.0) * barLen);
            StringBuilder bar = new StringBuilder();
            bar.append('[');
            for (int i = 0; i < filled; i++) bar.append('\u2588');
            for (int i = filled; i < barLen; i++) bar.append(' ');
            bar.append(']');
            System.out.printf("Loading: \u001B[33m%s %3d%%\u001B[0m\n", bar.toString(), loadingProgress);
        } else {
            loadingProgress = 0;
            System.out.println("Loading: \u001B[32mIdle\u001B[0m");
        }

        // basic stats block
        double avgGpa = ((Number) snap.getOrDefault("averageGPA", 0.0)).doubleValue();
        int total = ((Number) snap.getOrDefault("totalStudents", 0)).intValue();
        long hits = ((Number) snap.getOrDefault("cacheHits", 0)).longValue();
        long misses = ((Number) snap.getOrDefault("cacheMisses", 0)).longValue();

        System.out.println();
        System.out.printf("%sAverage GPA:%s  %.2f    %sStudents:%s  %d\n", "\u001B[1m", "\u001B[0m", avgGpa, "\u001B[1m", total);
        System.out.printf("Cache: hits=%d misses=%d\n", hits, misses);

        // distribution and top performers
        printDistributionAndTopPerformers();

        System.out.println("\n(Commands: p=pause/resume  r=refresh  q=quit)");
        // small footer line
        System.out.println("\u001B[1;36m+------------------------------------------------------+\u001B[0m");
    }

    private void printDistributionAndTopPerformers() {
        List<Student> students = store.getAllStudents();
        int a=0,b=0,c=0,d=0,f=0;
        for (Student s : students) {
            double gpa = s.computeGPA();
            if (gpa >= 90) a++;
            else if (gpa >= 80) b++;
            else if (gpa >= 70) c++;
            else if (gpa >= 60) d++;
            else f++;
        }
        System.out.println("Grade distribution (A/B/C/D/F): " + a + "/" + b + "/" + c + "/" + d + "/" + f);

        // top performers
        students.stream()
                .sorted((x,y) -> Double.compare(y.computeGPA(), x.computeGPA()))
                .limit(5)
                .forEach(s -> System.out.printf("%s - GPA: %.2f\n", s.getId(), s.computeGPA()));
    }

    private void clearScreen() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                System.out.print("\033[H\033[2J");
        } catch (Exception ignored) {}
    }
}
