package org.example;

// Core subject
public class CoreSubject extends Subject {
    
    public CoreSubject(String subjectName, String subjectCode) {
        super(subjectName, subjectCode);
    }
    
    @Override
    public String getSubjectType() {
        return "Core";
    }
    
    @Override
    public void displaySubjectInfo() {
        System.out.println(getSubjectName() + " [" + getSubjectCode() + "] - Core Subject");
    }
}
