package org.example;

import java.util.List;

/**
 * Populates the app with demo students and grades for presentation.
 */
public class DemoDataLoader {

    public static void loadSampleData(StudentManager sm, DataStore ds) {
        String[] names = new String[] {"John Doe","Anne O'Neill","Sam Carter","Maria Gomez","Liam Smith","Noah Brown","Olivia Davis","Emma Wilson","Ava Johnson","Sophia Lee","Mason Clark","Lucas Hall","Evelyn Young","Amelia King","Harper Wright"};
        String[] subjects = new String[] {"MAT101","ENG203","CS105","HIST210","BIO110"};
        String[] subjectNames = new String[] {"Mathematics","English","Computer Science","History","Biology"};

        for (int i = 0; i < names.length; i++) {
            Student s;
            if (i % 5 == 4) s = new HonorsStudent(names[i], 17 + (i % 4));
            else s = new RegularStudent(names[i], 17 + (i % 4));

            // Add contact info
            try { s.setEmail(names[i].toLowerCase().replaceAll("[^a-z]","") + "@demo.edu"); } catch (Exception ignored) {}
            try { s.setPhone("555-100-" + String.format("%04d", i+1)); } catch (Exception ignored) {}

            // Add 3 grades with varying scores
            int base = 60 + (i * 3) % 40; // vary base to create distribution
            for (int g = 0; g < 3; g++) {
                int idx = (i + g) % subjects.length;
                double score = Math.max(40, Math.min(99, base + (g * 5) - (i % 7)) );
                Grade grade = new Grade(subjects[idx], subjectNames[idx], score);
                s.addGrade(grade);
            }

            // Add to manager and central store
            sm.addStudent(s);
            ds.addStudent(s);
        }
        System.out.println("Demo data loaded: " + names.length + " students (with sample grades)");
    }
}
