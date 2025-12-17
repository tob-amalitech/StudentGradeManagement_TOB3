package org.example;

// Honors student
public class HonorsStudent extends Student {
    
    public HonorsStudent(String name, int age) {
        super(name, age);
    }
    
    @Override
    public String getStudentType() {
        return "Honors";
    }
    
    @Override
    public void displayStudentDetails() {
        System.out.println("ID: " + getStudentId() + " | Name: " + getName() + " | Age: " + getAge() + " | Type: " + getStudentType());
    }
    
    @Override
    public boolean isPassing(double grade) {
        return grade >= 60;
    }
    
    @Override
    public String getGradeLevel(double grade) {
        if (grade >= 90) return "A+";
        else if (grade >= 80) return "A";
        else if (grade >= 70) return "B";
        else if (grade >= 60) return "C";
        else return "F";
    }
    
    // NEW: Calculate GPA on 4.0 scale for Honors students (weighted higher)
    @Override
    public double calculateGPA(double grade) {
        if (grade >= 90) return 4.3;
        else if (grade >= 80) return 4.0;
        else if (grade >= 70) return 3.0;
        else if (grade >= 60) return 2.0;
        else return 0.0;
    }
}
