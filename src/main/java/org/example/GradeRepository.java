package org.example;

/**
 * SINGLE RESPONSIBILITY PRINCIPLE (SRP)
 * 
 * This class has ONE job: store and retrieve grades.
 * It doesn't worry about exporting files, importing CSVs, or calculating statistics.
 * 
 * Benefits for beginners:
 * - Easier to understand what this class does
 * - Easier to test (just test storage operations)
 * - If you need to change how grades are stored, you only change this class
 * - Can reuse this class in different projects
 */
public class GradeRepository implements IGradeRepository {
    private Grade[] grades;
    private int count;
    
    public GradeRepository() {
        grades = new Grade[100];  // Maximum 100 grades
        count = 0;
    }
    
    /**
     * Add a grade to storage
     */
    @Override
    public void addGrade(Grade grade) {
        if (count < grades.length) {
            grades[count++] = grade;
        }
    }
    
    /**
     * Get all grades for a student
     */
    @Override
    public Grade[] getStudentGrades(int studentId) {
        // Count how many grades this student has
        int studentGradeCount = 0;
        for (int i = 0; i < count; i++) {
            if (grades[i].getStudentId() == studentId) {
                studentGradeCount++;
            }
        }
        
        // Create array with exact size
        Grade[] studentGrades = new Grade[studentGradeCount];
        int index = 0;
        
        // Fill array with student's grades
        for (int i = 0; i < count; i++) {
            if (grades[i].getStudentId() == studentId) {
                studentGrades[index++] = grades[i];
            }
        }
        
        return studentGrades;
    }
    
    /**
     * Get all grades stored
     */
    @Override
    public Grade[] getAllGrades() {
        // Create array with exact size (only populated grades)
        Grade[] allGrades = new Grade[count];
        for (int i = 0; i < count; i++) {
            allGrades[i] = grades[i];
        }
        return allGrades;
    }
    
    /**
     * Get total number of grades
     */
    @Override
    public int getGradeCount() {
        return count;
    }
    
    /**
     * Check if storage is full
     */
    @Override
    public boolean isFull() {
        return count >= grades.length;
    }
}
