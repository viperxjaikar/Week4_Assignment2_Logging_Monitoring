package com.logging.audit;

import com.logging.model.AuditLog;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * HTTP Request/Response Logging Filter.
 * Intercepts every API request, logs details using SLF4J,
 * and saves an AuditLog entry to the database.
 *
 * Demonstrates: Servlet Filter, SLF4J logging, Audit trail.
 *
 * @author Gonuguntala Jaikar Ramu
 */
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Autowired
    private AuditLogService auditLogService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Wrap request and response to capture body content
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        // Log incoming request
        logger.info(">>> INCOMING REQUEST: {} {} from IP: {}",
                request.getMethod(), request.getRequestURI(), request.getRemoteAddr());

        // Continue the filter chain
        filterChain.doFilter(wrappedRequest, wrappedResponse);

        long duration = System.currentTimeMillis() - startTime;

        // Get request body (for POST/PUT)
        String requestBody = new String(wrappedRequest.getContentAsByteArray(), StandardCharsets.UTF_8);

        // Log outgoing response
        logger.info("<<< RESPONSE: {} {} -> Status: {} | Time: {}ms",
                request.getMethod(), request.getRequestURI(),
                wrappedResponse.getStatus(), duration);

        if (!requestBody.isEmpty()) {
            logger.debug("Request Body: {}", requestBody);
        }

        // Save audit log to database
        AuditLog auditLog = new AuditLog();
        auditLog.setHttpMethod(request.getMethod());
        auditLog.setRequestUrl(request.getRequestURI());
        auditLog.setClientIp(request.getRemoteAddr());
        auditLog.setRequestBody(requestBody.length() > 2000 ? requestBody.substring(0, 2000) : requestBody);
        auditLog.setResponseStatus(wrappedResponse.getStatus());
        auditLog.setExecutionTimeMs(duration);

        // Set response message based on status
        if (wrappedResponse.getStatus() >= 400) {
            auditLog.setResponseMessage("Error - Status " + wrappedResponse.getStatus());
        } else {
            auditLog.setResponseMessage("Success");
        }

        auditLogService.saveLog(auditLog);

        // Copy response body back to the actual response
        wrappedResponse.copyBodyToResponse();
    }

    /**
     * Skip logging for audit log endpoints to avoid infinite loop.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/audit");
    }
}
