# NFR Design Patterns - Unit 1: Common/Shared

## 1. 보안 패턴

### Pattern: JWT Authentication Filter Chain
**적용 NFR**: NFR-SEC-01, NFR-SEC-02, SECURITY-08
```
HTTP Request
  → CorsFilter
  → JwtAuthenticationFilter
    → 토큰 추출 (Authorization: Bearer ...)
    → 토큰 검증 (서명, 만료)
    → SecurityContext 설정 (userId/tableId, storeId, role)
  → StoreAccessFilter
    → 요청 경로의 storeId == 토큰의 storeId 검증
  → Controller
```

**구현 방식**:
- `OncePerRequestFilter` 상속한 `JwtAuthenticationFilter`
- `HandlerInterceptor` 기반 `StoreAccessInterceptor` (매장 소속 검증)
- 공개 경로 화이트리스트: `/api/admin/auth/**`, `/api/table/auth/**`, `POST /api/stores`

### Pattern: Security Headers Middleware
**적용 NFR**: NFR-SEC-03, SECURITY-04
```java
// WebSecurityConfig에서 설정
headers.contentSecurityPolicy("default-src 'self'")
headers.httpStrictTransportSecurity(max-age=31536000; includeSubDomains)
headers.contentTypeOptions()  // X-Content-Type-Options: nosniff
headers.frameOptions().deny()  // X-Frame-Options: DENY
headers.referrerPolicy(STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
```

### Pattern: Rate Limiting
**적용 NFR**: NFR-SEC-05, SECURITY-11
- 인메모리 Rate Limiter (Bucket4j 또는 커스텀)
- 공개 API: IP 기반 분당 60회
- 로그인 API: 계정 기반 5회 실패 후 15분 잠금
- 구현: `HandlerInterceptor` 기반

### Pattern: Input Validation Layer
**적용 NFR**: NFR-SEC-04, SECURITY-05
- Jakarta Bean Validation (`@Valid`, `@NotNull`, `@Size`, `@Min`, `@Max`)
- 커스텀 Validator (매장 코드 형식, 비밀번호 정책)
- `@ControllerAdvice`에서 `MethodArgumentNotValidException` 처리
- 요청 본문 크기 제한: `server.tomcat.max-http-form-post-size=10MB`

---

## 2. 성능 패턴

### Pattern: Connection Pooling
**적용 NFR**: NFR-PERF-01, NFR-PERF-03
- HikariCP (Spring Boot 기본)
- 최소 연결: 10, 최대 연결: 50
- 연결 타임아웃: 30초
- 유휴 타임아웃: 10분

### Pattern: Database Indexing Strategy
**적용 NFR**: NFR-PERF-03, NFR-SCALE-03
```sql
-- 핵심 인덱스
CREATE INDEX idx_order_store_session ON orders(store_id, session_id);
CREATE INDEX idx_order_store_status ON orders(store_id, status);
CREATE INDEX idx_order_item_order ON order_items(order_id);
CREATE INDEX idx_menu_store_category ON menus(store_id, category_id, display_order);
CREATE INDEX idx_table_session_table_status ON table_sessions(table_id, status);
CREATE INDEX idx_admin_store_username ON admins(store_id, username);
CREATE INDEX idx_order_history_table_completed ON order_history(table_id, completed_at);
```

### Pattern: Lazy Loading with Fetch Strategy
**적용 NFR**: NFR-PERF-01
- 기본: LAZY 로딩
- 필요 시 `@EntityGraph` 또는 JPQL `JOIN FETCH`로 N+1 방지
- 주문 목록 조회 시 OrderItems는 별도 쿼리

---

## 3. 실시간 통신 패턴

### Pattern: SSE Event Broadcasting
**적용 NFR**: NFR-PERF-02, NFR-SCALE-02
```
SSEService
  ├── Map<Long, List<SseEmitter>> storeEmitters  // 매장별 emitter 관리
  ├── subscribe(storeId) → SseEmitter
  ├── publish(storeId, event) → broadcast to all emitters
  └── cleanup() → 만료/에러 emitter 제거
```

