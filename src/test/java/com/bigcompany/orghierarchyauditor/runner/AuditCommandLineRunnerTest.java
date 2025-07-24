package com.bigcompany.orghierarchyauditor.runner;

import com.bigcompany.orghierarchyauditor.exception.OrgHierarchyAuditException;
import com.bigcompany.orghierarchyauditor.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class AuditCommandLineRunnerTest {

    private AuditService mockAuditService;
    private AuditCommandLineRunner runner;

    @BeforeEach
    void setUp() {
        mockAuditService = mock(AuditService.class);
        runner = new AuditCommandLineRunner(mockAuditService);
    }

    @Test
    void testRunWithValidArgShouldInvokeAuditService() {
        String[] args = {"src/test/resources/sample.csv"};

        runner.run(args);

        verify(mockAuditService, times(1)).runAudit("src/test/resources/sample.csv");
    }

    @Test
    void testRunWithNoArgsShouldUseDefaultFilePath() {
        String[] args = {};

        runner.run(args);

        verify(mockAuditService, times(1)).runAudit("src/main/resources/employees.csv");
    }

    @Test
    void testRunWithBlankArgShouldUseDefaultFilePath() {
        String[] args = {""};

        runner.run(args);

        verify(mockAuditService, times(1)).runAudit("src/main/resources/employees.csv");
    }

    @Test
    void testRunWhenAuditServiceThrowsKnownExceptionShouldLogError() {
        String[] args = {"src/test/resources/invalid.csv"};
        doThrow(new OrgHierarchyAuditException("Invalid data")).when(mockAuditService).runAudit(anyString());

        runner.run(args);

        verify(mockAuditService, times(1)).runAudit("src/test/resources/invalid.csv");
    }

    @Test
    void testRunWhenAuditServiceThrowsUnknownExceptionShouldLogError() {
        String[] args = {"src/test/resources/unknown.csv"};
        doThrow(new RuntimeException("Something went wrong")).when(mockAuditService).runAudit(anyString());

        runner.run(args);

        verify(mockAuditService, times(1)).runAudit("src/test/resources/unknown.csv");
    }
}