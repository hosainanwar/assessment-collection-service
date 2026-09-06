# e-pourashava-api — Architecture Document

## 1. Overview

**e-pourashava-api** is a monolithic REST API backend built with Java and Spring Boot. It serves as the central backend for e-Pourashava (municipal e-governance) platform, handling all business logic within a single deployable unit.

| Attribute | Value |
|-----------|-------|
| **Architecture Style** | Monolithic (Modular Monolith) |
| **Primary Framework** | Spring Boot 3.x |
| **Language** | Java 17 |
| **Build Tool** | Maven |
| **Database** | PostgreSQL |
| **Deployment** | Single JAR / Single Docker Container |

---

## 2. Tech Stack

| Category | Technology | Purpose |
|----------|------------|---------|
| Language | Java 17 | Core language |
| Framework | Spring Boot 3.2+ | Application framework |
| Security | Spring Security + JWT | Authentication & authorization |
| ORM | Spring Data JPA / Hibernate | Database access |
| Database | PostgreSQL 15+ | Primary data store |
| Cache | Redis | Caching layer |
| Migration | Flyway | Database versioning |
| Validation | Jakarta Bean Validation | Input validation |
| Docs | SpringDoc OpenAPI 3 | API documentation |
| Logging | SLF4J + Logback | Structured logging |
| Monitoring | Spring Boot Actuator + Micrometer | Health checks & metrics |
| Container | Docker | Containerization |
| Build | Maven 3.9+ | Build & dependency management |

---

## 3. High-Level Architecture

```
┌─────────────────────────────────────────────────────────┐
│                      CLIENTS                            │
│              (Web, Mobile, Third-party)                 │
└─────────────────────┬───────────────────────────────────┘
                      │ HTTP/HTTPS
                      ▼
┌─────────────────────────────────────────────────────────┐
│                  SPRING BOOT APP                        │
│  ┌───────────────────────────────────────────────────┐  │
│  │              CONTROLLER LAYER                     │  │
│  │         (REST Endpoints, Request Mapping)         │  │
│  └─────────────────────┬─────────────────────────────┘  │
│                        │                                │
│  ┌─────────────────────▼─────────────────────────────┐  │
│  │               SERVICE LAYER                       │  │
│  │       (Business Logic, Transaction Management)    │  │
│  └─────────────────────┬─────────────────────────────┘  │
│                        │                                │
│  ┌─────────────────────▼─────────────────────────────┐  │
│  │            REPOSITORY LAYER                       │  │
│  │         (Data Access, JPA Repositories)           │  │
│  └─────────────────────┬─────────────────────────────┘  │
│                        │                                │
│  ┌─────────────────────▼─────────────────────────────┐  │
│  │              ENTITY LAYER                         │  │
│  │           (JPA Entities, Domain Models)           │  │
│  └───────────────────────────────────────────────────┘  │
│                                                         │
│  ┌──────────┐  ┌──────────┐  ┌──────────────────────┐  │
│  │  Redis   │  │  Kafka   │  │   External Services  │  │
│  │  Cache   │  │ (Optional│  │   (Email, SMS, etc.) │  │
│  └──────────┘  └──────────┘  └──────────────────────┘  │
└─────────────────────┬───────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────┐
│                   PostgreSQL                            │
└─────────────────────────────────────────────────────────┘
```

---

## 4. Package Structure

