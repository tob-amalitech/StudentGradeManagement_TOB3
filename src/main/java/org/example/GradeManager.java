package org.example;

/**
 * Coordinator for grade-related operations.
 *
 * Delegates storage, import/export, and statistics calculation to injected
 * collaborators. This class orchestrates high-level use cases such as
 * recording grades and generating student reports.
 */
public class GradeManager {
    // Dependencies injected through constructor (Dependency Injection)
    private IGradeRepository gradeRepository;
    private IFileExporter fileExporter;
    private IGradeImporter gradeImporter;
    private IGradeStatisticsCalculator statisticsCalculator;
    
    /**
     * Constructor with dependency injection
     * This allows GradeManager to work with different implementations
     */
    public GradeManager(IGradeRepository gradeRepository,
                       IFileExporter fileExporter,
                       IGradeImporter gradeImporter,
                       IGradeStatisticsCalculator statisticsCalculator) {
        this.gradeRepository = gradeRepository;
        this.fileExporter = fileExporter;
        this.gradeImporter = gradeImporter;
        this.statisticsCalculator = statisticsCalculator;
    }
    
    /**
     * Record a grade (delegate to repository)
     */
    public void recordGrade(Student student, Subject subject, double score) {
        String level = student.getGradeLevel(score);
        boolean pass = student.isPassing(score);
        double gpa = student.calculateGPA(score);
        
        if (!gradeRepository.isFull()) {
            Grade grade = new Grade(student.getStudentId(), subject, score, level, pass, gpa);
            gradeRepository.addGrade(grade);
            
            System.out.println("\nGrade recorded!");
            System.out.println("Student: " + student.getName());
            System.out.println("Subject: " + subject.getSubjectName());
            System.out.println("Score: " + score);
            System.out.println("Grade: " + level);
            System.out.println("GPA: " + String.format("%.2f", gpa));
            System.out.println("Status: " + (pass ? "PASS" : "FAIL"));
        } else {
            System.out.println("Grade storage is full!");
        }
    }
    
    /**
     * View grade report for a student
     */
    public void viewGradeReport(Student student) {
        System.out.println("\n--- Grade Report ---");
        student.displayStudentDetails();
        System.out.println("\nGrades:");
        
        Grade[] studentGrades = gradeRepository.getStudentGrades(student.getStudentId());
        
        if (studentGrades.length == 0) {
            System.out.println("No grades yet.");
            return;
        }
        
        int gradeCount = 0;
        double total = 0;
        double totalGPA = 0;
        
        for (Grade grade : studentGrades) {
            System.out.println(grade.getSubject().getSubjectName() + ": " + 
                             grade.getScore() + " (" + grade.getGradeLevel() + ") - GPA: " + 
                             String.format("%.2f", grade.getGpa()));
            total += grade.getScore();
            totalGPA += grade.getGpa();
            gradeCount++;
        }
        
        double average = total / gradeCount;
        double avgGPA = totalGPA / gradeCount;
        System.out.println("\nAverage Score: " + String.format("%.2f", average));
        System.out.println("Average GPA: " + String.format("%.2f", avgGPA));
    }
    
    /**
     * Export grade report (delegate to exporter)
     */
    public void exportGradeReport(Student student) {
        fileExporter.exportGradeReport(student, gradeRepository);
    }
    
    /**
     * Bulk import grades (delegate to importer)
     */
    public void bulkImportGrades(StudentManager studentManager) {
        gradeImporter.importGrades(gradeRepository, studentManager);
    }
    
    /**
     * View grade statistics (delegate to calculator)
     */
    public void viewGradeStatistics(StudentManager studentManager) {
        statisticsCalculator.displayStatistics(gradeRepository, studentManager);
    }
}

