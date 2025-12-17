package org.example;

/**
 * OPEN/CLOSED PRINCIPLE (OCP)
 * 
 * This factory creates subjects dynamically.
 * You can add new subjects without modifying Main.java!
 * 
 * Benefits for beginners:
 * - Instead of hard-coding subjects in Main, they're created by factory
 * - To add a new subject, just add it to this factory
 * - Main.java stays clean and simple
 * - Easy to change subject lists without touching UI code
 * 
 * How it works:
 * 1. User selects a subject type and number
 * 2. Factory creates the right subject
 * 3. No hard-coded Subject creation in Main
 */
public class SubjectFactory {
    
    // Define core subjects
    public static final int MATH = 1;
    public static final int ENGLISH = 2;
    public static final int SCIENCE = 3;
    
    // Define elective subjects
    public static final int MUSIC = 1;
    public static final int ART = 2;
    public static final int PE = 3;
    
    /**
     * Create a core subject
     * @param choice the subject choice (1=Math, 2=English, 3=Science)
     * @return the created subject, or null if invalid choice
     */
    public static Subject createCoreSubject(int choice) {
        switch (choice) {
            case MATH:
                return new CoreSubject("Math", "MATH101");
            case ENGLISH:
                return new CoreSubject("English", "ENG101");
            case SCIENCE:
                return new CoreSubject("Science", "SCI101");
            default:
                return null;
        }
    }
    
    /**
     * Create an elective subject
     * @param choice the subject choice (1=Music, 2=Art, 3=PE)
     * @return the created subject, or null if invalid choice
     */
    public static Subject createElectiveSubject(int choice) {
        switch (choice) {
            case MUSIC:
                return new ElectiveSubject("Music", "MUS101");
            case ART:
                return new ElectiveSubject("Art", "ART101");
            case PE:
                return new ElectiveSubject("PE", "PE101");
            default:
                return null;
        }
    }
    
    /**
     * Get list of available core subjects
     * This can be used to display choices to user
     */
    public static void displayCoreSubjectOptions() {
        System.out.println("1. Math  2. English  3. Science");
    }
    
    /**
     * Get list of available elective subjects
     * This can be used to display choices to user
     */
    public static void displayElectiveSubjectOptions() {
        System.out.println("1. Music  2. Art  3. PE");
    }
}
