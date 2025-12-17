package org.example;

/**
 * DEPENDENCY INVERSION PRINCIPLE (DIP)
 * 
 * This interface defines how to calculate statistics on grades.
 * GradeManager depends on this interface, not on calculation details.
 * 
 * Benefits:
 * - Easy to change how statistics are calculated
 * - Can add new statistics methods without modifying GradeManager
 * - Separates statistics logic from grade management logic
 */
public interface IGradeStatisticsCalculator {
    
    /**
     * View and display all statistics
     * @param repository the grades to analyze
     * @param studentManager the student data
     */
    void displayStatistics(IGradeRepository repository, StudentManager studentManager);
    
    /**
     * Get the average score of all grades
     * @param repository the grades to analyze
     * @return the average score
     */
    double getAverageScore(IGradeRepository repository);
    
    /**
     * Get the highest score recorded
     * @param repository the grades to analyze
     * @return the highest score
     */
    double getHighestScore(IGradeRepository repository);
    
    /**
     * Get the lowest score recorded
     * @param repository the grades to analyze
     * @return the lowest score
     */
    double getLowestScore(IGradeRepository repository);
}
