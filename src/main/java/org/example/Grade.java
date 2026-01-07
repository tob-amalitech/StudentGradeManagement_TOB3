package org.example;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a single grade entry for a student.
 *
 * Notes for the lab review:
 * - This is intentionally immutable for primary fields (`courseCode`,
 *   `courseName`, `score`, `timestamp`) to make read paths thread-safe.
 * - Compatibility fields (studentId, subject, gradeLevel, passing, gpa)
 *   are present to support older code paths and exporters; prefer the main
 *   constructor when creating new Grade records.
 * - In a production system you would typically store these in a type-safe
 *   collection keyed by student (e.g., `Map<String, List<Grade>>`) and
 *   persist to a database. The design here demonstrates serialization,
 *   simple immutability, and compatibility with legacy constructors.
 */
public class Grade implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String courseCode;
    private final String courseName;
    private final double score;
    private final LocalDateTime timestamp;
    // Compatibility fields
    private int studentId; // optional numeric id
    private Subject subject; // optional subject object
    private String gradeLevel;
    private boolean passing;
    private double gpa;

    public Grade(String courseCode, String courseName, double score) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.score = score;
        this.timestamp = LocalDateTime.now();
    }

    // Legacy-compatible constructor
    public Grade(int studentId, Subject subject, double score, String gradeLevel, boolean passing, double gpa) {
        this.courseCode = subject == null ? "" : subject.getSubjectCode();
        this.courseName = subject == null ? "" : subject.getSubjectName();
        this.score = score;
        this.timestamp = LocalDateTime.now();
        this.studentId = studentId;
        this.subject = subject;
        this.gradeLevel = gradeLevel;
        this.passing = passing;
        this.gpa = gpa;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public double getScore() {
        return score;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Compatibility getters used by older code
    public int getStudentId() { return studentId; }
    public Subject getSubject() { return subject; }
    public String getGradeLevel() { return gradeLevel; }
    public boolean isPassing() { return passing; }
    public double getGpa() { return gpa; }
    public void setStudentId(int id) { this.studentId = id; }
}

