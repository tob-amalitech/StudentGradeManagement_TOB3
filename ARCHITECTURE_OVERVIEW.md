# Architecture Overview - SOLID Refactoring

## System Architecture Diagram

```
┌────────────────────────────────────────────────────────────────────────┐
│                          USER (Main.java)                              │
│                    Creates and Coordinates System                      │
└────────────────┬───────────────────────────────────────────────────────┘
                 │
                 │ Creates and Injects Dependencies
                 ▼
        ┌────────────────────┐
        │  GradeManager      │  (Coordinator - knows all components)
        │                    │
        │ Public Methods:    │
        │ - recordGrade()    │
        │ - viewGradeReport()│
        │ - exportReport()   │
        │ - bulkImport()     │
        │ - viewStats()      │
        └────────┬───────────┘
                 │
        ┌────────┴────────┬──────────────┬──────────────────┐
        │                 │              │                  │
        ▼                 ▼              ▼                  ▼
   ┌─────────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────────┐
   │GradeManager │  │GradeExp- │  │GradeIm-  │  │GradeStatistics   │
   │depends on:  │  │orter     │  │porter    │  │Calculator        │
   │             │  │          │  │          │  │                  │
   │- IGrade    │  │Exports   │  │Imports   │  │Calculates:       │
   │  Repository│  │to files  │  │from CSV  │  │- Average         │
   │- IFileExp- │  │          │  │          │  │- Median          │
   │  orter      │  │Uses:     │  │Uses:     │  │- StdDev          │
   │- IGradeImp-│  │- File    │  │- File    │  │- Pass/Fail       │
   │  orter      │  │  Writer  │  │  Reader  │  │- Distribution    │
   │- IGradeSta-│  │- Grade   │  │- Grade   │  │                  │
   │  tisticsCalc│ │  info    │  │  parsing │  │Uses:             │
   │             │  │          │  │          │  │- Grade array     │
   └─────────────┘  └──────────┘  └──────────┘  └──────────────────┘
```

---

## Component Responsibilities

### 1. GradeRepository
**What it does:** Stores and retrieves grades

**Interface:** `IGradeRepository`
```java
Methods:
- addGrade(Grade) → void
- getStudentGrades(int studentId) → Grade[]
- getAllGrades() → Grade[]
- getGradeCount() → int
- isFull() → boolean
```

**Uses:** `Grade[]` (array storage)

**Example:**
```java
IGradeRepository repo = new GradeRepository();
repo.addGrade(new Grade(1, math, 85.0, "B", true, 3.0));
Grade[] grades = repo.getStudentGrades(1);
```

---

### 2. GradeExporter
**What it does:** Exports grades to files (TXT format)

**Interface:** `IFileExporter`
```java
Methods:
- exportGradeReport(Student, IGradeRepository) → void
- getLastExportedFilename() → String
```

**Uses:** `FileWriter`, `IGradeRepository`

**Example:**
```java
IFileExporter exporter = new GradeExporter();
exporter.exportGradeReport(student, repository);
// Creates: GradeReport_1_John_Doe.txt
```

**Easy to extend:**
```java
// Want PDF? Create:
class PDFExporter implements IFileExporter {
    public void exportGradeReport(Student s, IGradeRepository r) {
        // PDF logic here
    }
}
// Just swap in Main: new PDFExporter() instead of new GradeExporter()
```

---

### 3. GradeImporter
**What it does:** Imports grades from CSV files

**Interface:** `IGradeImporter`
```java
Methods:
- importGrades(IGradeRepository, StudentManager) → void
- getSuccessCount() → int
- getFailCount() → int
```

**Uses:** `BufferedReader`, `IGradeRepository`, `StudentManager`

**CSV Format:**
```
StudentID,SubjectName,SubjectCode,SubjectType,Score
1,Math,MATH101,Core,85.5
1,English,ENG101,Core,90.0
```

**Example:**
```java
IGradeImporter importer = new GradeImporter();
importer.importGrades(repository, studentManager);
// User enters: "grades.csv"
// Imports all grades from file
```

---

### 4. GradeStatisticsCalculator
**What it does:** Calculates and displays statistics

