# SCM Service Architecture Rules

## Project Overview
This is a Spring Boot microservice for Supply Chain Management (SCM) in healthcare, built with Java 11, Spring Boot 2.6.2, and following a layered architecture pattern.

## Core Architecture

### 1. **Layer Structure**
```
src/main/java/co/egen/eghealth/scm/
├── controller/          # REST API endpoints
├── service/            # Business logic layer
├── repository/         # Data access layer
├── entity/            # JPA entities
├── dto/               # Data Transfer Objects
├── config/            # Configuration classes
├── proxy/             # External service integrations
├── util/              # Utility classes
└── enums/             # Enum definitions
```

### 2. **Service Layer Organization**
The service layer follows a **Command Query Responsibility Segregation (CQRS)** pattern:

```
service/
├── command/           # Write operations (create, update, delete)
├── query/             # Read operations
├── predicate/         # QueryDSL predicates for dynamic queries
├── validator/         # Input validation logic
├── mapper/            # Object mapping between layers
└── strategy/          # Strategy pattern implementations
```

### 3. **Controller Layer Rules**
- **Package**: `controller/`
- **Naming**: `[EntityName]Controller.java`
- **Annotations**: `@RestController`, `@RequestMapping`, `@RequiredArgsConstructor`
- **Dependencies**: Inject services only, never repositories
- **DTO Packages**:
  - `request_dto/` - Request payloads
  - `response_dto/` - Response payloads  
  - `search_dto/` - Search/filter parameters

### 4. **Service Layer Rules**
- **Package**: `service/`
- **Naming**: `[EntityName]Service.java`
- **Annotations**: `@Service`, `@Slf4j`, `@RequiredArgsConstructor`
- **Transaction Management**: Use `@Transactional` on write operations
- **Dependency Injection**: Inject repositories, mappers, validators, and other services
- **Business Logic**: All business logic must reside in service layer

### 5. **Repository Layer Rules**
- **Package**: `repository/`
- **Naming**: `[EntityName]Repository.java`
- **Annotations**: `@Repository`
- **Inheritance**: Extend `JpaRepository<Entity, Id>`
- **Custom Queries**: Use QueryDSL for complex queries via predicate classes

### 6. **Entity Layer Rules**
- **Package**: `entity/`
- **Naming**: `[EntityName].java`
- **Annotations**: `@Entity`, `@Table`, JPA annotations
- **Base Class**: Extend `Auditable.java` for audit fields
- **Relationships**: Use proper JPA relationship annotations
- **QueryDSL**: Q-classes auto-generated for type-safe queries

### 7. **DTO Layer Rules**
- **Package**: `dto/`
- **Organization**: Group by purpose (request/response/report)
- **Validation**: Use `@Valid` and JSR-303 annotations
- **Immutability**: Prefer immutable DTOs with `@Value` or final fields
- **Mapping**: Use ModelMapper for entity-DTO conversions

## Design Patterns

### 1. **CQRS Pattern**
- **Command Services**: Handle write operations (`[EntityName]Command.java`)
- **Query Services**: Handle read operations (`[EntityName]QueryService.java`)
- **Separation**: Strict separation between read and write operations

### 2. **Strategy Pattern**
- **Package**: `service/strategy/`
- **Usage**: For business rules that vary based on conditions
- **Example**: `StockHandlerService` for different stock operations

### 3. **Predicate Pattern**
- **Package**: `service/predicate/`
- **Purpose**: Build dynamic queries using QueryDSL BooleanBuilder
- **Naming**: `[EntityName]Predicate.java`

### 4. **Validator Pattern**
- **Package**: `service/validator/`
- **Naming**: `[EntityName]ValidatorService.java`
- **Purpose**: Centralized validation logic with descriptive error messages

## Technology Stack Rules

### 1. **Database**
- **Primary**: PostgreSQL
- **ORM**: Spring Data JPA with Hibernate
- **Query Builder**: QueryDSL for type-safe queries
- **Connection Pool**: HikariCP

### 2. **Caching**
- **Provider**: Redis
- **Usage**: Cache frequently accessed data and external API responses
- **Configuration**: `RedisConfig.java`

