# OAuth2 Authentication Service Architecture Template

## Overview
This document provides a reusable architecture template for implementing OAuth2-based authentication microservices using Spring Boot. The architecture supports multi-tenant environments, JWT token management, and role-based access control.

## Technology Stack

### Core Framework
- **Spring Boot**: 2.6.x (or latest stable)
- **Java**: 11+ (LTS recommended)
- **Spring Security**: OAuth2 with JWT support
- **Spring Cloud**: Service discovery and configuration

### Persistence & Data
- **Database**: PostgreSQL (recommended) or MySQL
- **ORM**: Spring Data JPA with Hibernate
- **Query DSL**: Type-safe database queries
- **Connection Pool**: HikariCP

### Infrastructure
- **Service Discovery**: Eureka Server/Client
- **Message Queue**: Apache Kafka (for audit events)
- **Monitoring**: Micrometer with Prometheus
- **Containerization**: Docker
- **Orchestration**: Kubernetes (optional)

## Project Structure

```
authentication-service/
├── src/main/java/
│   └── com/yourcompany/authentication/
│       ├── AuthenticationApplication.java
│       ├── config/
│       │   ├── AuthorizationServerConfig.java
│       │   ├── SecurityConfig.java
│       │   ├── CustomAuthenticationManager.java
│       │   └── CustomTokenEnhancer.java
│       ├── controller/
│       │   ├── AuthController.java
│       │   ├── JwkController.java
│       │   └── HealthController.java
│       ├── entity/
│       │   ├── User.java
│       │   ├── Role.java
│       │   ├── UserRole.java
│       │   └── ApiInfo.java
│       ├── repository/
│       │   └── UserRepository.java
│       ├── service/
│       │   ├── AuthService.java
│       │   └── ValidatorService.java
│       └── dto/
│           ├── LoginRequestDto.java
│           └── AuthResponseDto.java
├── src/main/resources/
│   ├── application.yml
│   ├── application-{profile}.yml
│   └── {keystore}.jks
├── db/
│   └── schema.sql
├── Dockerfile
├── pom.xml
└── README.md
```

## Core Components

### 1. OAuth2 Authorization Server Configuration

```java
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) {
        clients.inMemory()
            .withClient("your-client-id")
            .secret("{noop}your-client-secret")
            .authorizedGrantTypes("password", "refresh_token")
            .scopes("read", "write")
            .accessTokenValiditySeconds(3600)
            .refreshTokenValiditySeconds(7200);
    }
    
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        // Configure JWT with RSA signing
    }
    
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }
}
```

### 2. Security Configuration

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/oauth/token", "/.well-known/jwks.json").permitAll()
            .anyRequest().authenticated();
    }
}
```

### 3. Custom Authentication Manager

```java
@Component
public class CustomAuthenticationManager implements AuthenticationManager {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        // Custom authentication logic
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new BadCredentialsException("User not found"));
        
        if (passwordEncoder.matches(password, user.getPassword())) {
            return new UsernamePasswordAuthenticationToken(username, password, getAuthorities(user));
        }
        
        throw new BadCredentialsException("Invalid credentials");
    }
}
```

### 4. JWT Token Enhancer

```java
public class CustomTokenEnhancer implements TokenEnhancer {
    
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Map<String, Object> additionalInfo = new HashMap<>();
        
        // Add custom claims
        additionalInfo.put("user_id", getUserId(authentication));
        additionalInfo.put("tenant_id", getTenantId(authentication));
        additionalInfo.put("roles", getRoles(authentication));
        
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}
```

## Database Schema

### Core Tables

```sql
-- Users table
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    enabled BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Roles table
CREATE TABLE roles (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User roles junction table
CREATE TABLE user_roles (
    user_id VARCHAR(36) REFERENCES users(id),
    role_id VARCHAR(36) REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);

-- API permissions table
CREATE TABLE api_permissions (
    id VARCHAR(36) PRIMARY KEY,
    method VARCHAR(10) NOT NULL,
    uri VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Role permissions junction table
CREATE TABLE role_permissions (
    role_id VARCHAR(36) REFERENCES roles(id),
    permission_id VARCHAR(36) REFERENCES api_permissions(id),
    PRIMARY KEY (role_id, permission_id)
);
```

## Configuration Templates

### Application Configuration

```yaml
server:
  port: ${PORT:8080}
  servlet:
    context-path: /api/v1/auth

spring:
  application:
    name: authentication-service
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
  
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:auth_db}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:password}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
  
  jpa:
    hibernate:
      ddl-auto: validate
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    show-sql: false

eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_URL:http://localhost:8761/eureka}
  instance:
    prefer-ip-address: true

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

