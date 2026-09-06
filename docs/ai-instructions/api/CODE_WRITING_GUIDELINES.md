# SCM Service Code Writing Guidelines

## General Principles

### 1. **Code Quality Standards**
- Write clean, readable, and maintainable code
- Follow SOLID principles consistently
- Keep methods and classes focused on single responsibilities
- Use meaningful names for variables, methods, and classes
- Write self-documenting code with minimal comments

### 2. **Java Version Compliance**
- Use Java 11 features appropriately
- Leverage `var` for local variable type inference when type is obvious
- Use `Optional` properly for null safety
- Utilize Stream API for functional programming

## Naming Conventions

### 1. **Class Names**
```java
// Good
public class PurchaseOrderService { }
public class ItemStockDetailValidator { }
public class SalesQueryService { }

// Bad
public class POServ { }
public class ItemStockValid { }
public class SalesQS { }
```

### 2. **Method Names**
```java
// Good
public List<Sales> findSalesByStoreId(String storeId) { }
public void validateSalesItem(SalesItemDto salesItem) { }
public SalesResponse createSales(SalesDto salesDto) { }

// Bad
public List<Sales> getSales(String id) { }
public void validate(SalesItemDto dto) { }
public SalesResponse create(SalesDto dto) { }
```

### 3. **Variable Names**
```java
// Good
private final SalesQueryService salesQueryService;
private List<SalesItem> salesItemList;
private String purchaseOrderId;

// Bad
private final SalesQueryService sqs;
private List<SalesItem> list;
private String id;
```

### 4. **Constant Names**
```java
// Good
public static final String DEFAULT_STORE_ID = "DEFAULT_STORE";
public static final int MAX_RETRY_ATTEMPTS = 3;
public static final long LOCK_TIMEOUT_SECONDS = 30L;

// Bad
public static final String storeId = "DEFAULT_STORE";
public static final int retry = 3;
```

## Code Structure Guidelines

### 1. **Class Organization**
```java
@Service
@Slf4j
@RequiredArgsConstructor
public class SalesService {
  
  // 1. Static fields
  private static final String ERROR_MESSAGE = "Sales processing failed";
  
  // 2. Final instance fields (dependencies)
  private final SalesValidatorService salesValidatorService;
  private final SalesCommand salesCommand;
  private final SalesQueryService salesQueryService;
  
  // 3. Non-final instance fields
  private final ReentrantLock lock = new ReentrantLock();
  
  // 4. Public methods
  @Transactional
  public SalesResponse saveSales(SalesDto salesDto) {
    // implementation
  }
  
  // 5. Private methods
  private void validateSalesData(SalesDto salesDto) {
    // implementation
  }
}
```

### 2. **Method Structure**
```java
// Good - Clear method with single responsibility
public SalesResponse createSales(SalesDto salesDto) {
  // 1. Input validation
  salesValidatorService.validateSalesDto(salesDto);
  
  // 2. Business logic
  Sales sales = salesMapper.toEntity(salesDto);
  sales = salesCommand.save(sales);
  
  // 3. Post-processing
  publishSalesCreatedEvent(sales);
  
  // 4. Return response
  return salesMapper.toResponse(sales);
}

// Bad - Method doing too many things
public SalesResponse createSales(SalesDto salesDto) {
  // Validation, business logic, external calls, mapping all mixed
  if (salesDto == null) throw new IllegalArgumentException();
  Sales sales = new Sales();
  sales.setName(salesDto.getName());
  // ... 50 lines of mixed logic
  return response;
}
```

## Exception Handling Guidelines

### 1. **Exception Types**
```java
// Use domain-specific exceptions
public class SalesValidationException extends UserInformException {
  public SalesValidationException(String message) {
    super(message);
  }
}

// Use for business rule violations
public class InsufficientStockException extends UserInformException {
  public InsufficientStockException(String itemId, double requested, double available) {
    super(String.format("Insufficient stock for item %s. Requested: %.2f, Available: %.2f", 
                       itemId, requested, available));
  }
}
```

