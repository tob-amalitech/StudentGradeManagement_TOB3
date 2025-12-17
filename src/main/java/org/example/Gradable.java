package org.example;

/**
 * INTERFACE SEGREGATION PRINCIPLE (ISP)
 * 
 * This interface contains only the essential grading methods that all
 * students need. GPA calculation has been moved to GPACalculable interface
 * so classes can implement only what they need.
 * 
 * Benefits:
 * - More focused interface (single responsibility)
 * - Easier to understand what methods do
 * - Classes only implement methods they actually use
 */
public interface Gradable {
    /**
     * Check if a grade is passing
     * @param grade the numerical grade (0-100)
     * @return true if passing, false otherwise
     */
    boolean isPassing(double grade);
    
    /**
     * Get the letter grade for a numerical score
     * @param grade the numerical grade (0-100)
     * @return letter grade (A, B, C, D, E, F)
     */
    String getGradeLevel(double grade);
}
