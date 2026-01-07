package org.example;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * FileService: CSV streaming import, simple JSON export, and binary serialization.
 */
public class FileService {

    public static final Path DATA_DIR = Paths.get("data");
    public static final Path CSV_DIR = DATA_DIR.resolve("csv");
    public static final Path JSON_DIR = DATA_DIR.resolve("json");
    public static final Path BINARY_DIR = DATA_DIR.resolve("binary");

    public FileService() throws IOException {
        // ensure directories exist
        Files.createDirectories(CSV_DIR);
        Files.createDirectories(JSON_DIR);
        Files.createDirectories(BINARY_DIR);
    }

    /**
     * Stream-import students and grades from a CSV file. Expected CSV header:
     * id,name,email,phone,courseCode,courseName,score
        *
        * Implementation notes for demo/lab:
        * - Uses `Files.lines(...)` to stream the file using NIO.2 and avoid
        *   loading the entire file into memory (suitable for large files).
        * - Validation is delegated to `ValidationUtils` so parsing and
        *   validation responsibilities are separated and reusable.
        * - Error collection is returned to the caller so the UI can display
        *   which records failed and why.
     */
    public List<String> importStudentsFromCsv(Path csvPath, DataStore store) throws IOException {
        List<String> errors = new ArrayList<>();
        try (Stream<String> lines = Files.lines(csvPath, StandardCharsets.UTF_8)) {
            lines.skip(1).forEachOrdered(line -> {
                try {
                    String[] parts = line.split(",", -1);
                    if (parts.length < 7) {
                        errors.add("Invalid CSV line: " + line);
                        return;
                    }
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String email = parts[2].trim();
                    String phone = parts[3].trim();
                    String courseCode = parts[4].trim();
                    String courseName = parts[5].trim();
                    String scoreS = parts[6].trim();

                    if (!ValidationUtils.isValidStudentId(id)) {
                        errors.add(id + ": invalid student id");
                        return;
                    }
                    if (!ValidationUtils.isValidName(name)) {
                        errors.add(id + ": invalid name");
                        return;
                    }

                    Student s = store.getStudent(id);
                    if (s == null) {
                        s = new Student(id, name);
                        s.setEmail(email.isEmpty() ? null : email);
                        s.setPhone(phone.isEmpty() ? null : phone);
                        store.addStudent(s);
                    }

                    if (!ValidationUtils.isValidCourseCode(courseCode) || !ValidationUtils.isValidGrade(scoreS)) {
                        errors.add(id + ": invalid course or grade -> " + courseCode + "," + scoreS);
                        return;
                    }

                    double score = Double.parseDouble(scoreS);
                    Grade g = new Grade(courseCode, courseName, score);
                    s.addGrade(g);
                    store.updateRankingFor(s);
                } catch (Exception ex) {
                    errors.add("Exception parsing line: " + line + " -> " + ex.getMessage());
                }
            });
        }
        return errors;
    }

    /**
     * Export all students to a simple JSON array file.
     */
    public Path exportStudentsToJson(DataStore store, String fileName) throws IOException {
        Path out = JSON_DIR.resolve(fileName.endsWith(".json") ? fileName : (fileName + ".json"));
        try (BufferedWriter w = Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {
            w.write("[");
            boolean first = true;
            DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            for (Student s : store.getAllStudents()) {
                if (!first) w.write(",\n");
                first = false;
                StringBuilder sb = new StringBuilder();
                sb.append("  {");
                sb.append("\"id\":\"").append(s.getId()).append('\"');
                sb.append(", \"name\":\"").append(s.getName()).append('\"');
                sb.append(", \"email\":\"").append(s.getEmail() == null ? "" : s.getEmail()).append('\"');
                sb.append(", \"phone\":\"").append(s.getPhone() == null ? "" : s.getPhone()).append('\"');
                sb.append(", \"gpa\":").append(s.computeGPA());
                sb.append(", \"grades\": [");
                boolean gFirst = true;
                for (Grade g : s.getGradeHistory()) {
                    if (!gFirst) sb.append(',');
                    gFirst = false;
                    sb.append("{\"courseCode\":\"").append(g.getCourseCode()).append('\"');
                    sb.append(",\"courseName\":\"").append(g.getCourseName()).append('\"');
                    sb.append(",\"score\":").append(g.getScore()).append('}');
                }
                sb.append("]}");
                w.write(sb.toString());
            }
            w.write("\n]");
        }
        return out;
    }

    /**
     * Binary serialization of the DataStore (students list). Uses ObjectOutputStream.
     */
    public Path exportBinary(DataStore store, String fileName) throws IOException {
        Path out = BINARY_DIR.resolve(fileName.endsWith(".bin") ? fileName : (fileName + ".bin"));
        try (OutputStream os = Files.newOutputStream(out); ObjectOutputStream oos = new ObjectOutputStream(os)) {
            oos.writeObject(store.getAllStudents());
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    public List<Student> importBinary(Path path) throws IOException, ClassNotFoundException {
        try (InputStream is = Files.newInputStream(path); ObjectInputStream ois = new ObjectInputStream(is)) {
            Object obj = ois.readObject();
            return (List<Student>) obj;
        }
    }
}
