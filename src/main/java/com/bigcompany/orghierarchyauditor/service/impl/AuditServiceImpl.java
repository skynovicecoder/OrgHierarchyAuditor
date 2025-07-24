package com.bigcompany.orghierarchyauditor.service.impl;

import com.bigcompany.orghierarchyauditor.exception.OrgHierarchyAuditException;
import com.bigcompany.orghierarchyauditor.model.Employee;
import com.bigcompany.orghierarchyauditor.service.AuditService;
import com.bigcompany.orghierarchyauditor.util.CsvReaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class AuditServiceImpl implements AuditService {
    @Value("${orgHierarchyAuditor.reportingLineThreshold:4}")
    private int reportingLineThreshold;

    public void runAudit(String csvPath) throws OrgHierarchyAuditException{
        log.debug("AuditService:: runAudit: File Name with Path : {}", csvPath);
        Map<Integer, Employee> employees = CsvReaderUtil.readEmployeesFromCsv(csvPath);
        Optional<Employee> ceoOpt = employees.values().stream()
                .filter(e -> e.getManagerId() == null)
                //.findFirst();
                .max(Comparator.comparingDouble(Employee::getSalary));

        if (ceoOpt.isEmpty()) {
            log.warn("No CEO found for BigCompany in the data.");
            throw new OrgHierarchyAuditException("No CEO found for BigCompany in the data.");
        }

        Employee ceo = ceoOpt.get();
        log.info("\n\u001B[32m----------------------------------------------------------------------------------------------------------------------------\u001B[0m");
        log.info("\nBigCompany CEO Found: {} {}", ceo.getFirstName(), ceo.getLastName());
        log.info("\n\u001B[32m----------------------------------------------------------------------------------------------------------------------------\u001B[0m");

        checkSalaryCompliance(employees);
        checkReportingDepth(ceo);
    }

    private void checkSalaryCompliance(Map<Integer, Employee> employees) {
        log.info("\nSalary Compliance Report:");

        StringBuilder belowThresholdTable = new StringBuilder("\n\n====== Managers BELOW 20% Salary MIN-Threshold ======\n");
        belowThresholdTable.append(String.format("%-12s %-20s %-10s %-20s %-15s %-25s %-25s%n",
                "EmployeeID", "Name", "Salary", "Subordinates-AVG", "MIN-Threshold", "Diff from MIN-Threshold", "% Less than MIN-Threshold"));

        StringBuilder aboveThresholdTable = new StringBuilder("\n\n====== Managers ABOVE 50% Salary MAX-Threshold ======\n");
        aboveThresholdTable.append(String.format("%-12s %-20s %-10s %-20s %-15s %-25s %-25s%n",
                "EmployeeID", "Name", "Salary", "Subordinates-AVG", "MAX-Threshold", "Diff from MAX-Threshold", "% More than MAX-Threshold"));

        boolean foundBelow = false;
        boolean foundAbove = false;

        for (Employee manager : employees.values()) {
            if (manager.getSubordinates().isEmpty()) continue;

            double avgSubSalary = manager.getSubordinates().stream()
                    .mapToDouble(Employee::getSalary)
                    .average()
                    .orElse(0);

            double minRequired = avgSubSalary * 1.2;
            double maxAllowed = avgSubSalary * 1.5;
            double salary = manager.getSalary();

            if (salary < minRequired) {
                foundBelow = true;

                double salaryDifference = minRequired - salary;
                double lessPercentage = (salaryDifference / salary) * 100;
                log.debug("\n***************************************************************************************");
                log.debug("\nManager: {} {} earns {}, which is BELOW 20% threshold (expected ≥ {}) \nHence {} is getting {} less, which is {}% less than min threshold salary.",
                        manager.getFirstName(), manager.getLastName(), String.format("%.2f", salary), String.format("%.2f", minRequired), manager.getFirstName(), String.format("%.2f", salaryDifference), String.format("%.2f", lessPercentage));
                log.debug("\n**************************************************************************************");

                belowThresholdTable.append(String.format("%-12d %-20s %-10.2f %-20.2f %-15.2f %-25.2f %-25.2f%n",
                        manager.getId(),
                        manager.getFirstName() + " " + manager.getLastName(),
                        salary,
                        avgSubSalary,
                        minRequired,
                        salaryDifference,
                        lessPercentage));

            } else if (salary > maxAllowed) {
                foundAbove = true;

                double salaryDifference = salary - maxAllowed;
                double morePercentage = (salaryDifference / maxAllowed) * 100;
                log.debug("\n#######################################################################################");
                log.debug("\nManager: {} {} earns {}, which is ABOVE 50% threshold (expected ≤ {}) \nHence {} is getting {} more, which is {}% more than max threshold salary.",
                        manager.getFirstName(), manager.getLastName(), String.format("%.2f", salary), String.format("%.2f", maxAllowed), manager.getFirstName(), String.format("%.2f", salaryDifference), String.format("%.2f", morePercentage));
                log.debug("\n######################################################################################");

                aboveThresholdTable.append(String.format("%-12d %-20s %-10.2f %-20.2f %-15.2f %-25.2f %-25.2f%n",
                        manager.getId(),
                        manager.getFirstName() + " " + manager.getLastName(),
                        salary,
                        avgSubSalary,
                        maxAllowed,
                        salaryDifference,
                        morePercentage));
            }
        }
        if (!foundBelow) {
            belowThresholdTable.append("(No violations found)\n");
        }
        if (!foundAbove) {
            aboveThresholdTable.append("(No violations found)\n");
        }

        log.info("\n\u001B[31m----------------------------------------------------------------------------------------------------------------------------\u001B[0m");
        log.info(belowThresholdTable.toString());
        log.info("\n\u001B[31m----------------------------------------------------------------------------------------------------------------------------\u001B[0m");
        log.info("\n\u001B[34m----------------------------------------------------------------------------------------------------------------------------\u001B[0m");
        log.info(aboveThresholdTable.toString());
        log.info("\n\u001B[34m----------------------------------------------------------------------------------------------------------------------------\u001B[0m");
    }

    private void checkReportingDepth(Employee ceo) {
        log.info("\nReporting Chain Depth Report:");

        StringBuilder reportingDepthTable = new StringBuilder("\n\n====== Employees with Reporting Line Depth Greater Than Reporting-Line-Threshold i.e. " + reportingLineThreshold + " In-Between Managers Allowed ======\n");
        reportingDepthTable.append(String.format("%-12s %-20s %-30s%n", "EmployeeID", "Name", "Levels Above Threshold"));
        boolean found = false;

        Deque<Employee> stack = new ArrayDeque<>();
        Deque<Integer> depthStack = new ArrayDeque<>();

        stack.push(ceo);
        depthStack.push(0);

        while (!stack.isEmpty()) {
            Employee current = stack.pop();
            int depth = depthStack.pop();
            int inBetweenThreshold = reportingLineThreshold + 1;

            if (depth > inBetweenThreshold) {
                found = true;

                int extraDepth = depth - inBetweenThreshold;
                log.debug("\n--------------------------------------------------------------------------------------");
                log.debug("\nEmployee {} {} is {} levels below CEO (limit = {}) \nHence {} is having reporting line {} level more from the defined threshold",
                        current.getFirstName(), current.getLastName(), depth, reportingLineThreshold, current.getFirstName(), extraDepth);
                log.debug("\n-------------------------------------------------------------------------------------");

                reportingDepthTable.append(String.format("%-12d %-20s %-30d%n",
                        current.getId(),
                        current.getFirstName() + " " + current.getLastName(),
                        extraDepth));
            }

            for (Employee sub : current.getSubordinates()) {
                stack.push(sub);
                depthStack.push(depth + 1);
            }
        }
        if (!found) {
            reportingDepthTable.append("(No employees exceeded reporting line threshold)\n");
        }

        log.info("\n\u001B[33m----------------------------------------------------------------------------------------------------------------------------\u001B[0m");
        log.info(reportingDepthTable.toString());
        log.info("\n\u001B[33m----------------------------------------------------------------------------------------------------------------------------\u001B[0m");
    }
}