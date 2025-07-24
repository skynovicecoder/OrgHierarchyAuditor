package com.bigcompany.orghierarchyauditor.service;

import com.bigcompany.orghierarchyauditor.exception.OrgHierarchyAuditException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "spring.profiles.active=test")
class AuditServiceTest {

    @Autowired
    private AuditService auditService;

    @Test
    void testRunAuditShouldProcessValidCsv() {
        auditService.runAudit("src/test/resources/sample.csv");
    }

    @Test
    void testRunAuditShouldThrowException() {
        assertThrows(OrgHierarchyAuditException.class, () -> {
            auditService.runAudit("src/test/resources/sample_without_ceo.csv");
        });
    }

    @Test
    void testRunAuditAfterReOrg() {
        auditService.runAudit("src/test/resources/sample_after_reorg.csv");
    }

}