**Interface:** `IGradeStatisticsCalculator`
```java
Methods:
- displayStatistics(IGradeRepository, StudentManager) → void
- getAverageScore(IGradeRepository) → double
- getHighestScore(IGradeRepository) → double
- getLowestScore(IGradeRepository) → double
```

**Calculates:**
- Mean, Median, Standard Deviation
- Pass/Fail counts and percentages
- Grade distribution (A, B, C, D, E, F)

**Example:**
```java
IGradeStatisticsCalculator calc = new GradeStatisticsCalculator();
calc.displayStatistics(repository, studentManager);
// Outputs: All statistics to console
```

---

### 5. SubjectFactory
**What it does:** Creates subjects dynamically (follows OCP)

```java
Methods:
- createCoreSubject(int choice) → Subject
- createElectiveSubject(int choice) → Subject
- displayCoreSubjectOptions() → void
- displayElectiveSubjectOptions() → void
```

**Example:**
```java
Subject math = SubjectFactory.createCoreSubject(1);  // Math
Subject music = SubjectFactory.createElectiveSubject(1);  // Music

// To add new subject: just add case in factory, NOT in Main!
```

---

## Class Relationships

### Dependency Flow

```
Main
  ↓
GradeManager (depends on interfaces)
  ├─ IGradeRepository ← GradeRepository
  ├─ IFileExporter ← GradeExporter
  ├─ IGradeImporter ← GradeImporter
  └─ IGradeStatisticsCalculator ← GradeStatisticsCalculator

Data Classes:
  ├─ Student (parent)
  │  ├─ RegularStudent
  │  └─ HonorsStudent
  ├─ Subject (parent)
  │  ├─ CoreSubject
  │  └─ ElectiveSubject
  ├─ Grade (uses Student + Subject)
  └─ StudentManager (manages Students)
```

---

## Interface Segregation Details

### BEFORE: One Large Interface
```
Gradable interface
├─ isPassing()      ✅ All students need
├─ getGradeLevel()  ✅ All students need
└─ calculateGPA()   ❌ Not all grading systems need GPA
```

### AFTER: Segregated Interfaces
```
Gradable (essential grading)
├─ isPassing()
└─ getGradeLevel()

GPACalculable (optional)
└─ calculateGPA()

Student implements: Gradable + GPACalculable
RegularStudent overrides: isPassing(), getGradeLevel(), calculateGPA()
HonorsStudent overrides: isPassing(), getGradeLevel(), calculateGPA()
```

---

## Data Flow Examples

### Recording a Grade

```
User Input: Student ID 1, Math, Score 85
         ↓
   Main.java
         ↓
GradeManager.recordGrade()
         ↓
  Creates Grade object:
  - Gets from Student: getGradeLevel(85) → "B"
  - Gets from Student: isPassing(85) → true
  - Gets from Student: calculateGPA(85) → 3.0
         ↓
  Calls: gradeRepository.addGrade(grade)
         ↓
  GradeRepository stores in array
         ↓
  User sees: "Grade recorded!"
```

### Exporting Grades

```
User Input: Student ID 1
         ↓
   Main.java
         ↓
GradeManager.exportGradeReport(student)
         ↓
  Calls: fileExporter.exportGradeReport(student, repository)
         ↓
  GradeExporter:
  1. Gets student grades: repository.getStudentGrades(1)
  2. Creates FileWriter
  3. Writes header, grades, summary
  4. Closes file
         ↓
  File created: "GradeReport_1_Student_Name.txt"
         ↓
  User sees: "✓ Grade report exported successfully"
```

### Viewing Statistics

```
User Input: "View statistics"
         ↓
   Main.java
         ↓
GradeManager.viewGradeStatistics(studentManager)
         ↓
  Calls: statisticsCalculator.displayStatistics(repository, studentManager)
         ↓
  GradeStatisticsCalculator:
  1. Gets all grades: repository.getAllGrades()
  2. Calculates: avg, median, stddev
  3. Counts: pass/fail
  4. Distributes: A/B/C/D/E/F
  5. Displays results
         ↓
  User sees: Formatted statistics output
```

---

## Testing Architecture

