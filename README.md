# 📊 Centralized Logging & Monitoring

## Week 4 — Assignment 2: SLF4J, Logback, Audit Logs & Exception Handling

A **Spring Boot REST API** with centralized logging using SLF4J and Logback, database-backed audit logs for API requests, AOP-based method-level logging, and global exception handling.

---

## 📌 About

This application demonstrates a production-grade logging strategy. Every API request is logged to both the console and log files, and an audit trail is persisted in the database. AOP (Aspect-Oriented Programming) automatically logs method entry/exit/exceptions across service and controller layers without cluttering business logic.

---

## 🚀 API Endpoints

### Product APIs — `/api/products`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/products` | Create product |
| GET | `/api/products` | Get all products |
| GET | `/api/products/{id}` | Get by ID |
| PUT | `/api/products/{id}` | Update product |
| DELETE | `/api/products/{id}` | Delete product |

### Audit Log APIs — `/api/audit`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/audit/logs` | Get recent 50 audit logs |
| GET | `/api/audit/logs/all` | Get all audit logs |
| GET | `/api/audit/logs/method/{GET\|POST}` | Filter by HTTP method |
| GET | `/api/audit/logs/status/{200\|404\|500}` | Filter by status code |

---

## 🧠 Concepts Demonstrated

### 1. Centralized Logging with SLF4J
- `LoggerFactory.getLogger()` used in every class
- Log levels used appropriately:
  - `DEBUG` — Detailed internal info (fetching data, old values)
  - `INFO` — Normal operations (created, updated, fetched)
  - `WARN` — Potentially harmful (deleting data)
  - `ERROR` — Failures (not found, exceptions)

### 2. Logback Configuration
- `logback-spring.xml` configures 4 appenders:
  - **CONSOLE** — Colored logs to terminal
  - **FILE** — All logs to `logs/application.log` (daily rotation, 30-day retention)
  - **ERROR_FILE** — Only ERROR logs to `logs/error.log`
  - **AUDIT_FILE** — Audit-specific logs to `logs/audit.log`
- Framework noise (Spring, Hibernate) reduced to WARN level

### 3. Audit Logs for API Requests
- `RequestLoggingFilter` intercepts every HTTP request
- Captures: HTTP method, URL, client IP, request body, response status, execution time
- Saves to `audit_logs` table in MySQL
- Viewable via `/api/audit/logs` endpoints
- Filterable by method and status code

### 4. AOP-Based Logging (LoggingAspect)
- `@Before` — Logs method entry with arguments
- `@AfterReturning` — Logs method exit with return type
- `@AfterThrowing` — Logs exceptions with class and message
- `@Around` — Measures and logs execution time for controllers
- Zero changes to business code — logging is fully separated

### 5. Exception Handling with Logging
- `GlobalExceptionHandler` catches all exceptions
- Every exception is **logged via SLF4J** before returning JSON error
- Validation errors → `logger.warn()`
- Not found → `logger.error()`
- Unexpected → `logger.error()` with stack trace

---

## 📁 Project Structure

```
Week4_Assignment2/
├── pom.xml
├── README.md
├── logs/                                           # Generated at runtime
│   ├── application.log                             # All logs
│   ├── error.log                                   # ERROR only
│   └── audit.log                                   # Audit logs
└── src/
    └── main/
        ├── java/
        │   └── com/logging/
        │       ├── LoggingMonitoringApplication.java
        │       ├── controller/
        │       │   ├── ProductController.java       # REST API with logging
        │       │   └── AuditLogController.java      # View audit logs
        │       ├── service/
        │       │   └── ProductService.java          # Business logic with logging
        │       ├── repository/
        │       │   ├── ProductRepository.java
        │       │   └── AuditLogRepository.java
        │       ├── model/
        │       │   ├── Product.java
        │       │   └── AuditLog.java                # Audit log entity
        │       ├── audit/
        │       │   ├── RequestLoggingFilter.java     # HTTP request/response filter
        │       │   ├── AuditLogService.java          # Saves audit logs to DB
        │       │   └── LoggingAspect.java            # AOP method-level logging
        │       └── exception/
        │           ├── ResourceNotFoundException.java
        │           └── GlobalExceptionHandler.java   # Logs all exceptions
        └── resources/
            ├── application.properties
            └── logback-spring.xml                    # Logback configuration
```

---

## ⚙️ Setup & Run

### 1. Create MySQL Database
```sql
CREATE DATABASE logging_db;
```

### 2. Update `application.properties`
```properties
spring.datasource.password=your_mysql_password
```

### 3. Build & Run
```bash
cd Week4_Assignment2
mvn clean install
mvn spring-boot:run
```

---

## 🧪 Testing with Postman

### 1. Create a Product — see logs appear
```
POST http://localhost:8080/api/products
{ "name": "Laptop", "category": "Electronics", "price": 55000, "stock": 20 }
```

**Console output:**
```
2026-06-01 10:15:01.234 [http-nio-8080-exec-1] INFO  RequestLoggingFilter - >>> INCOMING REQUEST: POST /api/products from IP: 127.0.0.1
2026-06-01 10:15:01.240 [http-nio-8080-exec-1] INFO  LoggingAspect - ENTERING: ProductService.createProduct() with args: [Product@abc]
2026-06-01 10:15:01.245 [http-nio-8080-exec-1] INFO  ProductService - Creating product: name=Laptop, category=Electronics, price=55000.0
2026-06-01 10:15:01.280 [http-nio-8080-exec-1] INFO  ProductService - Product created successfully with ID: 1
2026-06-01 10:15:01.281 [http-nio-8080-exec-1] INFO  LoggingAspect - EXITING: ProductService.createProduct() with result: Product
2026-06-01 10:15:01.282 [http-nio-8080-exec-1] INFO  LoggingAspect - PERFORMANCE: ProductController.create() executed in 48ms
2026-06-01 10:15:01.283 [http-nio-8080-exec-1] INFO  RequestLoggingFilter - <<< RESPONSE: POST /api/products -> Status: 201 | Time: 49ms
```

### 2. Trigger a 404 error
```
GET http://localhost:8080/api/products/999
```

### 3. View audit logs from database
```
GET http://localhost:8080/api/audit/logs
```

### 4. Filter error logs
```
GET http://localhost:8080/api/audit/logs/status/404
```

### 5. Check log files
```bash
cat logs/application.log    # All logs
cat logs/error.log           # Only errors
cat logs/audit.log           # Audit trail
```

---

## 🛠️ Technologies Used

- **Framework:** Spring Boot 3.2.5
- **Logging:** SLF4J + Logback
- **AOP:** Spring AOP (@Aspect)
- **Database:** MySQL 8.0 + Spring Data JPA
- **Language:** Java 17

---

## 👤 Author

**Gonuguntala Jaikar Ramu**

---