### 3. **Security**
- **Framework**: Spring Security with OAuth2
- **Authorization**: Role-based access control
- **Current User**: Use `CurrentUserProvider` for user context

### 4. **External Services**
- **Communication**: OpenFeign for service-to-service calls
- **Services**: Registration, Accounting, Clinical services
- **Proxy Pattern**: `proxy/` package with `[ServiceName]ProxyService.java`

## Code Organization Rules

### 1. **Package Structure**
- Follow domain-driven design with clear package boundaries
- Each major entity has its own sub-packages
- Shared components in appropriate utility packages

### 2. **Naming Conventions**
- **Classes**: PascalCase, descriptive names
- **Methods**: camelCase, verb-noun pattern for actions
- **Variables**: camelCase, meaningful names
- **Constants**: UPPER_SNAKE_CASE

### 3. **Dependency Management**
- Use constructor injection (`@RequiredArgsConstructor`)
- Avoid field injection
- Keep dependency graphs shallow

## Transaction Management Rules

### 1. **Transaction Boundaries**
- Define at service method level
- Use `@Transactional` for write operations
- Read-only operations: `@Transactional(readOnly = true)`

### 2. **Rollback Strategy**
- Use compensating transactions for external service calls
- Implement proper error handling and rollback mechanisms
- Example: Sales service with accounting integration

### 3. **Concurrency Control**
- Use `ReentrantLock` for critical sections
- Implement optimistic locking where appropriate
- Handle concurrent access to shared resources

## Error Handling Rules

### 1. **Exception Hierarchy**
- Use `UserInformException` for user-facing errors
- Log technical exceptions appropriately
- Provide meaningful error messages

### 2. **Validation Errors**
- Centralized validation in validator services
- Use JSR-303 annotations for DTO validation
- Return descriptive validation error responses

## Testing Rules

### 1. **Test Structure**
- **Unit Tests**: `src/test/java/` with same package structure
- **Integration Tests**: Test service layers with in-memory database
- **Naming**: `[ClassName]Test.java`

### 2. **Test Coverage**
- Test business logic thoroughly
- Mock external dependencies
- Test both happy path and error scenarios

## Configuration Rules

### 1. **Profiles**
- **Development**: `dev` profile
- **QA**: `qa` profile  
- **Staging**: `stage` profile
- **Production**: `prod` profile

### 2. **Configuration Files**
- **Main**: `application.yml`
- **Profile-specific**: `application-{profile}.yml`
- **Bootstrap**: `bootstrap.yml` for cloud config

## Performance Rules

### 1. **Database Optimization**
- Use fetch joins to prevent N+1 queries
- Implement proper indexing strategies
- Use pagination for large result sets

### 2. **Caching Strategy**
- Cache frequently accessed reference data
- Use appropriate cache TTL values
- Implement cache invalidation strategies

### 3. **External Service Calls**
- Use circuit breakers for resilience
- Implement retry mechanisms with exponential backoff
- Cache external service responses where appropriate

## Security Rules

### 1. **Authentication**
- OAuth2-based authentication
- JWT tokens for stateless authentication
- Proper token validation

### 2. **Authorization**
- Role-based access control (RBAC)
- Resource-level permissions
- Method-level security annotations

### 3. **Data Security**
- Encrypt sensitive data
- Use parameterized queries to prevent SQL injection
- Validate all input data

## Monitoring and Logging Rules

### 1. **Logging**
- Use SLF4J with Logback
- Structured logging with correlation IDs
- Different log levels for different environments

### 2. **Monitoring**
- Spring Boot Actuator endpoints
- Prometheus metrics integration
- Health check endpoints

### 3. **Distributed Tracing**
- Correlation IDs for request tracing
- Log external service calls
- Monitor transaction boundaries

## Deployment Rules

### 1. **Containerization**
- Use Docker for containerization
- Multi-stage builds for optimization
- Proper health checks

### 2. **Configuration Management**
- Externalize configuration
- Use environment variables for secrets
- Profile-specific configurations

### 3. **Scalability**
- Design for horizontal scaling
- Use stateless service design
- Implement proper session management
