package com.bigcompany.orghierarchyauditor.service;

import com.bigcompany.orghierarchyauditor.exception.OrgHierarchyAuditException;

public interface AuditService {
    void runAudit(String csvPath) throws OrgHierarchyAuditException;
}