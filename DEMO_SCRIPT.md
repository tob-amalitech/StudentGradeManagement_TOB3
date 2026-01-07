DEMO: Student Grade Management — One-Page Talking Points

Slide 1 — Title
- **Project:** Student Grade Management System
- **Presenter:** (Your name)
- **Duration:** 5–7 minutes demo

Slide 2 — Goals
- **Problem:** Manage student grades, reports, imports, and audits at scale.
- **Goal:** Demonstrate CSV import, concurrent report generation, real-time stats, and audit trail.

Slide 3 — Key Features (quick bullets)
- **Multi-format I/O:** CSV streaming import, JSON export, binary snapshots.
- **Validation:** Regex-based validators for IDs, emails, names, dates.
- **Concurrency:** Parallel report generation with progress and per-report timing.
- **Cache:** Thread-safe LRU cache for repeated queries.
- **Audit Trail:** Asynchronous audit logging with rotation and CLI tail.

Slide 4 — Architecture (one-liner)
- **Services:** `DataStore` (in-memory), `FileService`, `ReportGenerator`, `StatsService`, `AuditLogger`, `LRUCache` — wired in `Main` CLI.

Slide 5 — Live Demo Steps (commands)
- **Start Application:**
```powershell
./gradlew run
```
- **Import sample CSV:** Select option **7** (Bulk Import) -> Enter `data/csv/import.csv` or default.
- **Generate concurrent reports:** Select option **10** -> Press Enter (for all) -> Enter `8` threads.
- **Show real-time stats:** Select option **20** to view the live dashboard.
- **Tail the audit log:** Select option **12** -> Enter line count (e.g. 20).


Slide 6 — Talking Points During Demo
- **Import:** Show streaming read (low memory), validation errors printed, successful count.
- **Reports:** Point out parallelism, progress output, average report time.
- **Stats:** Highlight live aggregates (avg GPA, distribution).
- **Audit:** Explain async logging and rotation (durability + low latency).

Slide 7 — Next Steps / Q&A
- **Planned:** Persist scheduled jobs, WatchService auto-import, richer audit viewer filters, extended tests/performance runs.
- **Ask:** Any area you'd like a deeper dive into? Code, performance, or UX?

End of demo script — one page of slides-style talking points.