### 2. **Exception Handling Patterns**
```java
// Good - Specific exception handling with proper logging
@Transactional
public SalesResponse saveSales(SalesDto salesDto) {
  try {
    validateSalesData(salesDto);
    return processSales(salesDto);
  } catch (SalesValidationException e) {
    log.warn("Sales validation failed: {}", e.getMessage());
    throw e; // Re-throw for proper HTTP response
  } catch (Exception e) {
    log.error("Unexpected error during sales processing", e);
    throw new UserInformException("Sales processing failed. Please try again.");
  }
}

// Bad - Catching all exceptions generically
@Transactional
public SalesResponse saveSales(SalesDto salesDto) {
  try {
    // business logic
  } catch (Exception e) {
    log.error("Error", e);
    return null; // Swallowing exceptions
  }
}
```

## Transaction Management Guidelines

### 1. **Transaction Boundaries**
```java
// Good - Transactional at service level
@Service
@RequiredArgsConstructor
public class SalesService {
  
  @Transactional
  public SalesResponse createSales(SalesDto salesDto) {
    // All database operations within single transaction
  }
  
  @Transactional(readOnly = true)
  public List<Sales> searchSales(SalesSearchDto searchDto) {
    // Read-only operations
  }
}

// Bad - Transactional at repository level
@Repository
public class SalesRepository {
  
  @Transactional
  public Sales save(Sales sales) {
    // Transactions should be at service layer
  }
}
```

### 2. **Transaction Rollback**
```java
// Good - Proper rollback handling
@Transactional
public SalesResponse saveSales(SalesDto salesDto) {
  String invoiceId = null;
  try {
    // Save sales data
    Sales sales = saveSalesData(salesDto);
    
    // Create invoice in external service
    InvoiceResponse invoiceResponse = accountingService.createInvoice(sales);
    invoiceId = invoiceResponse.getInvoiceId();
    
    // Update sales with invoice ID
    sales.setInvoiceId(invoiceId);
    salesCommand.update(sales);
    
    return salesMapper.toResponse(sales);
    
  } catch (Exception e) {
    // Compensating transaction
    if (invoiceId != null) {
      accountingService.cancelInvoice(invoiceId);
    }
    throw new UserInformException("Sales processing failed");
  }
}
```

## Validation Guidelines

### 1. **Input Validation**
```java
// Good - Comprehensive validation
@Component
public class SalesValidatorService {
  
  public void validateSalesDto(SalesDto salesDto) {
    if (salesDto == null) {
      throw new SalesValidationException("Sales data cannot be null");
    }
    
    if (CollectionUtils.isEmpty(salesDto.getSalesItemList())) {
      throw new SalesValidationException("Sales must contain at least one item");
    }
    
    if (StringUtils.isBlank(salesDto.getStoreId())) {
      throw new SalesValidationException("Store ID is required");
    }
    
    validateSalesItems(salesDto.getSalesItemList());
    validateSalesPayments(salesDto.getSalesPaymentList());
  }
  
  private void validateSalesItems(List<SalesItemDto> salesItems) {
    salesItems.forEach(this::validateSalesItem);
  }
  
  private void validateSalesItem(SalesItemDto salesItem) {
    if (StringUtils.isBlank(salesItem.getItemId())) {
      throw new SalesValidationException("Item ID is required for sales items");
    }
    
    if (salesItem.getQuantity() <= 0) {
      throw new SalesValidationException("Item quantity must be greater than zero");
    }
  }
}
```

### 2. **DTO Validation with Annotations**
```java
// Good - Using JSR-303 annotations
public class SalesDto {
  
  @NotBlank(message = "Store ID is required")
  private String storeId;
  
  @NotNull(message = "Sales date is required")
  private LocalDate salesDate;
  
  @NotEmpty(message = "Sales items cannot be empty")
  @Valid
  private List<SalesItemDto> salesItemList;
  
  @Valid
  private List<SalesPaymentDto> salesPaymentList;
}

public class SalesItemDto {
  
  @NotBlank(message = "Item ID is required")
  private String itemId;
  
  @DecimalMin(value = "0.01", message = "Quantity must be greater than zero")
  private Double quantity;
  
  @DecimalMin(value = "0.0", message = "Price cannot be negative")
  private Double unitPrice;
}
```

