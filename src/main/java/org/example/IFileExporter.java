package org.example;

/**
 * DEPENDENCY INVERSION PRINCIPLE (DIP)
 * 
 * This interface defines how to export grade reports.
 * GradeManager depends on this interface, not on FileWriter directly.
 * 
 * Benefits:
 * - Easy to add new export formats (PDF, Excel, etc.) without changing GradeManager
 * - Can test export functionality with mock exporters
 * - Separates "what to export" from "how to export it"
 */
public interface IFileExporter {
    
    /**
     * Export a student's grade report
     * @param student the student whose grades to export
     * @param repository the grade repository to read from
     */
    void exportGradeReport(Student student, IGradeRepository repository);
    
    /**
     * Get the name of the last exported file
     * @return filename that was created
     */
    String getLastExportedFilename();
}
