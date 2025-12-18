package org.example;
import java.nio.file.Path;

import java.util.Scanner;

/**
 * Application entry point and command-line interface.
 *
 * Responsibilities:
 * - Compose core services and wire dependencies
 * - Provide an interactive CLI for managing students, grades, imports/exports,
 *   reporting, and diagnostics
 * - Start background services (statistics, scheduler, audit logger)
 */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StudentManager studentManager = new StudentManager();
        
        // DEPENDENCY INJECTION - Create all components and pass to GradeManager
        IGradeRepository gradeRepository = new GradeRepository();
        IFileExporter fileExporter = new GradeExporter();
        IGradeImporter gradeImporter = new GradeImporter();
        IGradeStatisticsCalculator statisticsCalculator = new GradeStatisticsCalculator();
        
        // GradeManager coordinates between all these components
        GradeManager gradeManager = new GradeManager(
            gradeRepository,
            fileExporter,
            gradeImporter,
            statisticsCalculator
        );
        // Additional core services used by enhanced CLI
        DataStore store = new DataStore();
        AuditLogger audit = new AuditLogger(java.nio.file.Paths.get("audit.log"));
        FileService fileService = null;
        try { fileService = new FileService(); } catch (java.io.IOException e) { System.err.println("FileService init failed: " + e.getMessage()); }
        LRUCache<String,Object> cache = new LRUCache<>(150);
        ReportGenerator reportGen = new ReportGenerator(store);
        SchedulerService scheduler = new SchedulerService();
        StatsService stats = new StatsService(store, cache, audit);
        AdvancedSearch search = new AdvancedSearch(store);
        TaskQueue taskQueue = new TaskQueue(Math.max(1, Runtime.getRuntime().availableProcessors()));
        // start background stats
        stats.start(5);

        // Auto-load demo data for presentation when store is empty
        try {
            if (store.getAllStudents().isEmpty() || studentManager.getStudentCount() <= 5) {
                DemoDataLoader.loadSampleData(studentManager, store);
            }
        } catch (Exception e) {
            System.err.println("Demo data load skipped: " + e.getMessage());
        }
        
        System.out.println("\u001B[1;34m========================================\u001B[0m");
        System.out.println("\u001B[1;34m  Student Grade Management System\u001B[0m");
        System.out.println("\u001B[1;34m========================================\u001B[0m");
        
        while (true) {
            System.out.println("\n\u001B[1;36m=== Main Menu ===\u001B[0m");
            System.out.println("\u001B[1;33m1\u001B[0m. View Students");
            System.out.println("\u001B[1;33m2\u001B[0m. Add Student");
            System.out.println("\u001B[1;33m3\u001B[0m. Search Students");
            System.out.println("\u001B[1;33m4\u001B[0m. Record Grade");
            System.out.println("\u001B[1;33m5\u001B[0m. View Grade Report");
            System.out.println("\u001B[1;33m6\u001B[0m. Export Grade Report to File");
            System.out.println("\u001B[1;33m7\u001B[0m. Bulk Import Grades from CSV");
            System.out.println("\u001B[1;33m8\u001B[0m. View Grade Statistics");
            System.out.println("\u001B[1;33m10\u001B[0m. Generate Reports (concurrent)");
            System.out.println("\u001B[1;33m20\u001B[0m. Real-Time Statistics Dashboard");
            System.out.println("\u001B[1;33m11\u001B[0m. Cache Stats");
            System.out.println("\u001B[1;33m13\u001B[0m. Load Demo Data (for presentation)");
            System.out.println("\u001B[1;33m12\u001B[0m. View Audit Log (tail)");
            System.out.println("\u001B[1;33m9\u001B[0m. Exit");
            System.out.print("Choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            if (choice == 1) {
                // View all students
                studentManager.viewAllStudents();
                
            } else if (choice == 2) {
                // Add student
                System.out.println("\n1. Regular Student");
                System.out.println("2. Honors Student");
                System.out.print("Type: ");
                int type = scanner.nextInt();
                scanner.nextLine();
                
                System.out.print("Name: ");
                String name = scanner.nextLine();
                System.out.print("Age: ");
                int age = scanner.nextInt();
                scanner.nextLine();
                
                if (type == 1) {
                    // Use subtype to demonstrate Liskov Substitution Principle
                    studentManager.addStudent(new RegularStudent(name, age));
                } else {
                    studentManager.addStudent(new HonorsStudent(name, age));
                }
                
            } else if (choice == 3) {
                // Search students
                System.out.print("\nEnter student name or ID to search: ");
                String query = scanner.nextLine();
                studentManager.searchStudents(query);
                
            } else if (choice == 4) {
                // Record grade
                System.out.print("Student ID: ");
                int id = scanner.nextInt();
                scanner.nextLine();
                
                Student student = studentManager.findStudent(id);
                if (student == null) {
                    System.out.println("Student not found!");
                    continue;
                }
                
                System.out.println("\n1. Core Subject");
                System.out.println("2. Elective Subject");
                System.out.print("Type: ");
                int type = scanner.nextInt();
                scanner.nextLine();
                
                Subject subject = null;
                
                // USE FACTORY PATTERN - Much cleaner!
                if (type == 1) {
                    SubjectFactory.displayCoreSubjectOptions();
                    System.out.print("Choice: ");
                    int s = scanner.nextInt();
                    scanner.nextLine();
                    subject = SubjectFactory.createCoreSubject(s);
                } else {
                    SubjectFactory.displayElectiveSubjectOptions();
                    System.out.print("Choice: ");
                    int s = scanner.nextInt();
                    scanner.nextLine();
                    subject = SubjectFactory.createElectiveSubject(s);
                }
                
                if (subject == null) {
                    System.out.println("Invalid subject choice!");
                    continue;
                }
                
                System.out.print("Score (0-100): ");
                double score = scanner.nextDouble();
                scanner.nextLine();
                
                gradeManager.recordGrade(student, subject, score);
                
            } else if (choice == 5) {
                // View grade report
                System.out.print("Student ID: ");
                int id = scanner.nextInt();
                scanner.nextLine();
                
                Student student = studentManager.findStudent(id);
                if (student == null) {
                    System.out.println("Student not found!");
                    continue;
                }
                
                gradeManager.viewGradeReport(student);
                
            } else if (choice == 6) {
                // Export grade report
                System.out.print("Student ID: ");
                int id = scanner.nextInt();
                scanner.nextLine();
                
                Student student = studentManager.findStudent(id);
                if (student == null) {
                    System.out.println("Student not found!");
                    continue;
                }
                
                gradeManager.exportGradeReport(student);
                
            } else if (choice == 7) {
                // Bulk import grades
                if (fileService != null) {
                    System.out.print("CSV path (relative): ");
                    String path = scanner.nextLine().trim();
                    if (path.isEmpty()) path = "data/csv/import.csv";
                    long start = System.currentTimeMillis();
                    try {
                        var errs = fileService.importStudentsFromCsv(java.nio.file.Paths.get(path), store);
                        long took = System.currentTimeMillis() - start;
                        System.out.println("Imported in " + took + " ms");
                        if (!errs.isEmpty()) errs.forEach(System.out::println);
                    } catch (java.io.IOException e) {
                        System.out.println("Import failed: " + e.getMessage());
                    }
                } else System.out.println("FileService not available");
                
            } else if (choice == 8) {
                // View statistics
                var snap = stats.snapshot();
                System.out.println("Stats snapshot: " + snap);

            } else if (choice == 20) {
                // Launch terminal dashboard (blocking)
                TerminalStatsDashboard dashboard = new TerminalStatsDashboard(stats, store, audit);
                System.out.println("Launching terminal Real-Time Statistics Dashboard (press 'q' to quit)...\n");
                dashboard.runInteractive(5);

            } else if (choice == 13) {
                // Load demo data into StudentManager and DataStore
                DemoDataLoader.loadSampleData(studentManager, store);

            } else if (choice == 10) {
                System.out.print("Enter comma-separated student IDs (e.g. STU001,STU002) or leave empty for all: ");
                String idsLine = scanner.nextLine().trim();
                java.util.List<String> ids;
                if (idsLine.isEmpty()) {
                    ids = store.getAllStudents().stream().map(Student::getId).toList();
                } else {
                    ids = java.util.Arrays.stream(idsLine.split(",")).map(String::trim).toList();
                }
                System.out.print("Threads (2-8, default 4): ");
                int th = 4;
                try { th = Integer.parseInt(scanner.nextLine().trim()); } catch (Exception ignored) {}
                System.out.println("Generating " + ids.size() + " reports using " + th + " threads...");
                Path reportsDir = java.nio.file.Paths.get("reports");
                try {
                    // show simple progress bar via listener
                    var result = reportGen.generateReports(ids, th, reportsDir, audit, (done, total, sid) -> {
                        int pct = (int) ((done * 100.0) / total);
                        System.out.print("\rGenerating reports: " + done + "/" + total + " (" + pct + "%)");
                    });
                    System.out.println("\nCompleted " + result.completed + "/" + result.requested + " in " + result.totalMs + "ms");
                    System.out.println("Per-report times: " + result.perReportMs);
                } catch (InterruptedException e) { System.out.println("Report generation interrupted"); }

            } else if (choice == 11) {
                System.out.println("Cache hits=" + cache.getHits() + " misses=" + cache.getMisses() + " size=" + cache.size() + " evictions=" + cache.getEvictions());

            } else if (choice == 12) {
                System.out.print("Tail lines: ");
                int tail = 20; try { tail = Integer.parseInt(scanner.nextLine().trim()); } catch (Exception ignored) {}
                try {
                    java.nio.file.Path log = java.nio.file.Paths.get("audit.log");
                    var lines = java.nio.file.Files.readAllLines(log);
                    int start = Math.max(0, lines.size() - tail);
                    System.out.println("\n\u001B[1;36m--- Audit Log (tail) ---\u001B[0m");
                    for (int i = start; i < lines.size(); i++) {
                        String line = lines.get(i);
                        if (line.contains("ERROR")) System.out.println("\u001B[31m" + line + "\u001B[0m");
                        else if (line.contains("WARN")) System.out.println("\u001B[33m" + line + "\u001B[0m");
                        else System.out.println(line);
                    }
                } catch (Exception e) { System.out.println("Unable to read audit log: " + e.getMessage()); }
                
            } else if (choice == 9) {
                // Exit
                System.out.println("\n========================================");
                System.out.println("Thank you for using the Grade Management System!");
                System.out.println("========================================");
                break;
                
            } else {
                System.out.println("Invalid choice! Please try again.");
            }
        }
        
        scanner.close();
    }
}
