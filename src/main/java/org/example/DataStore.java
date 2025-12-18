package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central in-memory data store. Thread-safe student lookup and synchronized ranking updates.
 */
public class DataStore {
    private final ConcurrentHashMap<String, Student> studentMap = new ConcurrentHashMap<>(); // O(1) lookup
    private final NavigableMap<Double, List<Student>> gpaRankings = new TreeMap<>(Comparator.reverseOrder()); // sorted by GPA desc
    private final Object rankingLock = new Object();

    public void addStudent(Student s) {
        studentMap.put(s.getId(), s);
        updateRankingFor(s);
    }

    public Student getStudent(String id) {
        return studentMap.get(id);
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(studentMap.values());
    }

    public void updateRankingFor(Student s) {
        synchronized (rankingLock) {
            // remove from old entry if present
            gpaRankings.values().forEach(list -> list.remove(s));
            double gpa = s.computeGPA();
            gpaRankings.computeIfAbsent(round(gpa), k -> new ArrayList<>()).add(s);
        }
    }

    public Map<Double, List<Student>> getGpaRankingsSnapshot() {
        synchronized (rankingLock) {
            return Collections.unmodifiableMap(new TreeMap<>(gpaRankings));
        }
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0; // keep two decimals for grouping
    }
}