```
co.egen.pourashava
├── PourashavaApplication.java          # Main application entry point
│
├── controller/                         # REST API endpoints
│   ├── AuthController.java
│   ├── UserController.java
│   ├── CitizenController.java
│   ├── ServiceController.java
│   └── HealthController.java
│
├── service/                            # Business logic layer
│   ├── AuthService.java
│   ├── UserService.java
│   ├── CitizenService.java
│   ├── ServiceService.java
│   └── impl/
│       ├── AuthServiceImpl.java
│       ├── UserServiceImpl.java
│       ├── CitizenServiceImpl.java
│       └── ServiceServiceImpl.java
│
├── repository/                         # Data access layer
│   ├── UserRepository.java
│   ├── CitizenRepository.java
│   └── ServiceRepository.java
│
├── entity/                             # JPA entities
│   ├── BaseEntity.java                 # Auditable base entity
│   ├── User.java
│   ├── Citizen.java
│   ├── Service.java
│   ├── Role.java
│   └── Permission.java
│
├── dto/                                # Data Transfer Objects
│   ├── request/
│   │   ├── LoginRequest.java
│   │   ├── CreateUserRequest.java
│   │   └── CreateCitizenRequest.java
│   ├── response/
│   │   ├── AuthResponse.java
│   │   ├── UserResponse.java
│   │   └── ApiResponse.java
│   └── projection/
│       └── UserProjection.java
│
├── mapper/                             # Entity ↔ DTO mapping
│   ├── UserMapper.java
│   └── CitizenMapper.java
│
├── config/                             # Configuration classes
│   ├── SecurityConfig.java
│   ├── JwtConfig.java
│   ├── RedisConfig.java
│   ├── SwaggerConfig.java
│   └── FlywayConfig.java
│
├── security/                           # Security components
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── CustomUserDetailsService.java
│
├── enums/                              # Enumeration types
│   ├── UserRole.java
│   ├── ServiceStatus.java
│   └── CitizenType.java
│
├── exception/                          # Custom exceptions & handlers
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── UnauthorizedException.java
│   └── BadRequestException.java
│
├── util/                               # Utility classes
│   ├── DateUtil.java
│   └── StringUtil.java
│
└── annotation/                         # Custom annotations
    ├── AuditLog.java
    └── RateLimit.java
```

---

## 5. Database Design

### 5.1 Naming Conventions

| Element | Convention | Example |
|---------|------------|---------|
| Table names | `snake_case`, plural | `users`, `citizens`, `service_requests` |
| Column names | `snake_case` | `created_at`, `is_active` |
| Primary keys | `id` (bigint, auto-generated) | `id` |
| Foreign keys | `{referenced_table}_id` | `user_id`, `role_id` |
| Indexes | `idx_{table}_{column}` | `idx_users_email` |
| Unique constraints | `uk_{table}_{column}` | `uk_users_email` |

### 5.2 Entity Relationship Diagram

```
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│    users     │       │  user_roles  │       │    roles     │
├──────────────┤       ├──────────────┤       ├──────────────┤
│ id (PK)      │──┐    │ user_id (FK) │    ┌──│ id (PK)      │
│ email        │  └───>│ role_id (FK) │<───┘  │ name         │
│ password     │       └──────────────┘       │ description  │
│ first_name   │                              └──────────────┘
│ last_name    │       ┌──────────────┐       ┌──────────────┐
│ is_active    │       │role_permissions│      │ permissions  │
│ created_at   │       ├──────────────┤       ├──────────────┤
│ updated_at   │       │ role_id (FK) │──────>│ id (PK)      │
└──────────────┘       │permission_id │       │ name         │
                       │   (FK)       │       │ description  │
                       └──────────────┘       └──────────────┘

┌──────────────┐       ┌────────────────────┐
│  citizens    │       │  service_requests  │
├──────────────┤       ├────────────────────┤
│ id (PK)      │──────<│ id (PK)            │
│ national_id  │       │ citizen_id (FK)    │
│ first_name   │       │ service_type       │
│ last_name    │       │ status             │
│ phone        │       │ description        │
│ email        │       │ created_at         │
│ address      │       │ updated_at         │
│ created_at   │       └────────────────────┘
│ updated_at   │
└──────────────┘
```

### 5.3 Base Entity

