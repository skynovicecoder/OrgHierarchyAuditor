package com.bigcompany.orghierarchyauditor;

import static org.assertj.core.api.Assertions.assertThat;

import com.bigcompany.orghierarchyauditor.service.AuditService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OrgHierarchyAuditorApplicationTest {

    @Autowired
    private AuditService auditService;

    @Test
    void contextLoads() {
        assertThat(auditService).isNotNull();
    }

    @Test
    void testMainMethodCoverage() {
        OrgHierarchyAuditorApplication.main(new String[]{});
    }
}