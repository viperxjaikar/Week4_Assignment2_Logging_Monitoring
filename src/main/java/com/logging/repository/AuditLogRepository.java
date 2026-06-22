package com.logging.repository;

import com.logging.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Gonuguntala Jaikar Ramu
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findTop50ByOrderByTimestampDesc();

    List<AuditLog> findByHttpMethodIgnoreCase(String httpMethod);

    List<AuditLog> findByResponseStatus(int status);
}
