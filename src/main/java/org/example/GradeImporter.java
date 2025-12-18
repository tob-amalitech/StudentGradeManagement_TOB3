package org.example;

import java.io.*;

/**
 * Responsible for importing grades from CSV files into the grade repository.
 *
 * Responsibilities include parsing CSV lines, validating fields, locating
 * students, constructing Grade objects, and recording import success/failure
 * counts. The class focuses solely on import concerns.
 */
public class GradeImporter implements IGradeImporter {
    private int successCount;
    private int failCount;
    
    public GradeImporter() {
        this.successCount = 0;
        this.failCount = 0;
    }
    
    @Override
    public void importGrades(IGradeRepository repository, StudentManager studentManager) {
        System.out.print("\nEnter CSV filename (e.g., grades.csv): ");
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        String filename = scanner.nextLine();
        
        successCount = 0;
        failCount = 0;
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            int lineNumber = 0;
            
            System.out.println("\nImporting grades from: " + filename);
            System.out.println("Expected format: StudentID,SubjectName,SubjectCode,SubjectType,Score");
            System.out.println("Example: 1,Math,MATH101,Core,85.5\n");
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                // Skip header line if it exists
                if (lineNumber == 1 && line.toLowerCase().contains("student")) {
                    continue;
                }
                
                String[] parts = line.split(",");
                
                if (parts.length != 5) {
                    System.out.println("Line " + lineNumber + " skipped - invalid format: " + line);
                    failCount++;
                    continue;
                }
                
                try {
                    int studentId = Integer.parseInt(parts[0].trim());
                    String subjectName = parts[1].trim();
                    String subjectCode = parts[2].trim();
                    String subjectType = parts[3].trim();
                    double score = Double.parseDouble(parts[4].trim());
                    
                    // Find student
                    Student student = studentManager.findStudent(studentId);
                    if (student == null) {
                        System.out.println("Line " + lineNumber + " skipped - student ID " + studentId + " not found");
                        failCount++;
                        continue;
                    }
                    
                    // Create subject
                    Subject subject;
                    if (subjectType.equalsIgnoreCase("Core")) {
                        subject = new CoreSubject(subjectName, subjectCode);
                    } else if (subjectType.equalsIgnoreCase("Elective")) {
                        subject = new ElectiveSubject(subjectName, subjectCode);
                    } else {
                        System.out.println("Line " + lineNumber + " skipped - invalid subject type: " + subjectType);
                        failCount++;
                        continue;
                    }
                    
                    // Validate score
                    if (score < 0 || score > 100) {
                        System.out.println("Line " + lineNumber + " skipped - invalid score: " + score);
                        failCount++;
                        continue;
                    }
                    
                    // Create and store grade
                    String level = student.getGradeLevel(score);
                    boolean pass = student.isPassing(score);
                    double gpa = student.calculateGPA(score);
                    
                    Grade grade = new Grade(studentId, subject, score, level, pass, gpa);
                    repository.addGrade(grade);
                    successCount++;
                    
                } catch (NumberFormatException e) {
                    System.out.println("Line " + lineNumber + " skipped - invalid number format: " + line);
                    failCount++;
                }
            }
            
            reader.close();
            
            System.out.println("\n========================================");
            System.out.println("Import Complete!");
            System.out.println("Successfully imported: " + successCount + " grades");
            System.out.println("Failed: " + failCount + " records");
            System.out.println("========================================");
            
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found - " + filename);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
    
    @Override
    public int getSuccessCount() {
        return successCount;
    }
    
    @Override
    public int getFailCount() {
        return failCount;
    }
}