## Database Access Guidelines

### 1. **Repository Usage**
```java
// Good - Using QueryDSL for complex queries
@Repository
public interface SalesRepository extends JpaRepository<Sales, String> {
  
  // Simple queries with method naming convention
  List<Sales> findByStoreIdAndSalesDateBetween(String storeId, LocalDate startDate, LocalDate endDate);
  
  // Complex queries with QueryDSL
  @Query("SELECT s FROM Sales s WHERE s.store.id = :storeId AND s.salesDate = :date")
  List<Sales> findSalesByStoreAndDate(@Param("storeId") String storeId, @Param("date") LocalDate date);
}

// Good - Query service for complex queries
@Service
@RequiredArgsConstructor
public class SalesQueryService {
  
  private final EntityManager entityManager;
  
  public Page<Sales> searchSales(SalesSearchDto searchDto) {
    QSales qSales = QSales.sales;
    
    JPAQuery<Sales> query = new JPAQuery<>(entityManager)
        .from(qSales)
        .where(SalesPredicate.buildSearchPredicate(searchDto))
        .orderBy(qSales.createdDate.desc());
    
    // Apply pagination
    Pageable pageable = PageRequest.of(searchDto.getPage(), searchDto.getSize());
    query.limit(pageable.getPageSize()).offset(pageable.getOffset());
    
    List<Sales> results = query.fetch();
    long total = query.fetchCount();
    
    return new PageImpl<>(results, pageable, total);
  }
}
```

### 2. **Entity Design**
```java
// Good - Proper entity design with relationships
@Entity
@Table(name = "sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sales extends Auditable {
  
  @Id
  private String id;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_id", nullable = false)
  private Store store;
  
  @Column(name = "invoice_nc_id", unique = true)
  private String invoiceNcId;
  
  @OneToMany(mappedBy = "sales", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<SalesItem> salesItemList = new ArrayList<>();
  
  @OneToMany(mappedBy = "sales", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<SalesPayment> salesPaymentList = new ArrayList<>();
  
  @Column(name = "total_amount", precision = 19, scale = 2)
  private BigDecimal totalAmount;
  
  @Enumerated(EnumType.STRING)
  private SourceEnum salesSource;
}
```

## API Design Guidelines

### 1. **Controller Structure**
```java
// Good - Clean controller with proper separation
@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@Slf4j
public class SalesController {
  
  private final SalesService salesService;
  
  @PostMapping
  public ResponseEntity<SalesResponse> createSales(@Valid @RequestBody SalesDto salesDto) {
    SalesResponse response = salesService.saveSales(salesDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
  
  @GetMapping("/{salesId}")
  public ResponseEntity<SalesResponse> getSales(@PathVariable String salesId) {
    SalesResponse response = salesService.getSalesById(salesId);
    return ResponseEntity.ok(response);
  }
  
  @GetMapping
  public ResponseEntity<Page<SalesResponse>> searchSales(
      @ModelAttribute SalesSearchDto searchDto,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    
    searchDto.setPage(page);
    searchDto.setSize(size);
    
    Page<SalesResponse> response = salesService.searchSales(searchDto);
    return ResponseEntity.ok(response);
  }
}
```

### 2. **Response Structure**
```java
// Good - Consistent response structure
@Data
@Builder
public class ApiResponse<T> {
  private boolean success;
  private String message;
  private T data;
  private List<String> errors;
  private long timestamp;
  
  public static <T> ApiResponse<T> success(T data) {
    return ApiResponse.<T>builder()
        .success(true)
        .data(data)
        .timestamp(System.currentTimeMillis())
        .build();
  }
  
  public static <T> ApiResponse<T> error(String message, List<String> errors) {
    return ApiResponse.<T>builder()
        .success(false)
        .message(message)
        .errors(errors)
        .timestamp(System.currentTimeMillis())
        .build();
  }
}
```

## Testing Guidelines

