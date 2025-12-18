package org.example;

// Regular student
public class RegularStudent extends Student {
    
    public RegularStudent(String name, int age) {
        super(name, age);
    }
    
    @Override
    public String getStudentType() {
        return "Regular";
    }
    
    @Override
    public void displayStudentDetails() {
        System.out.println("ID: " + getStudentId() + " | Name: " + getName() + " | Age: " + getAge() + " | Type: " + getStudentType());
    }
    
    @Override
    public boolean isPassing(double grade) {
        return grade >= 50;
    }
    
    @Override
    public String getGradeLevel(double grade) {
        if (grade >= 90) return "A";
        else if (grade >= 80) return "B";
        else if (grade >= 70) return "C";
        else if (grade >= 60) return "D";
        else if (grade >= 50) return "E";
        else return "F";
    }
    
    /**
     * Calculate GPA on a 4.0 scale for regular students.
     */
    @Override
    public double calculateGPA(double grade) {
        if (grade >= 90) return 4.0;
        else if (grade >= 80) return 3.0;
        else if (grade >= 70) return 2.0;
        else if (grade >= 60) return 1.0;
        else if (grade >= 50) return 0.5;
        else return 0.0;
    }
}
