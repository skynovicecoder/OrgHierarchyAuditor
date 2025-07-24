package com.bigcompany.orghierarchyauditor.runner;

import com.bigcompany.orghierarchyauditor.exception.OrgHierarchyAuditException;
import com.bigcompany.orghierarchyauditor.service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile("!test")
@Slf4j
public class AuditCommandLineRunner implements CommandLineRunner {
    private static final String DEFAULT_FILE_PATH = "src/main/resources/employees.csv";

    private final AuditService auditService;

    public AuditCommandLineRunner(AuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    public void run(String... args) {
        try {
            String filename = Optional.ofNullable(args)
                    .filter(a -> a.length > 0 && a[0] != null && !a[0].isBlank())
                    .map(a -> a[0])
                    .orElse(DEFAULT_FILE_PATH);
            log.info("File Name with Path from CommandLineRunner : {}", filename);
            auditService.runAudit(filename);
        } catch (OrgHierarchyAuditException e) {
            log.error("Audit failed due to application error: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while running audit: {}", e.getMessage(), e);
        }
    }
}