package com.bigcompany.orghierarchyauditor.util;

import com.bigcompany.orghierarchyauditor.exception.OrgHierarchyAuditException;
import com.bigcompany.orghierarchyauditor.model.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=test")
public class CsvReaderUtilTest {

    @Test
    void testRunAuditForCorruptedFile() {
        Map<Integer, Employee> employeeMap = CsvReaderUtil.readEmployeesFromCsv("src/test/resources/sample_file_corrupted.csv");
        assertTrue(employeeMap.isEmpty(), "Expected empty map for corrupted CSV");
    }

    @Test
    void testRunAuditForCorruptedRecord() {
        assertThrows(OrgHierarchyAuditException.class, () -> {
            CsvReaderUtil.readEmployeesFromCsv("src/test/resources/sample_rec_corrupted.csv");
        });
    }

    @Test
    void testRunAuditForMissingRecord() {
        Map<Integer, Employee> employeeMap = CsvReaderUtil.readEmployeesFromCsv("src/test/resources/sample_data_corrupted.csv");
        assertNotNull(employeeMap, "Employee map should not be null, even for corrupted CSV");
    }
}
