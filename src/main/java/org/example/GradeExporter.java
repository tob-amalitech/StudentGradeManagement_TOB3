package org.example;

import java.io.*;

/**
 * Handles exporting a student's grade report to a file (text format).
 *
 * Responsible for formatting the report content and writing it to disk.
 */
public class GradeExporter implements IFileExporter {
    private String lastExportedFilename;
    
    @Override
    public void exportGradeReport(Student student, IGradeRepository repository) {
        // Note: This is a simple text exporter intended for demos.
        // For production-quality reports consider using a templating
        // library or a PDF generation library for richer formatting.
        String filename = "GradeReport_" + student.getStudentId() + "_" + 
                         student.getName().replace(" ", "_") + ".txt";
        this.lastExportedFilename = filename;
        
        try {
            FileWriter writer = new FileWriter(filename);
            
            // Write header
            writer.write("========================================\n");
            writer.write("       GRADE REPORT\n");
            writer.write("========================================\n\n");
            writer.write("Student ID: " + student.getStudentId() + "\n");
            writer.write("Name: " + student.getName() + "\n");
            writer.write("Age: " + student.getAge() + "\n");
            writer.write("Type: " + student.getStudentType() + "\n");
            writer.write("\n========================================\n");
            writer.write("       GRADES\n");
            writer.write("========================================\n\n");
            
            // Get student's grades from repository
            Grade[] studentGrades = repository.getStudentGrades(student.getStudentId());
            
            int gradeCount = 0;
            double total = 0;
            double totalGPA = 0;
            
            // Write each grade
            for (Grade grade : studentGrades) {
                writer.write(String.format("%-20s: %6.2f (%2s) - GPA: %.2f\n",
                           grade.getSubject().getSubjectName(),
                           grade.getScore(),
                           grade.getGradeLevel(),
                           grade.getGpa()));
                total += grade.getScore();
                totalGPA += grade.getGpa();
                gradeCount++;
            }
            
            // Write summary
            if (gradeCount > 0) {
                double average = total / gradeCount;
                double avgGPA = totalGPA / gradeCount;
                writer.write("\n========================================\n");
                writer.write("       SUMMARY\n");
                writer.write("========================================\n\n");
                writer.write("Total Subjects: " + gradeCount + "\n");
                writer.write("Average Score: " + String.format("%.2f", average) + "\n");
                writer.write("Average GPA: " + String.format("%.2f", avgGPA) + "\n");
            } else {
                writer.write("\nNo grades recorded yet.\n");
            }
            
            writer.write("\n========================================\n");
            writer.write("End of Report\n");
            writer.write("========================================\n");
            
            writer.close();
            System.out.println("\n✓ Grade report exported successfully to: " + filename);
            
        } catch (IOException e) {
            System.out.println("Error exporting grade report: " + e.getMessage());
        }
    }
    
    @Override
    public String getLastExportedFilename() {
        return lastExportedFilename;
    }
}
