package org.example;

/**
 * INTERFACE SEGREGATION PRINCIPLE (ISP)
 * 
 * This interface is separated from Gradable because not all grading systems
 * need GPA calculation. By splitting the interface, classes can implement
 * only the methods they need.
 * 
 * Benefits:
 * - Classes are not forced to implement unused methods
 * - Easier to extend or change GPA calculation independently
 * - Cleaner contracts between classes
 */
public interface GPACalculable {
    /**
     * Calculate GPA (Grade Point Average) for a given score
     * @param score the numerical score (0-100)
     * @return the GPA value
     */
    double calculateGPA(double score);
}
