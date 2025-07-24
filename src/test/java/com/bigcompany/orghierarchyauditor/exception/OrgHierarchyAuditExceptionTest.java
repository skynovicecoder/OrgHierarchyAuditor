package com.bigcompany.orghierarchyauditor.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrgHierarchyAuditExceptionTest {

    @Test
    void testExceptionWithMessage() {
        String message = "Custom error occurred";
        OrgHierarchyAuditException exception = new OrgHierarchyAuditException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause(), "Cause should be null when not provided");
    }

    @Test
    void testExceptionWithMessageAndCause() {
        String message = "Another error occurred";
        Throwable cause = new RuntimeException("Root cause");
        OrgHierarchyAuditException exception = new OrgHierarchyAuditException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testExceptionWithOnlyCause() {
        Throwable cause = new IllegalArgumentException("Only cause provided");
        OrgHierarchyAuditException exception = new OrgHierarchyAuditException(cause);

        assertEquals(cause, exception.getCause());
        assertEquals(cause.toString(), exception.getMessage());
    }
}