All entities extend `BaseEntity` which provides auditing fields:

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
```

---

## 6. Database Migrations

### 6.1 Flyway Configuration

| Property | Value |
|----------|-------|
| Tool | Flyway |
| Migration location | `classpath:db/migration` |
| Naming convention | `V{version}__{description}.sql` |
| Repeatable migrations | `R__{description}.sql` |

### 6.2 Migration File Structure

```
src/main/resources/
└── db/
    ├── migration/
    │   ├── V1__create_users_table.sql
    │   ├── V2__create_roles_table.sql
    │   ├── V3__create_user_roles_table.sql
    │   ├── V4__create_citizens_table.sql
    │   ├── V5__create_service_requests_table.sql
    │   └── V6__add_permissions_table.sql
    └── repeatable/
        ├── R__create_views.sql
        └── R__seed_roles.sql
```

### 6.3 Seed Data Strategy

- **Development**: Full seed data via `data.sql` or Flyway repeatable migrations
- **QA**: Minimal seed data for testing
- **Production**: No seed data; managed via admin interfaces

---

## 7. Security

### 7.1 Authentication Flow

```
┌────────┐     ┌─────────────┐     ┌──────────────┐     ┌─────────┐
│ Client │────>│ /api/v1/auth│────>│ Validate     │────>│ Generate│
│        │     │ /login      │     │ Credentials  │     │ JWT     │
└────────┘     └─────────────┘     └──────────────┘     └────┬────┘
                                                             │
┌────────┐     ┌─────────────┐     ┌──────────────┐         │
│ Client │<────│ Return JWT  │<────│ Store in     │<────────┘
│        │     │ Token       │     │ Response     │
└────────┘     └─────────────┘     └──────────────┘

┌────────┐     ┌─────────────┐     ┌──────────────┐     ┌─────────┐
│ Client │────>│ Protected   │────>│ JwtAuthFilter│────>│ Validate│
│ (JWT)  │     │ Endpoint    │     │ Extract Token│     │ Token   │
└────────┘     └─────────────┘     └──────────────┘     └────┬────┘
                                                             │
┌────────┐     ┌─────────────┐     ┌──────────────┐         │
│ Client │<────│ Return      │<────│ Set Security │<────────┘
│        │     │ Response    │     │ Context      │
└────────┘     └─────────────┘     └──────────────┘
```

### 7.2 JWT Configuration

| Property | Value |
|----------|-------|
| Algorithm | RS256 (RSA) or HS256 (HMAC) |
| Access Token expiry | 15 minutes |
| Refresh Token expiry | 7 days |
| Token structure | `{header}.{payload}.{signature}` |

### 7.3 Role-Based Access Control

```java
// Role hierarchy
SUPER_ADMIN > ADMIN > USER

// Endpoint protection examples
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin/users")
public List<UserResponse> getAllUsers() { ... }

@PreAuthorize("hasRole('USER') and @security.isOwner(#id)")
@GetMapping("/users/{id}")
public UserResponse getUser(@PathVariable Long id) { ... }
```

---

## 8. API Design

### 8.1 RESTful Conventions

| HTTP Method | Purpose | Example |
|-------------|---------|---------|
| `GET` | Read resources | `GET /api/v1/users` |
| `POST` | Create resources | `POST /api/v1/users` |
| `PUT` | Full update | `PUT /api/v1/users/{id}` |
| `PATCH` | Partial update | `PATCH /api/v1/users/{id}` |
| `DELETE` | Delete resources | `DELETE /api/v1/users/{id}` |

### 8.2 URL Structure

```
/api/v1/{resource}
/api/v1/{resource}/{id}
/api/v1/{resource}/{id}/{sub-resource}
```

### 8.3 Response Format

```json
// Success Response
{
  "status": "success",
  "data": { ... },
  "timestamp": "2026-07-29T10:30:00Z"
}

// Error Response
{
  "status": "error",
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid input",
    "details": [
      {
        "field": "email",
        "message": "Email is required"
      }
    ]
  },
  "timestamp": "2026-07-29T10:30:00Z"
}

