# NFR Design Patterns - Unit 3: Admin Backend

## 개요
Unit 1 (Common/Shared)의 NFR Design 패턴을 기반으로, Admin Backend API 모듈에 특화된 설계 패턴을 정의합니다. Unit 1에서 정의된 공통 패턴(JWT Filter Chain, Security Headers, Rate Limiting, Input Validation, Connection Pooling, DB Indexing, Global Exception Handler, Structured Logging, Store-Scoped Data Access)은 모두 적용되며, 이 문서는 Admin Backend 고유의 보충 패턴을 다룹니다.

---

## 1. 인증/인가 패턴

### Pattern: Admin Authentication Flow
**적용 NFR**: ANFR-SEC-01, ANFR-SEC-02
```
AdminAuthController.login()
  → AdminAuthService.login(storeCode, username, password)
    → StoreRepository.findByCode(storeCode)
    → AdminRepository.findByStoreIdAndUsername(storeId, username)
    → 계정 잠금 확인 (admin.isLocked())
    → PasswordEncoder.matches(password, admin.password)
    → 실패 시: incrementLoginAttempts(), 5회 초과 시 lock(15분)
    → 성공 시: resetLoginAttempts(), JwtTokenProvider.createAdminToken()
  → return TokenResponse
```

**보안 설계 원칙**:
- 매장/사용자/비밀번호 중 어느 것이 틀렸는지 구분하지 않는 동일 에러 메시지 (정보 노출 방지)
- 계정 잠금은 DB 레벨에서 관리 (lockedUntil 필드)
- 잠금 해제는 시간 기반 자동 해제 (별도 관리자 개입 불필요)

### Pattern: Admin Role Enforcement
**적용 NFR**: ANFR-SEC-02, SECURITY-08
```
모든 Admin API 요청:
  1. JwtAuthenticationFilter → SecurityContext에 AuthenticatedUser 설정
  2. StoreAccessInterceptor → 경로의 storeId == 토큰의 storeId 검증
  3. Controller → AuthenticatedUser에서 역할/storeId 추출하여 비즈니스 로직에 전달
```

