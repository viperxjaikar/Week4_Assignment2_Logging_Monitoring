package com.logging.audit;

import com.logging.model.AuditLog;
import com.logging.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing audit logs.
 *
 * @author Gonuguntala Jaikar Ramu
 */
@Service
public class AuditLogService {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogService.class);

    @Autowired
    private AuditLogRepository auditLogRepository;

    /**
     * Save an audit log entry to the database.
     */
    public void saveLog(AuditLog auditLog) {
        try {
            auditLogRepository.save(auditLog);
            logger.debug("Audit log saved: {} {} -> {} ({}ms)",
                    auditLog.getHttpMethod(), auditLog.getRequestUrl(),
                    auditLog.getResponseStatus(), auditLog.getExecutionTimeMs());
        } catch (Exception e) {
            logger.error("Failed to save audit log: {}", e.getMessage());
        }
    }

    /**
     * Get recent audit logs.
     */
    public List<AuditLog> getRecentLogs() {
        return auditLogRepository.findTop50ByOrderByTimestampDesc();
    }

    /**
     * Get audit logs by HTTP method.
     */
    public List<AuditLog> getLogsByMethod(String method) {
        return auditLogRepository.findByHttpMethodIgnoreCase(method);
    }

    /**
     * Get audit logs by response status.
     */
    public List<AuditLog> getLogsByStatus(int status) {
        return auditLogRepository.findByResponseStatus(status);
    }

    /**
     * Get all audit logs.
     */
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }
}
