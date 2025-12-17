package org.example;

// Manages students
public class StudentManager {
    private Student[] students;
    private int count;
    
    public StudentManager() {
        students = new Student[50];
        count = 0;
        
        // Add some default students
        students[count++] = new RegularStudent("Alice", 18);
        students[count++] = new RegularStudent("Bob", 17);
        students[count++] = new RegularStudent("Charlie", 19);
        students[count++] = new HonorsStudent("Diana", 18);
        students[count++] = new HonorsStudent("Edward", 17);
    }
    
    public void addStudent(Student student) {
        if (count < students.length) {
            students[count++] = student;
            System.out.println("Student added! ID: " + student.getStudentId());
        } else {
            System.out.println("Student list is full!");
        }
    }
    
    public void viewAllStudents() {
        System.out.println("\n--- All Students ---");
        for (int i = 0; i < count; i++) {
            students[i].displayStudentDetails();
        }
    }
    
    public Student findStudent(int id) {
        for (int i = 0; i < count; i++) {
            if (students[i].getStudentId() == id) {
                return students[i];
            }
        }
        return null;
    }
    
    // NEW: Search students by name or ID with partial matching
    public void searchStudents(String query) {
        System.out.println("\n--- Search Results for: '" + query + "' ---");
        boolean found = false;
        
        // Try to parse as ID
        int searchId = -1;
        try {
            searchId = Integer.parseInt(query);
        } catch (NumberFormatException e) {
            // Not a number, search by name
        }
        
        for (int i = 0; i < count; i++) {
            boolean match = false;
            
            // Check if ID matches
            if (searchId != -1 && students[i].getStudentId() == searchId) {
                match = true;
            }
            
            // Check if name contains query (case-insensitive partial match)
            if (students[i].getName().toLowerCase().contains(query.toLowerCase())) {
                match = true;
            }
            
            if (match) {
                students[i].displayStudentDetails();
                found = true;
            }
        }
        
        if (!found) {
            System.out.println("No students found matching: " + query);
        }
    }
    
    // NEW: Get all students (for statistics)
    public Student[] getAllStudents() {
        Student[] result = new Student[count];
        for (int i = 0; i < count; i++) {
            result[i] = students[i];
        }
        return result;
    }
    
    public int getStudentCount() {
        return count;
    }
}