// Paginated Response
{
  "status": "success",
  "data": {
    "content": [...],
    "page": 0,
    "size": 10,
    "totalElements": 100,
    "totalPages": 10
  }
}
```

### 8.4 API Versioning

- **URL Path Versioning**: `/api/v1/`, `/api/v2/`
- Major version changes for breaking changes
- Minor changes within same version

---

## 9. API Documentation

### 9.1 SpringDoc OpenAPI 3

| Property | Value |
|----------|-------|
| UI Path | `/swagger-ui.html` |
| API Docs Path | `/v3/api-docs` |
| Format | OpenAPI 3.0 |

### 9.2 Swagger Annotations

```java
@Tag(name = "User Management", description = "CRUD operations for users")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Operation(summary = "Get user by ID", description = "Retrieves a user by their unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) { ... }
}
```

---

## 10. Error Handling & Validation

### 10.1 Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(NOT_FOUND).body(
            ErrorResponse.builder()
                .code("NOT_FOUND")
                .message(ex.getMessage())
                .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<FieldError> errors = ex.getBindingResult().getFieldErrors();
        return ResponseEntity.status(BAD_REQUEST).body(
            ErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message("Invalid input")
                .details(errors.stream()
                    .map(e -> FieldErrorDetail.builder()
                        .field(e.getField())
                        .message(e.getDefaultMessage())
                        .build())
                    .collect(toList()))
                .build()
        );
    }
}
```

### 10.2 Validation Rules

```java
@Data
public class CreateUserRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;
}
```

### 10.3 Error Codes

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `VALIDATION_ERROR` | 400 | Input validation failed |
| `UNAUTHORIZED` | 401 | Authentication required |
| `FORBIDDEN` | 403 | Insufficient permissions |
| `NOT_FOUND` | 404 | Resource not found |
| `CONFLICT` | 409 | Resource already exists |
| `RATE_LIMITED` | 429 | Too many requests |
| `INTERNAL_ERROR` | 500 | Server error |

---

## 11. Logging Strategy

### 11.1 Technology Stack

| Component | Technology |
|-----------|------------|
| Facade | SLF4J |
| Implementation | Logback |
| Format | JSON (structured) |
| Level Management | Logback Spring profiles |

### 11.2 Log Levels

| Level | Usage |
|-------|-------|
| `ERROR` | System errors, exceptions requiring attention |
| `WARN` | Unexpected conditions, degraded functionality |
| `INFO` | Significant business events, audit trail |
| `DEBUG` | Diagnostic information, development troubleshooting |
| `TRACE` | Detailed execution flow, most verbose |

### 11.3 Logback Configuration

```xml
<configuration>
    <springProfile name="dev">
        <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        <root level="DEBUG">
            <appender-ref ref="CONSOLE"/>
        </root>
    </springProfile>

    <springProfile name="prod">
        <appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
            <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
        </appender>
        <root level="INFO">
            <appender-ref ref="JSON"/>
        </root>
    </springProfile>
</configuration>
```

### 11.4 Structured Logging

```java
// Business event logging
log.info("User created successfully | userId={} | email={} | createdBy={}",
    user.getId(), user.getEmail(), currentUser);

// Error logging with context
log.error("Failed to process service request | requestId={} | citizenId={} | error={}",
    requestId, citizenId, ex.getMessage(), ex);

// Audit logging (via AOP)
@AuditLog(action = "USER_LOGIN", resource = "User")
public AuthResponse login(LoginRequest request) { ... }
```

---

## 12. Caching Strategy

### 12.1 Technology

| Component | Technology |
|-----------|------------|
| Cache Provider | Redis |
| Spring Integration | Spring Cache (`@Cacheable`) |
| Serialization | Jackson JSON |

### 12.2 Cache Configuration

```java
@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .withCacheConfiguration("users", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
            .withCacheConfiguration("roles", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)))
            .build();
    }
}
```

### 12.3 Caching Patterns

| Pattern | Use Case | TTL |
|---------|----------|-----|
| `@Cacheable` | Read-heavy, rarely changing data (roles, configs) | 1 hour |
| `@CachePut` | Update cache on write | 30 minutes |
| `@CacheEvict` | Invalidate on update/delete | Immediate |
| `@Caching` | Multiple cache operations | Varies |

