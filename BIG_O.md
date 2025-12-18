Big-O Complexity Report
=======================

This document summarizes the chosen collections and the time complexity of common operations used in the project.

Student Data Structures
-----------------------
- `ConcurrentHashMap<String, Student> studentMap` — O(1) average for `get`, `put`, `remove`. Used for quick student lookup by ID.
- `TreeMap<Double, List<Student>> gpaRankings` — O(log n) for insertion and lookup. Sorted by GPA (descending) used for ranking operations.
- `ArrayList<Student> studentList` (when used) — O(1) access by index, O(n) for insertion in the middle, O(1) amortized append.
- `HashSet<String> uniqueCourses` — O(1) average membership tests.

Grades and History
------------------
- `LinkedList<Grade> gradeHistory` — O(1) for insertion/removal at ends, O(n) for indexed access or searches.
- `TreeMap<String, List<Grade>> subjectGrades` — O(log n) insert/lookup by subject name.

Cache
-----
- `ConcurrentHashMap` for storage: O(1) get/put.
- `LinkedHashMap` access-order for eviction control: O(1) for access/update; overall eviction is O(1) amortized.

Task Scheduling
---------------
- `PriorityQueue<Task>` — O(log n) for insertion and removal (peek/poll), used for selecting highest-priority task.

Streams and File I/O
--------------------
- `Files.lines(path)` processes lines as a stream; memory complexity O(1) per line when processed sequentially, avoids loading entire file.
- CSV streaming: processing time O(m) where m is number of lines; each line parse cost O(k) where k is fields per line.

Parallelism and Concurrency
---------------------------
- Report generation with a fixed thread pool: total wall-clock time approximately (sequential_time / threads) ± overhead.
- Concurrent collections (`ConcurrentHashMap`) provide near O(1) non-blocking reads with some contention overhead under heavy writes.

Performance Notes
-----------------
- For large numbers of students (N) and grades (M):
  - Lookup by student ID remains O(1).
  - Generating GPA rankings is dominated by sorting costs if performed from scratch: O(N log N).
  - Maintaining `TreeMap` incremental rankings keeps per-update cost at O(log R) where R is number of distinct GPA keys.

Recommendations
---------------
- Use `ConcurrentHashMap` for high-concurrency lookup-heavy operations.
- Use `TreeMap` only when sorted iteration or range queries are needed; otherwise `HashMap` is faster for unsorted operations.
- Use streaming (`Files.lines`) to handle very large CSV files to avoid O(file_size) memory usage.

