package org.example;

// Subject class
public abstract class Subject {
    private String subjectName;
    private String subjectCode;
    
    public Subject(String subjectName, String subjectCode) {
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
    }
    
    public String getSubjectName() { 
        return subjectName; 
    }
    
    public String getSubjectCode() { 
        return subjectCode; 
    }
    
    public abstract String getSubjectType();
    public abstract void displaySubjectInfo();
}