### 12.4 Cache Key Convention

```
{service}:{entity}:{id}           # Single entity: user:User:123
{service}:{entity}:list:{params}  # List query: user:User:list:page:0
{service}:{entity}:count          # Count query: user:User:count
```

---

## 13. File Management

### 13.1 Storage Strategy

| Environment | Storage |
|-------------|---------|
| Development | Local filesystem |
| QA/Production | Object storage (S3-compatible) / Local with volume mount |

### 13.2 File Upload API

```
POST /api/v1/files/upload
Content-Type: multipart/form-data

Response:
{
  "status": "success",
  "data": {
    "fileId": "abc-123",
    "fileName": "document.pdf",
    "fileUrl": "/api/v1/files/abc-123",
    "size": 1024000,
    "contentType": "application/pdf"
  }
}
```

### 13.3 File Service

```java
@Service
public class FileService {

    private final String uploadDir;

    public String storeFile(MultipartFile file) {
        // Validate file type and size
        // Generate unique filename
        // Store to configured location
        // Return file metadata
    }

    public Resource loadFileAsResource(String fileId) {
        // Retrieve file from storage
        // Return as Resource
    }
}
```

### 13.4 File Restrictions

| Property | Limit |
|----------|-------|
| Max file size | 10 MB |
| Allowed types | PDF, JPG, PNG, DOC, DOCX |
| Naming | UUID-based (no original names stored) |

---

## 14. Notification System

### 14.1 Notification Types

| Type | Channel | Use Case |
|------|---------|----------|
| Email | SMTP (Spring Mail) | Account registration, password reset, alerts |
| SMS | External API (Twilio/local) | OTP, critical alerts |
| In-App | Database + WebSocket | Real-time notifications |

### 14.2 Email Service

```java
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendWelcomeEmail(String to, String name) {
        Context context = new Context();
        context.setVariable("name", name);
        String htmlContent = templateEngine.process("welcome-email", context);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Welcome to e-Pourashava");
        message.setText(htmlContent);
        mailSender.send(message);
    }
}
```

### 14.3 Notification Queue

For non-critical notifications, use async processing:

```java
@Service
public class NotificationService {

    @Async
    public void sendEmailAsync(String to, String subject, String body) {
        // Queue for background processing
    }
}
```

---

## 15. Monitoring & Observability

### 15.1 Spring Boot Actuator

| Endpoint | Purpose |
|----------|---------|
| `/actuator/health` | Application health status |
| `/actuator/health/db` | Database connectivity |
| `/actuator/health/redis` | Redis connectivity |
| `/actuator/info` | Application info |
| `/actuator/metrics` | Application metrics |
| `/actuator/prometheus` | Prometheus-format metrics |

### 15.2 Health Indicators

```java
@Component
public class CustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // Check external dependencies
        // Return UP/DOWN with details
    }
}
```

### 15.3 Metrics

| Metric | Type | Description |
|--------|------|-------------|
| `http.server.requests` | Timer | API request latency |
| `jvm.memory.used` | Gauge | JVM memory usage |
| `db.connections.active` | Gauge | Active DB connections |
| `cache.hits` | Counter | Cache hit rate |
| `business.orders.created` | Counter | Custom business metrics |

### 15.4 Micrometer + Prometheus

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: e-pourashava-api
```

---

## 16. Configuration Management

### 16.1 Profile-Based Configuration

```
src/main/resources/
├── application.yml                    # Base config
├── application-dev.yml                # Development
├── application-qa.yml                 # QA environment
├── application-staging.yml            # Staging
└── application-prod.yml               # Production (not in git)
```

### 16.2 Externalized Configuration

```yaml
# application.yml
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}

server:
  port: ${SERVER_PORT:8080}
  servlet:
    context-path: /api/v1

