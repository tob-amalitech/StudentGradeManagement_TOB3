package org.example;

// Grade class
public class Grade {
    private static int counter = 0;
    private int gradeId;
    private int studentId;
    private Subject subject;
    private double score;
    private String gradeLevel;
    private boolean passing;
    private double gpa; // NEW: Store GPA value
    
    public Grade(int studentId, Subject subject, double score, String gradeLevel, boolean passing, double gpa) {
        this.gradeId = ++counter;
        this.studentId = studentId;
        this.subject = subject;
        this.score = score;
        this.gradeLevel = gradeLevel;
        this.passing = passing;
        this.gpa = gpa;
    }
    
    public int getGradeId() {
        return gradeId;
    }
    
    public int getStudentId() { 
        return studentId; 
    }
    
    public Subject getSubject() { 
        return subject; 
    }
    
    public double getScore() { 
        return score; 
    }
    
    public String getGradeLevel() { 
        return gradeLevel; 
    }
    
    public boolean isPassing() { 
        return passing; 
    }
    
    // NEW: Get GPA value
    public double getGpa() {
        return gpa;
    }
}
