import org.example.ValidationUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilsTest {
    @Test
    void testStudentIdValid() {
        assertTrue(ValidationUtils.isValidStudentId("STU001"));
        assertFalse(ValidationUtils.isValidStudentId("stu001"));
    }

    @Test
    void testEmailValid() {
        assertTrue(ValidationUtils.isValidEmail("alice@example.edu"));
        assertFalse(ValidationUtils.isValidEmail("alice@.edu"));
    }
}