spring.datasource:
  url: ${DB_URL:jdbc:postgresql://localhost:5432/pourashava}
  username: ${DB_USERNAME:postgres}
  password: ${DB_PASSWORD:password}
```

### 16.3 Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active profile | `dev` |
| `DB_URL` | Database URL | `jdbc:postgresql://localhost:5432/pourashava` |
| `DB_USERNAME` | Database username | `postgres` |
| `DB_PASSWORD` | Database password | - |
| `JWT_SECRET` | JWT signing key | - |
| `REDIS_HOST` | Redis host | `localhost` |
| `REDIS_PORT` | Redis port | `6379` |

---

## 17. Deployment

### 17.1 Dockerfile

```dockerfile
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/e-pourashava-api-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 17.2 Docker Compose (Development)

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - DB_URL=jdbc:postgresql://db:5432/pourashava
      - REDIS_HOST=redis
    depends_on:
      - db
      - redis

  db:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: pourashava
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

volumes:
  postgres_data:
```

### 17.3 Deployment Commands

```bash
# Build
mvn clean package -DskipTests

# Run locally
java -jar target/e-pourashava-api-1.0.0.jar --spring.profiles.active=dev

# Docker build & run
docker build -t e-pourashava-api:latest .
docker run -p 8080:8080 e-pourashava-api:latest

# Docker Compose
docker-compose up -d
```

---

## 18. Development Guidelines

### 18.1 Code Style

| Rule | Convention |
|------|------------|
| Classes | PascalCase (`UserService`, `AuthController`) |
| Methods | camelCase (`getUserById`, `createUser`) |
| Variables | camelCase (`userRepository`, `authService`) |
| Constants | UPPER_SNAKE_CASE (`MAX_FILE_SIZE`) |
| Packages | lowercase (`co.egen.pourashava.service`) |
| SQL tables | snake_case, plural (`users`, `service_requests`) |

### 18.2 Testing Strategy

| Type | Framework | Coverage Target |
|------|-----------|-----------------|
| Unit Tests | JUnit 5 + Mockito | 80%+ |
| Integration Tests | Spring Boot Test | Critical paths |
| API Tests | MockMvc / TestRestTemplate | All endpoints |

### 18.3 Git Workflow

```
main (production)
  └── develop (integration)
       ├── feature/xxx
       ├── bugfix/xxx
       └── release/x.x.x
```

### 18.4 Commit Convention

```
type(scope): description

Examples:
feat(auth): add JWT refresh token endpoint
fix(user): resolve duplicate email validation
docs(api): update Swagger annotations
```

---

## Appendix A: Environment Matrix

| Environment | Database | Cache | Log Level | Debug |
|-------------|----------|-------|-----------|-------|
| Development | Local PostgreSQL | Local Redis | DEBUG | Yes |
| QA | QA PostgreSQL | QA Redis | DEBUG | Yes |
| Staging | Staging PostgreSQL | Staging Redis | INFO | No |
| Production | Prod PostgreSQL | Prod Redis | INFO | No |

---

## Appendix B: API Endpoint Summary

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/v1/auth/login` | User login | No |
| POST | `/api/v1/auth/refresh` | Refresh token | Yes |
| GET | `/api/v1/users` | List users | Admin |
| GET | `/api/v1/users/{id}` | Get user | Admin |
| POST | `/api/v1/users` | Create user | Admin |
| PUT | `/api/v1/users/{id}` | Update user | Admin |
| DELETE | `/api/v1/users/{id}` | Delete user | Admin |
| GET | `/api/v1/citizens` | List citizens | User |
| GET | `/api/v1/citizens/{id}` | Get citizen | User |
| POST | `/api/v1/citizens` | Register citizen | Public |
| GET | `/api/v1/services` | List services | User |
| POST | `/api/v1/services` | Create service request | User |
| GET | `/api/v1/actuator/health` | Health check | No |
| GET | `/swagger-ui.html` | API docs | No |

---

*Document Version: 1.0*
*Last Updated: 2026-07-29*
