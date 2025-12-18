package org.example;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImportDemoTest {
    @Test
    void importSampleCsv() throws Exception {
        FileService fs = new FileService();
        DataStore store = new DataStore();
        List<String> errs = fs.importStudentsFromCsv(Paths.get("data/csv/import.csv"), store);
        // Ensure import succeeded without catastrophic errors
        System.out.println("Import errors: " + errs);
        assertTrue(store.getAllStudents().size() >= 1, "At least one student should be imported");
    }
}
