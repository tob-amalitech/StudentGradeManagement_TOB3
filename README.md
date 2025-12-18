# Student Grade Management System

A Java-based console application for managing students and their grades with support for different student types and subject categories.

## Features

- **Student Management**: Add and view both Regular and Honors students
- **Grade Recording**: Record grades for various subjects (Core and Elective)
- **Grade Reports**: View individual student grade reports with averages
- **Automatic Grading**: Different grading scales for Regular and Honors students
- **Pre-loaded Data**: Comes with 5 sample students for testing
- **Export Grade Report - Save student reports to text files
- **Calculate GPA - Convert percentage grades to 4.0 GPA scale
- **﻿﻿Bulk Grade Import - Load multiple grades from CSV file
- **Grade Statistics - View class-wide analytics (highest, lowest, median, standard deviation)
- ﻿﻿**Student Search - Find studentsby name or ID with partial matching


## System Architecture

### Class Structure

**Interfaces:**
- `Gradable` - Defines grading behavior for students

"""
# Student Grade Management System — Lab-ready Summary

This project is a teaching-grade Student Grade Management System implemented in Java. It demonstrates modern Java features (NIO.2, Streams), appropriate Collections usage, concurrency primitives, and input validation.

Quick goals for your lab review
 - Run the CLI demo and show: CSV import, concurrent report generation, live stats updating, and audit log tailing.

Getting started
 - Build and run tests:

```powershell
.\gradlew.bat clean test
```

 - Run the application (CLI):

```powershell
.\gradlew.bat run
```

Important project locations
 - Data directories: data/csv/, data/json/, data/binary/
 - Reports output: reports/
 - Audit log file: audit.log

Key implemented features (matching lab user stories)
 - Multi-format I/O: CSV streaming import, JSON export, binary serialization — see src/main/java/org/example/FileService.java.
 - Regex validation: centralised in src/main/java/org/example/ValidationUtils.java (Student ID, email, phone, name, date, course code, grade patterns).
 - Thread-safe data store & collections: DataStore uses ConcurrentHashMap for O(1) lookup and TreeMap for sorted GPA rankings.
 - Concurrent report generation: ReportGenerator uses a fixed thread pool and reports per-student timing and progress.
 - Real-time stats: StatsService runs as a background scheduled task (updates every 5s) and provides snapshots.
 - Scheduler: SchedulerService wraps a ScheduledExecutorService for recurring tasks.
 - Caching: LRUCache (ConcurrentHashMap + access-order LinkedHashMap) with eviction and stats.
 - Audit trail: AuditLogger writes asynchronously with size-based rotation; CLI includes a tail viewer.
 - Advanced regex search: AdvancedSearch supports field-based regex queries and match highlighting.

Quick demo script (recommended for a 5–8 minute lab demo)
 1. Run the app: .\gradlew.bat run
 2. Import sample CSV:
   - Choose menu option for Bulk Import (or provide path data/csv/import.csv).
   - Show import timing and any parse errors.
 3. Generate reports concurrently:
   - Use the "Generate Reports (concurrent)" menu entry.
   - Pick default thread count (4) and show inline progress and final timings.
 4. Show live stats:
   - Use the "View Grade Statistics" menu entry to display the latest snapshot (auto-updated by background thread).
 5. Tail the audit log:
   - Use the "View Audit Log (tail)" menu entry to show recent audit entries.

Useful files to reference during the review
 - src/main/java/org/example/Main.java — CLI wiring and demo commands
 - src/main/java/org/example/FileService.java — CSV/JSON/Binary I/O
 - src/main/java/org/example/ReportGenerator.java — concurrent report generation and progress listener
 - src/main/java/org/example/StatsService.java — background statistics
 - src/main/java/org/example/LRUCache.java — thread-safe LRU cache implementation
 - src/main/java/org/example/AuditLogger.java — asynchronous audit logging with rotation
 - BIG_O.md — Big‑O analysis for key collections and operations

What is fully implemented (ready for demo)
 - CSV streaming import (large files supported via Files.lines())
 - JSON export and binary serialization
 - Regex-based validation and reusable ValidationUtils
 - Concurrent report generation with timing and inline progress
 - Background stats updates (daemon thread) and snapshot API
 - Thread-safe cache and audit logging with CLI tail

Remaining polish (won't block a lab demo)
 - Persistent schedule storage (schedules survive restarts) — currently the scheduler runs in-memory.
 - WatchService-based automatic import detection (bonus feature)
 - More exhaustive tests and larger sample datasets for performance comparisons
 - Audit rotation metadata (daily rotation + rotation logs) and an interactive audit viewer with filters

Notes for the reviewer
 - The code compiles and tests pass. To reproduce: run .\gradlew.bat clean test.
 - The CLI demonstrates the main workflows; open the listed source files during the review to point out design decisions: use of ConcurrentHashMap for lookups, TreeMap for rankings, LinkedList for grade history, and ExecutorService for concurrency.



"""

