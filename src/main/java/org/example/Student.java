package org.example;

/**
 * LISKOV SUBSTITUTION PRINCIPLE (LSP)
 * 
 * This abstract class serves as a contract that all student types must follow.
 * Both RegularStudent and HonorsStudent can substitute Student in any context.
 */
public abstract class Student implements Gradable, GPACalculable {
    private static int counter = 0;
    private int studentId;
    private String name;
    private int age;
    
    public Student(String name, int age) {
        this.studentId = ++counter;
        this.name = name;
        this.age = age;
    }
    
    public int getStudentId() { 
        return studentId; 
    }
    
    public String getName() { 
        return name; 
    }
    
    public int getAge() { 
        return age; 
    }
    
    public abstract String getStudentType();
    public abstract void displayStudentDetails();
}
