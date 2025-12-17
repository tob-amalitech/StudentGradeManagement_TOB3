package org.example;

/**
 * DEPENDENCY INVERSION PRINCIPLE (DIP)
 * 
 * This interface defines how to import grades from files.
 * GradeManager depends on this interface, not on file reading details.
 * 
 * Benefits:
 * - Easy to add support for different import formats
 * - Can test import with mock data without using real files
 * - Separates "what to import" from "how to parse it"
 */
public interface IGradeImporter {
    
    /**
     * Import grades from a file or source
     * @param repository where to store the imported grades
     * @param studentManager to validate students exist
     */
    void importGrades(IGradeRepository repository, StudentManager studentManager);
    
    /**
     * Get the number of grades successfully imported
     * @return success count
     */
    int getSuccessCount();
    
    /**
     * Get the number of grades that failed to import
     * @return failure count
     */
    int getFailCount();
}
