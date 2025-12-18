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
        System.out.println("\n\u001B[1;36m--- All Students ---\u001B[0m");
        System.out.printf("% -6s | %-20s | %-3s | %-8s | %-6s\n", "ID", "Name", "Age", "Type", "GPA");
        System.out.println("---------------------------------------------------------------");
        for (int i = 0; i < count; i++) {
            Student s = students[i];
            System.out.printf("% -6s | %-20s | %-3d | %-8s | %5.2f\n", s.getId(), s.getName(), s.getAge(), s.getStudentType(), s.computeGPA());
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
    
    // Search students by name or ID with partial matching (case-insensitive)
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
    
    // Return array of all students (snapshot) for statistics and reporting
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
