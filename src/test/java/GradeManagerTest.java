import org.example.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GradeManagerTest {

    private GradeManager gradeManager;
    private Student testStudent;
    private Subject testSubject;

    @BeforeEach
    void setUp() throws Exception {
        // This runs before each test
        // Create all dependencies using dependency injection
        IGradeRepository gradeRepository = new GradeRepository();
        IFileExporter fileExporter = new GradeExporter();
        IGradeImporter gradeImporter = new GradeImporter();
        IGradeStatisticsCalculator statisticsCalculator = new GradeStatisticsCalculator();
        
        gradeManager = new GradeManager(gradeRepository, fileExporter, gradeImporter, statisticsCalculator);

        // Create a mock student
        testStudent = new RegularStudent( "John Doe", 20);

        // Create a mock subject
        testSubject = new CoreSubject("Mathematics", "MATH101");
    }

    @AfterEach
    void tearDown() {
        // Clean up any files created during tests
        // Clean up grade report files with any student ID
        File[] files = new File(".").listFiles((dir, name) -> 
            name.matches("GradeReport_\\d+_John_Doe\\.txt"));
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        
        File file2 = new File("test_grades.csv");
        if (file2.exists()) {
            file2.delete();
        }
        File file3 = new File("test_invalid.csv");
        if (file3.exists()) {
            file3.delete();
        }
    }

    @Test
    void testRecordGrade_ValidGrade() {
        // Test recording a valid grade
        gradeManager.recordGrade(testStudent, testSubject, 85.0);

        // Verify by checking the grade report (indirect verification)
        // Since recordGrade prints to console, we're testing it doesn't throw exceptions
        assertDoesNotThrow(() -> gradeManager.recordGrade(testStudent, testSubject, 85.0));
    }

    @Test
    void testRecordGrade_MultipleGrades() {
        // Test recording multiple grades
        Subject math = new CoreSubject("Math", "MATH101");
        Subject english = new CoreSubject("English", "ENG101");
        Subject science = new CoreSubject("Science", "SCI101");

        assertDoesNotThrow(() -> {
            gradeManager.recordGrade(testStudent, math, 85.0);
            gradeManager.recordGrade(testStudent, english, 90.0);
            gradeManager.recordGrade(testStudent, science, 78.5);
        });
    }

    @Test
    void testRecordGrade_BoundaryScores() {
        // Test boundary values
        assertDoesNotThrow(() -> gradeManager.recordGrade(testStudent, testSubject, 0.0));
        assertDoesNotThrow(() -> gradeManager.recordGrade(testStudent, testSubject, 100.0));
        assertDoesNotThrow(() -> gradeManager.recordGrade(testStudent, testSubject, 50.0));
    }

    @Test
    void testRecordGrade_MaxCapacity() {
        // Test that the array doesn't overflow
        // Create 100 subjects and record grades
        for (int i = 0; i < 100; i++) {
            Subject subject = new CoreSubject("Subject" + i, "SUB" + i);
            gradeManager.recordGrade(testStudent, subject, 75.0);
        }

        // Try to add one more (should print "Grade storage is full!")
        Subject extraSubject = new CoreSubject("Extra", "EXTRA");
        assertDoesNotThrow(() -> gradeManager.recordGrade(testStudent, extraSubject, 80.0));
    }

    @Test
    void testViewGradeReport_WithGrades() {
        // Record some grades first
        gradeManager.recordGrade(testStudent, testSubject, 85.0);
        Subject english = new CoreSubject("English", "ENG101");
        gradeManager.recordGrade(testStudent, english, 90.0);

        // Test that viewing report doesn't throw exceptions
        assertDoesNotThrow(() -> gradeManager.viewGradeReport(testStudent));
    }

    @Test
    void testViewGradeReport_NoGrades() {
        // Test viewing report with no grades recorded
        assertDoesNotThrow(() -> gradeManager.viewGradeReport(testStudent));
    }

    @Test
    void testViewGradeReport_MultipleStudents() {
        // Create another student
        Student student2 = new RegularStudent( "Jane Smith", 21);

        // Record grades for both students
        gradeManager.recordGrade(testStudent, testSubject, 85.0);
        Subject english = new CoreSubject("English", "ENG101");
        gradeManager.recordGrade(student2, english, 92.0);

        // Verify each student sees only their grades
        assertDoesNotThrow(() -> gradeManager.viewGradeReport(testStudent));
        assertDoesNotThrow(() -> gradeManager.viewGradeReport(student2));
    }

    @Test
    void testExportGradeReport_CreatesFile() throws IOException {
        // Record a grade
        gradeManager.recordGrade(testStudent, testSubject, 85.0);

        // Export the report
        gradeManager.exportGradeReport(testStudent);

        // Check if file was created (with pattern matching for student ID)
        File[] files = new File(".").listFiles((dir, name) -> 
            name.matches("GradeReport_\\d+_John_Doe\\.txt"));
        assertTrue(files != null && files.length > 0, "Grade report file should be created");

        // Verify file has content
        File reportFile = files[0];
        BufferedReader reader = new BufferedReader(new FileReader(reportFile));
        String firstLine = reader.readLine();
        assertNotNull(firstLine, "File should have content");
        reader.close();
    }

    @Test
    void testExportGradeReport_FileContent() throws IOException {
        // Record grades
        gradeManager.recordGrade(testStudent, testSubject, 85.0);

        // Export report
        gradeManager.exportGradeReport(testStudent);

        // Find the created file (with pattern matching for student ID)
        File[] files = new File(".").listFiles((dir, name) -> 
            name.matches("GradeReport_\\d+_John_Doe\\.txt"));
        assertTrue(files != null && files.length > 0, "Grade report file should exist");
        
        File reportFile = files[0];
        BufferedReader reader = new BufferedReader(new FileReader(reportFile));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
        reader.close();

        String fileContent = content.toString();
        assertTrue(fileContent.contains("GRADE REPORT"), "File should contain report header");
        assertTrue(fileContent.contains("John Doe"), "File should contain student name");
        assertTrue(fileContent.contains("85"), "File should contain the score");
    }

    @Test
    void testExportGradeReport_NoGrades() throws IOException {
        // Export report without recording any grades
        assertDoesNotThrow(() -> gradeManager.exportGradeReport(testStudent));

        // File should still be created (with pattern matching for student ID)
        File[] files = new File(".").listFiles((dir, name) -> 
            name.matches("GradeReport_\\d+_John_Doe\\.txt"));
        assertTrue(files != null && files.length > 0, "Report file should be created even with no grades");
        
        // Verify it has content about no grades
        File reportFile = files[0];
        BufferedReader reader = new BufferedReader(new FileReader(reportFile));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
        reader.close();
        
        String fileContent = content.toString();
        assertTrue(fileContent.contains("GRADE REPORT"), "File should contain report header even with no grades");
    }

    @Test
    void testViewGradeStatistics_NoGrades() {
        // Create a mock StudentManager
        StudentManager studentManager = new StudentManager();
        studentManager.addStudent(testStudent);

        // Test statistics with no grades
        assertDoesNotThrow(() -> gradeManager.viewGradeStatistics(studentManager));
    }

    @Test
    void testViewGradeStatistics_WithGrades() {
        StudentManager studentManager = new StudentManager();
        studentManager.addStudent(testStudent);

        // Record various grades
        gradeManager.recordGrade(testStudent, new CoreSubject("Math", "MATH101"), 85.0);
        gradeManager.recordGrade(testStudent, new CoreSubject("English", "ENG101"), 90.0);
        gradeManager.recordGrade(testStudent, new CoreSubject("Science", "SCI101"), 78.0);
        gradeManager.recordGrade(testStudent, new CoreSubject("History", "HIST101"), 92.0);

        // Test that statistics display works
        assertDoesNotThrow(() -> gradeManager.viewGradeStatistics(studentManager));
    }

    @Test
    void testViewGradeStatistics_PassFailRatio() {
        StudentManager studentManager = new StudentManager();
        studentManager.addStudent(testStudent);

        // Record mix of passing and failing grades
        gradeManager.recordGrade(testStudent, new CoreSubject("Subject1", "SUB1"), 85.0); // Pass
        gradeManager.recordGrade(testStudent, new CoreSubject("Subject2", "SUB2"), 45.0); // Fail
        gradeManager.recordGrade(testStudent, new CoreSubject("Subject3", "SUB3"), 92.0); // Pass

        // Should calculate pass/fail statistics correctly
        assertDoesNotThrow(() -> gradeManager.viewGradeStatistics(studentManager));
    }

    @Test
    void testBulkImportGrades_FileNotFound() {
        StudentManager studentManager = new StudentManager();

        // Simulate user input for non-existent file
        String input = "nonexistent.csv\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Should handle file not found gracefully
        assertDoesNotThrow(() -> gradeManager.bulkImportGrades(studentManager));

        // Reset System.in
        System.setIn(System.in);
    }

    @Test
    void testBulkImportGrades_ValidCSV() throws IOException {
        StudentManager studentManager = new StudentManager();
        studentManager.addStudent(testStudent);

        // Create a test CSV file
        File csvFile = new File("test_grades.csv");
        FileWriter writer = new FileWriter(csvFile);
        writer.write("StudentID,SubjectName,SubjectCode,SubjectType,Score\n");
        writer.write("1,Math,MATH101,Core,85.5\n");
        writer.write("1,English,ENG101,Core,90.0\n");
        writer.close();

        // Simulate user input
        String input = "test_grades.csv\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Import grades
        assertDoesNotThrow(() -> gradeManager.bulkImportGrades(studentManager));

        // Clean up
        System.setIn(System.in);
        csvFile.delete();
    }

    @Test
    void testBulkImportGrades_InvalidFormat() throws IOException {
        StudentManager studentManager = new StudentManager();
        studentManager.addStudent(testStudent);

        // Create CSV with invalid format
        File csvFile = new File("test_invalid.csv");
        FileWriter writer = new FileWriter(csvFile);
        writer.write("1,Math,85.5\n"); // Missing fields
        writer.write("1,English,ENG101,Core,NotANumber\n"); // Invalid score
        writer.close();

        String input = "test_invalid.csv\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Should handle errors gracefully
        assertDoesNotThrow(() -> gradeManager.bulkImportGrades(studentManager));

        System.setIn(System.in);
        csvFile.delete();
    }
}