package com.logging.controller;

import com.logging.audit.AuditLogService;
import com.logging.model.AuditLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller to view audit logs.
 * Allows monitoring API activity through the database.
 *
 * @author Gonuguntala Jaikar Ramu
 */
@RestController
@RequestMapping("/api/audit")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    /**
     * GET /api/audit/logs — Get recent 50 audit logs.
     */
    @GetMapping("/logs")
    public ResponseEntity<List<AuditLog>> getRecentLogs() {
        return ResponseEntity.ok(auditLogService.getRecentLogs());
    }

    /**
     * GET /api/audit/logs/all — Get all audit logs.
     */
    @GetMapping("/logs/all")
    public ResponseEntity<List<AuditLog>> getAllLogs() {
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }

    /**
     * GET /api/audit/logs/method/{method} — Filter by HTTP method.
     */
    @GetMapping("/logs/method/{method}")
    public ResponseEntity<List<AuditLog>> getByMethod(@PathVariable String method) {
        return ResponseEntity.ok(auditLogService.getLogsByMethod(method));
    }

    /**
     * GET /api/audit/logs/status/{status} — Filter by response status.
     */
    @GetMapping("/logs/status/{status}")
    public ResponseEntity<List<AuditLog>> getByStatus(@PathVariable int status) {
        return ResponseEntity.ok(auditLogService.getLogsByStatus(status));
    }
}