### How Dependency Injection Helps Testing

**Before (Hard to test):**
```java
GradeManager manager = new GradeManager();
// Creates real files, uses real arrays
// Can't control behavior easily
```

**After (Easy to test):**
```java
// Create mock implementations for testing
IGradeRepository mockRepo = new MockGradeRepository();
IFileExporter mockExporter = new MockFileExporter();
IGradeImporter mockImporter = new MockGradeImporter();
IGradeStatisticsCalculator mockCalc = new MockStatisticsCalculator();

// Inject mocks
GradeManager manager = new GradeManager(
    mockRepo, mockExporter, mockImporter, mockCalc
);

// Test without touching real files or database!
manager.recordGrade(student, subject, 85.0);
assertEquals(1, mockRepo.getGradeCount());
```

---

## SOLID Checklist

### ✅ Single Responsibility Principle
| Class | Single Responsibility |
|-------|----------------------|
| GradeRepository | Store grades |
| GradeExporter | Export to file |
| GradeImporter | Import from file |
| GradeStatisticsCalculator | Calculate stats |
| GradeManager | Coordinate components |
| SubjectFactory | Create subjects |

### ✅ Open/Closed Principle
- `SubjectFactory` - open for new subjects without modifying
- Component interfaces - can swap implementations

### ✅ Liskov Substitution Principle
- `RegularStudent` and `HonorsStudent` properly substitute `Student`
- Can use either anywhere `Student` is expected

### ✅ Interface Segregation Principle
- `Gradable` interface is focused
- `GPACalculable` is separate
- Components only depend on interfaces they need

### ✅ Dependency Inversion Principle
- `GradeManager` depends on interfaces, not concrete classes
- Dependencies injected in constructor
- Easy to swap implementations

---

## Extension Examples

### Adding Email Export
```java
// NEW class - no changes to existing code!
public class EmailExporter implements IFileExporter {
    public void exportGradeReport(Student student, IGradeRepository repo) {
        // Send email with grades
    }
}

// In Main.java:
IFileExporter exporter = new EmailExporter();  // Just change this!
```

### Adding Database Storage
```java
// NEW class - no changes to GradeManager!
public class DatabaseRepository implements IGradeRepository {
    public void addGrade(Grade grade) {
        // Store in database
    }
    // ... implement all methods
}

// In Main.java:
IGradeRepository repo = new DatabaseRepository();  // Just change this!
```

### Adding Excel Export
```java
// NEW class
public class ExcelExporter implements IFileExporter {
    public void exportGradeReport(Student student, IGradeRepository repo) {
        // Create Excel file
    }
}

// In Main.java: swap the exporter
```

---

## File Structure

```
src/main/java/org/example/
├─ Main.java                      (Entry point with DI)
├─ StudentManager.java            (Manages students)
├─ Student.java                   (Abstract parent)
├─ RegularStudent.java
├─ HonorsStudent.java
├─ Subject.java                   (Abstract parent)
├─ CoreSubject.java
├─ ElectiveSubject.java
├─ Grade.java                     (Data class)
├─ Gradable.java                  (Interface) ✨
├─ GPACalculable.java             (Interface) ✨
│
├─ IGradeRepository.java          (Interface) ✨
├─ GradeRepository.java           (Implementation) ✨
│
├─ IFileExporter.java             (Interface) ✨
├─ GradeExporter.java             (Implementation) ✨
│
├─ IGradeImporter.java            (Interface) ✨
├─ GradeImporter.java             (Implementation) ✨
│
├─ IGradeStatisticsCalculator.java (Interface) ✨
├─ GradeStatisticsCalculator.java  (Implementation) ✨
│
├─ GradeManager.java              (Refactored Coordinator) ✨
└─ SubjectFactory.java            (Factory) ✨

✨ = New or significantly refactored during SOLID refactoring
```

---

## Summary

The refactored system is:
- **Modular:** Each component is independent
- **Testable:** Easy to mock dependencies
- **Extensible:** Add features without modifying existing code
- **Maintainable:** Each class has one clear purpose
- **Professional:** Follows industry best practices (SOLID)

This is enterprise-level code structure! 🎉
