package com.bigcompany.orghierarchyauditor.exception;

public class OrgHierarchyAuditException extends RuntimeException {

    public OrgHierarchyAuditException(String message) {
        super(message);
    }

    public OrgHierarchyAuditException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrgHierarchyAuditException(Throwable cause) {
        super(cause);
    }
}