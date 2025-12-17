package org.example;

import java.util.Arrays;

/**
 * SINGLE RESPONSIBILITY PRINCIPLE (SRP)
 * 
 * This class has ONE job: calculate and display grade statistics.
 * It doesn't manage grades, export files, or import data.
 * 
 * Benefits for beginners:
 * - If you want to add new statistics (median, mode, percentile), add them here
 * - All statistics logic is in one place
 * - Easy to modify how statistics are displayed
 */
public class GradeStatisticsCalculator implements IGradeStatisticsCalculator {
    
    @Override
    public void displayStatistics(IGradeRepository repository, StudentManager studentManager) {
        Grade[] allGrades = repository.getAllGrades();
        
        if (allGrades.length == 0) {
            System.out.println("\nNo grades recorded yet!");
            return;
        }
        
        System.out.println("\n========================================");
        System.out.println("       GRADE STATISTICS");
        System.out.println("========================================\n");
        
        // Collect all scores
        double[] allScores = new double[allGrades.length];
        for (int i = 0; i < allGrades.length; i++) {
            allScores[i] = allGrades[i].getScore();
        }
        
        // Sort scores for median calculation
        Arrays.sort(allScores);
        
        // Calculate statistics
        double sum = 0;
        double highest = allScores[allScores.length - 1];
        double lowest = allScores[0];
        
        for (double score : allScores) {
            sum += score;
        }
        double mean = sum / allScores.length;
        
        // Calculate median
        double median;
        if (allScores.length % 2 == 0) {
            median = (allScores[allScores.length/2 - 1] + allScores[allScores.length/2]) / 2.0;
        } else {
            median = allScores[allScores.length/2];
        }
        
        // Calculate standard deviation
        double sumSquaredDiff = 0;
        for (double score : allScores) {
            double diff = score - mean;
            sumSquaredDiff += diff * diff;
        }
        double stdDev = Math.sqrt(sumSquaredDiff / allScores.length);
        
        // Count passing/failing grades
        int passing = 0;
        int failing = 0;
        for (Grade grade : allGrades) {
            if (grade.isPassing()) {
                passing++;
            } else {
                failing++;
            }
        }
        
        // Display statistics
        System.out.println("Total Grades Recorded: " + allGrades.length);
        System.out.println("Total Students: " + studentManager.getStudentCount());
        System.out.println("\nScore Statistics:");
        System.out.println("  Highest Score: " + String.format("%.2f", highest));
        System.out.println("  Lowest Score: " + String.format("%.2f", lowest));
        System.out.println("  Mean (Average): " + String.format("%.2f", mean));
        System.out.println("  Median: " + String.format("%.2f", median));
        System.out.println("  Standard Deviation: " + String.format("%.2f", stdDev));
        System.out.println("\nPass/Fail Statistics:");
        System.out.println("  Passing: " + passing + " (" + String.format("%.1f", (passing * 100.0 / allGrades.length)) + "%)");
        System.out.println("  Failing: " + failing + " (" + String.format("%.1f", (failing * 100.0 / allGrades.length)) + "%)");
        
        // Grade distribution
        System.out.println("\nGrade Distribution:");
        int[] gradeDistribution = new int[6]; // A, B, C, D, E, F
        for (Grade grade : allGrades) {
            String gradeLevel = grade.getGradeLevel();
            if (gradeLevel.startsWith("A")) gradeDistribution[0]++;
            else if (gradeLevel.startsWith("B")) gradeDistribution[1]++;
            else if (gradeLevel.startsWith("C")) gradeDistribution[2]++;
            else if (gradeLevel.startsWith("D")) gradeDistribution[3]++;
            else if (gradeLevel.startsWith("E")) gradeDistribution[4]++;
            else if (gradeLevel.startsWith("F")) gradeDistribution[5]++;
        }
        
        System.out.println("  A: " + gradeDistribution[0] + " (" + String.format("%.1f", (gradeDistribution[0] * 100.0 / allGrades.length)) + "%)");
        System.out.println("  B: " + gradeDistribution[1] + " (" + String.format("%.1f", (gradeDistribution[1] * 100.0 / allGrades.length)) + "%)");
        System.out.println("  C: " + gradeDistribution[2] + " (" + String.format("%.1f", (gradeDistribution[2] * 100.0 / allGrades.length)) + "%)");
        System.out.println("  D: " + gradeDistribution[3] + " (" + String.format("%.1f", (gradeDistribution[3] * 100.0 / allGrades.length)) + "%)");
        System.out.println("  E: " + gradeDistribution[4] + " (" + String.format("%.1f", (gradeDistribution[4] * 100.0 / allGrades.length)) + "%)");
        System.out.println("  F: " + gradeDistribution[5] + " (" + String.format("%.1f", (gradeDistribution[5] * 100.0 / allGrades.length)) + "%)");
        
        System.out.println("\n========================================");
    }
    
    @Override
    public double getAverageScore(IGradeRepository repository) {
        Grade[] allGrades = repository.getAllGrades();
        if (allGrades.length == 0) return 0;
        
        double sum = 0;
        for (Grade grade : allGrades) {
            sum += grade.getScore();
        }
        return sum / allGrades.length;
    }
    
    @Override
    public double getHighestScore(IGradeRepository repository) {
        Grade[] allGrades = repository.getAllGrades();
        if (allGrades.length == 0) return 0;
        
        double highest = allGrades[0].getScore();
        for (Grade grade : allGrades) {
            if (grade.getScore() > highest) {
                highest = grade.getScore();
            }
        }
        return highest;
    }
    
    @Override
    public double getLowestScore(IGradeRepository repository) {
        Grade[] allGrades = repository.getAllGrades();
        if (allGrades.length == 0) return 0;
        
        double lowest = allGrades[0].getScore();
        for (Grade grade : allGrades) {
            if (grade.getScore() < lowest) {
                lowest = grade.getScore();
            }
        }
        return lowest;
    }
}
