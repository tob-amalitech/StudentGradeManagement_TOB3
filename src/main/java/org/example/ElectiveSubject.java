package org.example;

// Elective subject
public class ElectiveSubject extends Subject {
    
    public ElectiveSubject(String subjectName, String subjectCode) {
        super(subjectName, subjectCode);
    }
    
    @Override
    public String getSubjectType() {
        return "Elective";
    }
    
    @Override
    public void displaySubjectInfo() {
        System.out.println(getSubjectName() + " [" + getSubjectCode() + "] - Elective Subject");
    }
}
