package org.example;

import java.util.regex.Pattern;

public final class ValidationUtils {
    private ValidationUtils() {}

    public static final Pattern STUDENT_ID = Pattern.compile("^STU\\d{3}$");
    public static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    public static final Pattern PHONE = Pattern.compile("^(\\(\\d{3}\\) \\\\d{3}-\\d{4}|\\d{3}-\\d{3}-\\d{4}|\\+1-\\d{3}-\\d{3}-\\d{4}|\\d{10})$");
    public static final Pattern NAME = Pattern.compile("^[a-zA-Z]+(['\\-\\s][a-zA-Z]+)*$");
    public static final Pattern DATE = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    public static final Pattern COURSE_CODE = Pattern.compile("^[A-Z]{3}\\d{3}$");
    public static final Pattern GRADE = Pattern.compile("^(100|[1-9]?\\d)$");

    public static boolean isValidStudentId(String s) {
        return s != null && STUDENT_ID.matcher(s).matches();
    }

    public static boolean isValidEmail(String s) {
        return s != null && EMAIL.matcher(s).matches();
    }

    public static boolean isValidPhone(String s) {
        return s != null && PHONE.matcher(s).matches();
    }

    public static boolean isValidName(String s) {
        return s != null && NAME.matcher(s).matches();
    }

    public static boolean isValidDate(String s) {
        return s != null && DATE.matcher(s).matches();
    }

    public static boolean isValidCourseCode(String s) {
        return s != null && COURSE_CODE.matcher(s).matches();
    }

    public static boolean isValidGrade(String s) {
        return s != null && GRADE.matcher(s).matches();
    }

    public static String examplesFor(String field) {
        switch ((field == null) ? "" : field.toLowerCase()) {
            case "studentid": return "Example: STU001";
            case "email": return "Example: alice@example.edu";
            case "phone": return "Examples: (123) 456-7890 | 123-456-7890 | +1-123-456-7890 | 1234567890";
            case "name": return "Examples: John Doe | Anne-Marie O'Neill";
            case "date": return "Example: 2025-12-31";
            case "course": return "Example: MAT101";
            case "grade": return "Examples: 0 | 85 | 100";
            default: return "No examples available";
        }
    }
}