### Environment-Specific Configurations

```yaml
# application-dev.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/auth_dev
    username: dev_user
    password: dev_password
  jpa:
    show-sql: true

# application-prod.yml
spring:
  datasource:
    url: jdbc:postgresql://prod-db:5432/auth_prod
    username: ${DB_PROD_USER}
    password: ${DB_PROD_PASSWORD}
  jpa:
    show-sql: false
```

## API Endpoints

### Authentication Endpoints

```java
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginRequest request) {
        // Authenticate user and return JWT token
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        // Refresh JWT token
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest request) {
        // Logout user (if using token blacklist)
    }
}
```

### JWK Endpoint (for token validation)

```java
@RestController
public class JwkController {
    
    @GetMapping("/.well-known/jwks.json")
    public ResponseEntity<Map<String, Object>> jwks() {
        // Return public key set for JWT validation
    }
}
```

## Deployment Configuration

### Dockerfile

```dockerfile
FROM openjdk:11-jre-slim

LABEL maintainer="Your Team"

COPY target/authentication-service.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Docker Compose

```yaml
version: '3.8'
services:
  authentication-service:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_HOST=postgres
      - DB_USERNAME=auth_user
      - DB_PASSWORD=auth_password
    depends_on:
      - postgres
      - eureka
  
  postgres:
    image: postgres:13
    environment:
      - POSTGRES_DB=auth_db
      - POSTGRES_USER=auth_user
      - POSTGRES_PASSWORD=auth_password
    volumes:
      - postgres_data:/var/lib/postgresql/data
  
  eureka:
    image: your-eureka-image:latest
    ports:
      - "8761:8761"

volumes:
  postgres_data:
```

## Implementation Checklist

### Setup Phase
- [ ] Create Spring Boot project with required dependencies
- [ ] Set up database schema and initial data
- [ ] Configure OAuth2 authorization server
- [ ] Implement JWT token management
- [ ] Create RSA key pair for token signing

### Development Phase
- [ ] Implement custom authentication manager
- [ ] Create user and role entities
- [ ] Build authentication endpoints
- [ ] Implement token enhancement logic
- [ ] Add JWK endpoint for token validation

### Security Phase
- [ ] Configure password encoding
- [ ] Implement role-based access control
- [ ] Add input validation and sanitization
- [ ] Set up CORS configuration
- [ ] Implement rate limiting

### Integration Phase
- [ ] Configure service discovery (Eureka)
- [ ] Set up Kafka for audit events
- [ ] Add monitoring and health checks
- [ ] Configure logging and metrics
- [ ] Test with external services

### Deployment Phase
- [ ] Create Docker configuration
- [ ] Set up environment-specific configs
- [ ] Configure CI/CD pipeline
- [ ] Set up Kubernetes manifests
- [ ] Implement blue-green deployment

## Best Practices

### Security
- Use strong password hashing (BCrypt)
- Implement proper token expiration
- Use HTTPS in production
- Validate all input parameters
- Implement proper error handling

### Performance
- Use connection pooling for database
- Cache frequently accessed data
- Implement proper logging levels
- Monitor JWT token size
- Use async processing where possible

### Scalability
- Design for horizontal scaling
- Use stateless authentication
- Implement proper load balancing
- Configure health checks
- Use container orchestration

### Maintainability
- Follow Spring Boot conventions
- Use proper package structure
- Implement comprehensive testing
- Document API endpoints
- Use version control best practices

## Migration Guide

### From Basic Auth to OAuth2
1. Add OAuth2 dependencies
2. Configure authorization server
3. Update authentication endpoints
4. Migrate user data if needed
5. Update client applications

### From Session-based to JWT
1. Remove session configuration
2. Add JWT token store
3. Update authentication flow
4. Implement token validation
5. Update logout mechanism

## Troubleshooting

### Common Issues
- **Token validation failures**: Check RSA key configuration
- **Database connection issues**: Verify connection pool settings
- **CORS problems**: Configure proper CORS settings
- **Service discovery**: Check Eureka configuration
- **Memory leaks**: Monitor token storage and cleanup

### Debug Configuration
```yaml
logging:
  level:
    org.springframework.security: DEBUG
    org.springframework.oauth2: DEBUG
    com.yourcompany.auth: DEBUG
```

## References

- [Spring Security OAuth2 Documentation](https://spring.io/projects/spring-security-oauth)
- [JWT Specification](https://tools.ietf.org/html/rfc7519)
- [OAuth2 Best Practices](https://oauth.net/articles/)
- [Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)

---

*This template provides a comprehensive foundation for implementing OAuth2 authentication services. Adapt the configurations and implementations based on your specific requirements and infrastructure.*
