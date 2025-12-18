package org.example;

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class Student {
    private final String id; // STU\d{3}
    private final int studentId; // compatibility numeric id
    private String name;
    private int age; // compatibility
    private String email;
    private String phone;
    private final Deque<Grade> gradeHistory = new LinkedList<>(); // LinkedList for frequent insert/remove
    private final Set<String> courses = new ConcurrentSkipListSet<>();
    private static final java.util.concurrent.atomic.AtomicInteger ID_COUNTER = new java.util.concurrent.atomic.AtomicInteger(0);

    // Compatibility constructor used by legacy callers that construct students by name/age
    public Student(String name, int age) {
        if (!ValidationUtils.isValidName(name)) throw new IllegalArgumentException("Invalid name: " + name + ". " + ValidationUtils.examplesFor("name"));
        this.studentId = ID_COUNTER.incrementAndGet();
        this.id = String.format("STU%03d", this.studentId);
        this.name = name;
        this.age = age;
    }

    // Constructor using explicit student identifier and name
    public Student(String id, String name) {
        if (!ValidationUtils.isValidStudentId(id)) throw new IllegalArgumentException("Invalid student ID: " + id);
        if (!ValidationUtils.isValidName(name)) throw new IllegalArgumentException("Invalid name: " + name + ". " + ValidationUtils.examplesFor("name"));
        this.id = id;
        // parse numeric id if possible
        int parsed = 0;
        try { parsed = Integer.parseInt(id.replaceAll("^\\D+", "")); } catch (Exception e) { parsed = ID_COUNTER.incrementAndGet(); }
        this.studentId = parsed;
        this.name = name;
    }

    public String getId() { return id; }

    // Compatibility numeric id
    public int getStudentId() { return studentId; }

    public int getAge() { return age; }

    public String getName() { return name; }

    public void setName(String name) {
        if (!ValidationUtils.isValidName(name)) throw new IllegalArgumentException("Invalid name: " + name);
        this.name = name;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) {
        if (email != null && !ValidationUtils.isValidEmail(email)) throw new IllegalArgumentException("Invalid email: " + email);
        this.email = email;
    }

    public String getPhone() { return phone; }

    public void setPhone(String phone) {
        if (phone != null && !ValidationUtils.isValidPhone(phone)) throw new IllegalArgumentException("Invalid phone: " + phone);
        this.phone = phone;
    }

    public void addGrade(Grade g) {
        Objects.requireNonNull(g);
        gradeHistory.addLast(g);
        courses.add(g.getCourseCode());
        // update grade's studentId if missing
        if (g.getStudentId() == 0) {
            g.setStudentId(this.studentId);
        }
    }

    public List<Grade> getGradeHistory() {
        return Collections.unmodifiableList(new LinkedList<>(gradeHistory));
    }

    public double computeGPA() {
        if (gradeHistory.isEmpty()) return 0.0;
        double sum = 0.0;
        for (Grade g : gradeHistory) sum += g.getScore();
        return sum / gradeHistory.size();
    }

    // Compatibility hooks used by older code
    public String getStudentType() { return ""; }
    public void displayStudentDetails() { System.out.println("ID: " + getStudentId() + " | Name: " + getName()); }
    public boolean isPassing(double grade) { return grade >= 50; }
    public String getGradeLevel(double grade) { return grade >= 90 ? "A" : grade >= 80 ? "B" : grade >= 70 ? "C" : grade >= 60 ? "D" : grade >= 50 ? "E" : "F"; }
    public double calculateGPA(double grade) { return Math.min(4.0, Math.max(0.0, (grade / 25.0))); }

    public Set<String> getCourses() { return Collections.unmodifiableSet(courses); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student s = (Student) o;
        return id.equals(s.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}

