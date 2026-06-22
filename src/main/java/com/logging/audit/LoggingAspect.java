package com.logging.audit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * AOP-based Logging Aspect.
 * Automatically logs method entry, exit, execution time, and exceptions
 * for all service and controller methods.
 *
 * Demonstrates: AOP (@Aspect), SLF4J, Centralized logging.
 *
 * @author Gonuguntala Jaikar Ramu
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Logs method entry with arguments for all service methods.
     */
    @Before("execution(* com.logging.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("ENTERING: {}.{}() with args: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    /**
     * Logs method exit with return value for all service methods.
     */
    @AfterReturning(pointcut = "execution(* com.logging.service.*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("EXITING: {}.{}() with result: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                result != null ? result.getClass().getSimpleName() : "void");
    }

    /**
     * Logs exceptions thrown by service methods.
     */
    @AfterThrowing(pointcut = "execution(* com.logging.service.*.*(..))", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        logger.error("EXCEPTION in {}.{}(): {} - {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                exception.getClass().getSimpleName(),
                exception.getMessage());
    }

    /**
     * Logs execution time for all controller methods.
     */
    @Around("execution(* com.logging.controller.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long duration = System.currentTimeMillis() - start;
        logger.info("PERFORMANCE: {}.{}() executed in {}ms",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                duration);

        return result;
    }
}