**설계 결정**:
- 인메모리 ConcurrentHashMap으로 emitter 관리
- 타임아웃: 30분 (SseEmitter timeout)
- 하트비트: 15초 간격 (연결 유지)
- 에러/타임아웃 시 자동 제거
- 단일 서버 인스턴스 기준 (수평 확장 시 Redis Pub/Sub 추가 필요)

### Pattern: Event Types
```json
{
  "type": "ORDER_CREATED",
  "data": {
    "orderId": 123,
    "tableNumber": 5,
    "totalAmount": 25000,
    "items": [{"menuName": "아메리카노", "quantity": 2}],
    "createdAt": "2026-04-30T12:00:00"
  }
}
```

이벤트 타입:
- `ORDER_CREATED`: 신규 주문
- `ORDER_STATUS_CHANGED`: 상태 변경 (status 포함)
- `ORDER_DELETED`: 주문 삭제
- `TABLE_COMPLETED`: 이용 완료

---

## 4. 에러 처리 패턴

### Pattern: Global Exception Handler
**적용 NFR**: NFR-REL-01, SECURITY-09, SECURITY-15
```
@RestControllerAdvice GlobalExceptionHandler
  ├── handleBusinessException → 400 Bad Request
  ├── handleNotFoundException → 404 Not Found
  ├── handleAccessDeniedException → 403 Forbidden
  ├── handleAuthenticationException → 401 Unauthorized
  ├── handleValidationException → 400 Bad Request (필드별 에러)
  ├── handleRateLimitException → 429 Too Many Requests
  └── handleException → 500 Internal Server Error (스택 트레이스 미노출)
```

**응답 형식**:
```json
{
  "code": "ORDER_NOT_FOUND",
  "message": "주문을 찾을 수 없습니다",
  "timestamp": "2026-04-30T12:00:00Z"
}
```

### Pattern: Custom Business Exceptions
```
BusinessException (abstract)
  ├── InvalidCredentialsException
  ├── AccountLockedException
  ├── StoreNotFoundException
  ├── TableNotFoundException
  ├── MenuNotFoundException
  ├── OrderNotFoundException
  ├── InvalidStatusTransitionException
  ├── DuplicateResourceException
  ├── NoActiveSessionException
  └── AccessDeniedException
```

---

## 5. 트랜잭션 패턴

### Pattern: Service-Level Transaction Management
**적용 NFR**: NFR-REL-02
- `@Transactional` 어노테이션 Service 레이어에 적용
- 읽기 전용: `@Transactional(readOnly = true)`
- 이용 완료: `@Transactional` (주문 이동 + 세션 종료 원자적)
- 주문 생성: `@Transactional` (Order + OrderItems 원자적)
- 롤백: RuntimeException 발생 시 자동 롤백

---

## 6. 로깅 패턴

### Pattern: Structured Logging with Correlation ID
**적용 NFR**: NFR-MAINT-03, SECURITY-03
```
RequestLoggingFilter
  → MDC.put("correlationId", UUID)
  → MDC.put("storeId", storeId)
  → 요청 처리
  → MDC.clear()
```

**로그 형식**:
```
{timestamp} [{correlationId}] [{level}] [{storeId}] {logger} - {message}
```

**로깅 규칙**:
- 비밀번호, 토큰 값 로깅 금지
- 요청/응답 본문 로깅 시 민감 필드 마스킹
- ERROR: 예외 발생, 외부 서비스 실패
- WARN: 비즈니스 규칙 위반 (로그인 실패 등)
- INFO: 주요 비즈니스 이벤트 (주문 생성, 이용 완료)
- DEBUG: 상세 처리 흐름

---

## 7. 데이터 격리 패턴

### Pattern: Store-Scoped Data Access
**적용 NFR**: NFR-SEC-02, SECURITY-08
- 모든 Repository 쿼리에 storeId 조건 포함
- `StoreAccessInterceptor`에서 요청 경로의 storeId와 토큰의 storeId 비교
- 불일치 시 403 Forbidden 즉시 반환
- 테이블 토큰: 추가로 tableId 검증