### 1. **Unit Test Structure**
```java
// Good - Comprehensive unit test
@ExtendWith(MockitoExtension.class)
class SalesServiceTest {
  
  @Mock
  private SalesValidatorService salesValidatorService;
  
  @Mock
  private SalesCommand salesCommand;
  
  @Mock
  private SalesMapper salesMapper;
  
  @InjectMocks
  private SalesService salesService;
  
  @Test
  @DisplayName("Should create sales successfully when valid data provided")
  void createSales_Success() {
    // Given
    SalesDto salesDto = createValidSalesDto();
    Sales sales = createSalesEntity();
    SalesResponse expectedResponse = createSalesResponse();
    
    when(salesValidatorService.validateAndReturn(salesDto)).thenReturn(sales);
    when(salesCommand.save(sales)).thenReturn(sales);
    when(salesMapper.apply(sales)).thenReturn(expectedResponse);
    
    // When
    SalesResponse actualResponse = salesService.saveSales(salesDto);
    
    // Then
    assertThat(actualResponse).isEqualTo(expectedResponse);
    verify(salesValidatorService).validateAndReturn(salesDto);
    verify(salesCommand).save(sales);
    verify(salesMapper).apply(sales);
  }
  
  @Test
  @DisplayName("Should throw exception when invalid sales data provided")
  void createSales_InvalidData_ThrowsException() {
    // Given
    SalesDto invalidSalesDto = createInvalidSalesDto();
    
    when(salesValidatorService.validateAndReturn(invalidSalesDto))
        .thenThrow(new SalesValidationException("Invalid sales data"));
    
    // When & Then
    assertThatThrownBy(() -> salesService.saveSales(invalidSalesDto))
        .isInstanceOf(SalesValidationException.class)
        .hasMessage("Invalid sales data");
    
    verify(salesValidatorService).validateAndReturn(invalidSalesDto);
    verifyNoInteractions(salesCommand, salesMapper);
  }
}
```

### 2. **Integration Test Structure**
```java
// Good - Integration test with test database
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class SalesRepositoryIntegrationTest {
  
  @Autowired
  private SalesRepository salesRepository;
  
  @Autowired
  private TestEntityManager entityManager;
  
  @Test
  void findByStoreId_ShouldReturnSales_WhenSalesExist() {
    // Given
    Store store = createAndPersistStore();
    Sales sales = createSales(store);
    entityManager.persistAndFlush(sales);
    
    // When
    List<Sales> foundSales = salesRepository.findByStoreId(store.getId());
    
    // Then
    assertThat(foundSales).hasSize(1);
    assertThat(foundSales.get(0).getId()).isEqualTo(sales.getId());
  }
}
```

## Performance Guidelines

### 1. **Database Optimization**
```java
// Good - Using fetch joins to prevent N+1 queries
public List<Sales> getSalesWithDetails(String storeId) {
  QSales qSales = QSales.sales;
  QSalesItem qSalesItem = QSalesItem.salesItem;
  
  return new JPAQuery<>(entityManager)
      .from(qSales)
      .leftJoin(qSales.salesItemList, qSalesItem).fetchJoin()
      .where(qSales.store.id.eq(storeId))
      .fetch();
}

// Good - Pagination for large result sets
public Page<Sales> searchSales(SalesSearchDto searchDto) {
  Pageable pageable = PageRequest.of(searchDto.getPage(), searchDto.getSize());
  
  JPAQuery<Sales> query = new JPAQuery<>(entityManager)
      .from(QSales.sales)
      .where(SalesPredicate.buildSearchPredicate(searchDto));
  
  List<Sales> results = query
      .limit(pageable.getPageSize())
      .offset(pageable.getOffset())
      .fetch();
  
  long total = query.fetchCount();
  
  return new PageImpl<>(results, pageable, total);
}
```

### 2. **Caching Strategy**
```java
// Good - Method-level caching
@Service
@RequiredArgsConstructor
public class InventoryItemService {
  
  @Cacheable(value = "inventoryItems", key = "#itemId")
  public InventoryItem getItemById(String itemId) {
    return inventoryItemRepository.findById(itemId)
        .orElseThrow(() -> new ItemNotFoundException(itemId));
  }
  
  @CacheEvict(value = "inventoryItems", key = "#itemId")
  public void updateItem(String itemId, InventoryItemDto itemDto) {
    InventoryItem item = getItemById(itemId);
    inventoryItemMapper.updateEntityFromDto(itemDto, item);
    inventoryItemRepository.save(item);
  }
}
```

