package com.bigcompany.orghierarchyauditor.util;

import com.bigcompany.orghierarchyauditor.exception.OrgHierarchyAuditException;
import com.bigcompany.orghierarchyauditor.model.Employee;
import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.util.*;

@Slf4j
public class CsvReaderUtil {

    public static Map<Integer, Employee> readEmployeesFromCsv(String filePath) throws OrgHierarchyAuditException {
        Map<Integer, Employee> employees = new HashMap<>();

        try (
                FileReader fileReader = new FileReader(filePath);
                CSVReader csvReader = new CSVReader(fileReader)
        ) {
            String[] line;
            int lineNumber = 0;

            line = csvReader.readNext(); // skip header
            if (line == null || line.length < 4) {
                log.error("Invalid or missing CSV header in file: {}", filePath);
                return Collections.emptyMap();
            }

            while ((line = csvReader.readNext()) != null) {
                lineNumber++;

                try {
                    if (line.length < 4) {
                        log.warn("Skipping line {}: Not enough columns -> {}", lineNumber, Arrays.toString(line));
                        continue;
                    }
                    int id = Integer.parseInt(line[0].trim());
                    String firstName = line[1].trim();
                    String lastName = line[2].trim();
                    double salary = Double.parseDouble(line[3].trim());
                    Integer managerId = (line.length > 4 && !line[4].isBlank()) ? Integer.parseInt(line[4].trim()) : null;

                    Employee employee = new Employee(id, firstName, lastName, salary, managerId, new ArrayList<>());
                    employees.put(id, employee);
                } catch (Exception e) {
                    log.error("Error parsing line {}: {}", lineNumber, Arrays.toString(line));
                    throw new OrgHierarchyAuditException("CSV File line parsing error due to: "+e.getMessage(), e);
                }
            }

            // Set subordinates
            for (Employee emp : employees.values()) {
                Integer mgrId = emp.getManagerId();
                if (mgrId != null) {
                    Employee manager = employees.get(mgrId);
                    if (manager != null) {
                        manager.getSubordinates().add(emp);
                    } else {
                        log.warn("Warning: Manager with ID {} not found for {} {} having employee ID {}",
                                mgrId, emp.getFirstName(), emp.getLastName(), emp.getId());
                    }
                }
            }

        } catch (Exception e) {
            log.error("Failed to read or parse CSV file: {}", filePath);
            throw new OrgHierarchyAuditException("Failed to parse file due to: "+e.getMessage(), e);
        }

        return employees;
    }
}
