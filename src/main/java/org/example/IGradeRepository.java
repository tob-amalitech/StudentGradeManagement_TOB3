package org.example;

/**
 * DEPENDENCY INVERSION PRINCIPLE (DIP)
 * 
 * This interface defines the contract for storing and retrieving grades.
 * High-level modules depend on this abstraction, not on concrete implementations.
 * 
 * Benefits:
 * - Decouples GradeManager from storage implementation
 * - Easy to change storage method (file, database, cloud) without changing GradeManager
 * - Easy to test with mock implementations
 * 
 * How it helps beginners:
 * - Instead of GradeManager knowing HOW to store grades, it just calls methods
 * - You can swap the storage mechanism without touching GradeManager code
 */
public interface IGradeRepository {
    
    /**
     * Add a grade to the repository
     * @param grade the grade to store
     */
    void addGrade(Grade grade);
    
    /**
     * Get all grades for a specific student
     * @param studentId the student's ID
     * @return array of grades for that student
     */
    Grade[] getStudentGrades(int studentId);
    
    /**
     * Get all grades in the system
     * @return array of all grades
     */
    Grade[] getAllGrades();
    
    /**
     * Get the total number of grades stored
     * @return total grade count
     */
    int getGradeCount();
    
    /**
     * Check if repository is full
     * @return true if no more space for grades
     */
    boolean isFull();
}