## Security Guidelines

### 1. **Input Sanitization**
```java
// Good - Proper input validation and sanitization
@Component
public class InputSanitizer {
  
  public String sanitizeString(String input) {
    if (StringUtils.isBlank(input)) {
      return "";
    }
    
    // Remove potentially dangerous characters
    return input.replaceAll("[<>\"'&]", "");
  }
  
  public void validateStoreAccess(String storeId) {
    User currentUser = currentUserProvider.getCurrentUser();
    
    if (!currentUser.getStoreIds().contains(storeId)) {
      throw new AccessDeniedException("User does not have access to store: " + storeId);
    }
  }
}
```

### 2. **Secure Data Handling**
```java
// Good - Secure handling of sensitive data
@Service
@RequiredArgsConstructor
public class SecureSalesService {
  
  private final PasswordEncoder passwordEncoder;
  
  public void processSecureSalesData(SecureSalesDto salesDto) {
    // Encrypt sensitive data before storage
    String encryptedPaymentInfo = encryptPaymentInfo(salesDto.getPaymentInfo());
    
    // Never log sensitive information
    log.info("Processing sales for store: {}", salesDto.getStoreId());
    // Avoid: log.info("Processing sales with payment: {}", salesDto.getPaymentInfo());
  }
  
  private String encryptPaymentInfo(String paymentInfo) {
    // Implementation for encryption
    return paymentEncoder.encode(paymentInfo);
  }
}
```

## Logging Guidelines

### 1. **Proper Logging Levels**
```java
// Good - Appropriate logging levels and structured logging
@Service
@Slf4j
public class SalesService {
  
  public SalesResponse createSales(SalesDto salesDto) {
    log.info("Starting sales creation for store: {}, items: {}", 
             salesDto.getStoreId(), salesDto.getSalesItemList().size());
    
    try {
      SalesResponse response = processSales(salesDto);
      
      log.info("Sales created successfully with invoice ID: {}", response.getInvoiceNcId());
      return response;
      
    } catch (SalesValidationException e) {
      log.warn("Sales validation failed for store: {}, reason: {}", 
               salesDto.getStoreId(), e.getMessage());
      throw e;
      
    } catch (Exception e) {
      log.error("Unexpected error during sales creation for store: {}", 
                salesDto.getStoreId(), e);
      throw new UserInformException("Sales processing failed");
    }
  }
}
```

### 2. **Structured Logging**
```java
// Good - Structured logging with correlation ID
@Component
public class LoggingUtils {
  
  public static void logWithCorrelation(String level, String message, Object... args) {
    String correlationId = MDC.get("correlationId");
    
    String logMessage = String.format("[correlation-id: %s] %s", correlationId, message);
    
    switch (level.toLowerCase()) {
      case "info":
        log.info(logMessage, args);
        break;
      case "warn":
        log.warn(logMessage, args);
        break;
      case "error":
        log.error(logMessage, args);
        break;
      default:
        log.debug(logMessage, args);
    }
  }
}
```

## Code Review Guidelines

### 1. **Review Checklist**
- [ ] Code follows naming conventions
- [ ] Methods have single responsibility
- [ ] Proper error handling implemented
- [ ] Transactions are correctly placed
- [ ] Input validation is comprehensive
- [ ] Database queries are optimized
- [ ] Security considerations are addressed
- [ ] Logging is appropriate and doesn't expose sensitive data
- [ ] Tests cover critical scenarios
- [ ] Documentation is clear and concise

### 2. **Common Issues to Watch For**
- **N+1 Query Problems**: Use fetch joins or batch loading
- **Transaction Boundaries**: Ensure proper scoping
- **Exception Swallowing**: Never swallow exceptions without handling
- **Hard-coded Values**: Use constants or configuration
- **Memory Leaks**: Proper resource cleanup
- **Thread Safety**: Consider concurrent access
- **Performance**: Monitor for slow operations
- **Security**: Validate all inputs and sanitize outputs

This comprehensive set of guidelines should help maintain consistent, high-quality code across the SCM service project.