**접근 제어 매트릭스**:
| 엔드포인트 | 인증 | 역할 | 매장 격리 |
|---|---|---|---|
| POST /api/admin/auth/login | 불필요 | - | - |
| POST /api/admin/auth/register | 불필요 | - | - |
| POST /api/stores | 불필요 | - | - |
| GET /api/stores/{storeCode} | 불필요 | - | - |
| /api/stores/{storeId}/** | 필수 | ADMIN | StoreAccessInterceptor |

---

## 2. SSE 실시간 통신 패턴

### Pattern: Store-Scoped SSE Broadcasting
**적용 NFR**: ANFR-PERF-02, ANFR-SCALE-01, ANFR-AVAIL-01
```
SSEService
  ├── ConcurrentHashMap<Long, CopyOnWriteArrayList<SseEmitter>> storeEmitters
  │
  ├── subscribe(storeId):
  │     1. SseEmitter emitter = new SseEmitter(30분)
  │     2. storeEmitters.computeIfAbsent(storeId, k -> new CopyOnWriteArrayList<>()).add(emitter)
  │     3. emitter.onCompletion/onTimeout/onError → removeEmitter()
  │     4. emitter.send(event("connected").data("connected"))
  │     5. return emitter
  │
  ├── publish(storeId, OrderEventData):
  │     1. emitters = storeEmitters.get(storeId)
  │     2. if null/empty → return
  │     3. deadEmitters = []
  │     4. for each emitter:
  │     │     try: emitter.send(event(type).data(eventData))
  │     │     catch: deadEmitters.add(emitter)
  │     5. emitters.removeAll(deadEmitters)
  │
  └── heartbeat() [@Scheduled(fixedRate = 15000)]:
        1. for each (storeId, emitters):
        2.   deadEmitters = []
        3.   for each emitter:
        4.     try: emitter.send(comment("heartbeat"))
        5.     catch: deadEmitters.add(emitter)
        6.   emitters.removeAll(deadEmitters)
```

**설계 결정**:
- CopyOnWriteArrayList: 읽기 빈번(이벤트 발행), 쓰기 드문(구독/해제) 패턴에 적합
- 하트비트 15초: 프록시/로드밸런서의 유휴 연결 타임아웃(보통 60초) 이내
- Dead emitter 즉시 정리: 메모리 누수 방지
- 초기 "connected" 이벤트: 클라이언트 연결 확인용

---

## 3. 파일 업로드 패턴

### Pattern: S3 Image Upload with Validation
**적용 NFR**: ANFR-PERF-03, ANFR-SEC-03, ANFR-AVAIL-02
```
MenuManageController.uploadImage(storeId, menuId, MultipartFile)
  → MenuManageService.uploadImage()
    → 1. 메뉴 소속 검증 (storeId)
    → 2. 파일 검증:
    │     a. Content-Type 확인 (image/jpeg, image/png, image/webp)
    │     b. 파일 크기 확인 (≤ 5MB)
    │     c. 파일 확장자 확인 (이중 검증)
    → 3. S3 키 생성: {storeCode}/menus/{menuId}/{UUID}.{ext}
    → 4. S3Service.upload(key, inputStream, contentType)
    │     a. PutObjectRequest 생성
    │     b. S3Client.putObject(request, body)
    │     c. return S3 URL
    → 5. menu.updateImageUrl(url)
    → 6. return ImageResponse(url)
```

**S3 설정**:
```java
S3Config:
  - S3Client bean 생성
  - Region: 환경변수 (AWS_REGION)
  - Credentials: DefaultCredentialsProvider (IAM Role 또는 환경변수)
  - Bucket: 환경변수 (S3_BUCKET)
```

---

## 4. 이용 완료 트랜잭션 패턴

### Pattern: Atomic Table Completion
**적용 NFR**: ANFR-REL-01, ANFR-PERF-04
```
@Transactional
TableManageService.completeTable(storeId, tableId):
  1. 테이블 조회 + storeId 검증
  2. ACTIVE 세션 조회 → 없으면 NoActiveSessionException
  3. 세션의 모든 주문 조회
  4. 배치 처리:
     a. 각 주문 → OrderHistory 변환 (items JSON 직렬화)
     b. OrderHistoryRepository.saveAll(histories)  // 배치 INSERT
     c. OrderRepository.deleteAll(orders)           // CASCADE DELETE
  5. session.complete()
  6. SSEService.publish(storeId, TABLE_COMPLETED)
```

**트랜잭션 설계**:
- 단일 @Transactional로 원자성 보장
- 실패 시 전체 롤백 (주문 데이터 유실 방지)
- JSON 직렬화: Jackson ObjectMapper 사용 (OrderItem → JSON 문자열)

---

## 5. 주문 상태 관리 패턴

### Pattern: State Machine with SSE Notification
**적용 NFR**: ANFR-REL-03, ANFR-PERF-02
```
OrderManageService.updateOrderStatus(storeId, orderId, newStatus):
  1. 주문 조회 + storeId 검증
  2. OrderStatus.canTransitionTo(newStatus) 검증
     → 불가: InvalidStatusTransitionException
  3. order.updateStatus(newStatus)
  4. OrderRepository.save(order)
  5. SSEService.publish(storeId, ORDER_STATUS_CHANGED)
```

**상태 전이 다이어그램**:
```
PENDING ──→ PREPARING ──→ COMPLETED
   │            │              │
   └────────────┴──────────────┘
         DELETE (모든 상태에서 가능)
```

---

## 6. 페이지네이션 패턴

### Pattern: Cursor-less Pagination for History
**적용 NFR**: ANFR-PERF-05
```
OrderManageService.getOrderHistory(tableId, startDate, endDate, page, size):
  1. Pageable pageable = PageRequest.of(page, size)
  2. if 날짜 범위 있음:
     Page<OrderHistory> = repository.findByTableIdAndCompletedAtBetween(...)
  3. else:
     Page<OrderHistory> = repository.findByTableIdOrderByCompletedAtDesc(...)
  4. return PageResponse.from(page)
```

**설계 결정**:
- Spring Data의 Pageable/Page 활용 (offset 기반)
- 기본 페이지 크기: 20건
- 정렬: completedAt DESC (최신순)
- 인덱스: idx_order_history_table_completed (table_id, completed_at)

---

## 7. 입력 검증 패턴

### Pattern: Layered Validation
**적용 NFR**: ANFR-SEC-01, SECURITY-05
```
Layer 1 - DTO 레벨 (Jakarta Bean Validation):
  @NotBlank, @Size, @Min, @Max, @Pattern
  → MethodArgumentNotValidException → GlobalExceptionHandler

Layer 2 - Service 레벨 (비즈니스 규칙):
  - 매장 코드 형식: ^[a-z0-9-]{3,50}$
  - 비밀번호 정책: 영문+숫자 조합 8자 이상
  - 파일 형식/크기 검증
  - 상태 전이 규칙 검증
  → BusinessException → GlobalExceptionHandler

Layer 3 - Repository 레벨 (DB 제약):
  - UNIQUE 제약 (매장 코드, 테이블 번호)
  - CHECK 제약 (가격 >= 0, 수량 >= 1)
  - FK 제약 (참조 무결성)
  → DataIntegrityViolationException → GlobalExceptionHandler
```
