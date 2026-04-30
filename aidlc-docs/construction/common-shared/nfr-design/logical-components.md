# Logical Components - Unit 1: Common/Shared

## 시스템 논리적 구성도

```
+------------------------------------------------------------------+
|                        Load Balancer (ALB)                        |
|                     (HTTPS Termination, TLS 1.2+)                |
+------------------------------------------------------------------+
                              |
                              v
+------------------------------------------------------------------+
|                    Spring Boot Application                        |
|  +------------------------------------------------------------+  |
|  |  Filter Chain                                               |  |
|  |  CorsFilter → JwtAuthFilter → RateLimitFilter              |  |
|  +------------------------------------------------------------+  |
|  |  Interceptor                                                |  |
|  |  StoreAccessInterceptor → RequestLoggingInterceptor        |  |
|  +------------------------------------------------------------+  |
|  |  Controllers                                                |  |
|  |  Auth | Store | Table | Menu | Order | SSE                 |  |
|  +------------------------------------------------------------+  |
|  |  Services                                                   |  |
|  |  Auth | Store | Table | Menu | Order | SSE | S3            |  |
|  +------------------------------------------------------------+  |
|  |  Repositories (Spring Data JPA)                             |  |
|  +------------------------------------------------------------+  |
+------------------------------------------------------------------+
         |                    |                    |
         v                    v                    v
+----------------+  +------------------+  +----------------+
|  PostgreSQL    |  |  AWS S3          |  |  SSE Emitters  |
|  (RDS)         |  |  (이미지 저장)    |  |  (인메모리)     |
+----------------+  +------------------+  +----------------+
```

---

## 논리적 컴포넌트 상세

### 1. Security Components

#### JwtTokenProvider
- **역할**: JWT 토큰 생성, 파싱, 검증
- **설정**: secret key (환경변수), 알고리즘 HS256
- **메서드**: createToken(), validateToken(), getClaims()

#### JwtAuthenticationFilter
- **역할**: 모든 요청에서 JWT 토큰 추출 및 인증
- **위치**: Spring Security Filter Chain
- **동작**: Bearer 토큰 추출 → 검증 → SecurityContext 설정

#### StoreAccessInterceptor
- **역할**: 매장 소속 검증 (storeId 일치 확인)
- **위치**: HandlerInterceptor
- **동작**: 경로의 storeId와 토큰의 storeId 비교

#### RateLimitInterceptor
- **역할**: API 호출 빈도 제한
- **위치**: HandlerInterceptor
- **전략**: 인메모리 카운터 (ConcurrentHashMap + 시간 윈도우)

---

### 2. Error Handling Components

#### GlobalExceptionHandler
- **역할**: 전역 예외 처리 및 일관된 에러 응답
- **위치**: @RestControllerAdvice
- **응답**: ApiErrorResponse(code, message, timestamp)

#### BusinessException Hierarchy
- **역할**: 도메인별 비즈니스 예외 정의
- **구조**: 추상 BusinessException → 구체 예외 클래스들
- **HTTP 매핑**: 예외 타입별 HTTP 상태 코드 매핑

---

### 3. Logging Components

#### RequestLoggingFilter
- **역할**: 요청별 Correlation ID 생성 및 MDC 설정
- **위치**: Servlet Filter (최상위)
- **동작**: UUID 생성 → MDC 설정 → 요청 처리 → MDC 정리

#### LoggingConfig
- **역할**: Logback 설정
- **형식**: JSON 구조화 로깅 (프로덕션) / 텍스트 (개발)
- **레벨**: ERROR, WARN, INFO, DEBUG

---

### 4. Data Access Components

#### BaseEntity
- **역할**: 공통 엔티티 필드 (id, createdAt, updatedAt)
- **구현**: @MappedSuperclass, @EntityListeners(AuditingEntityListener)

#### JPA Auditing
- **역할**: createdAt, updatedAt 자동 설정
- **설정**: @EnableJpaAuditing

---

### 5. Configuration Components

#### SecurityConfig
- **역할**: Spring Security 전체 설정
- **내용**: 필터 체인, CORS, CSRF 비활성화, 경로별 접근 제어, 보안 헤더

#### CorsConfig
- **역할**: CORS 정책 설정
- **허용 오리진**: 프론트엔드 도메인 (환경변수)
- **허용 메서드**: GET, POST, PUT, PATCH, DELETE
- **허용 헤더**: Authorization, Content-Type

#### WebConfig
- **역할**: 인터셉터 등록, 정적 리소스 설정
- **등록**: StoreAccessInterceptor, RateLimitInterceptor

#### S3Config
- **역할**: AWS S3 클라이언트 설정
- **설정**: region, credentials (환경변수)

---

### 6. DTO Components

#### ApiResponse<T>
```java
{
  "success": true,
  "data": T,
  "message": null
}
```

#### ApiErrorResponse
```java
{
  "code": "ERROR_CODE",
  "message": "사용자 친화적 메시지",
  "timestamp": "2026-04-30T12:00:00Z"
}
```

#### PageResponse<T>
```java
{
  "content": List<T>,
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5
}
```

---

## 환경 설정 (application.yml 구조)

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      minimum-idle: 10
      maximum-pool-size: 50
      connection-timeout: 30000
      idle-timeout: 600000
  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
  servlet:
    multipart:
      max-file-size: 5MB
      max-request-size: 10MB

server:
  tomcat:
    max-http-form-post-size: 10MB

jwt:
  secret: ${JWT_SECRET}
  admin-expiration: 57600000    # 16 hours
  table-expiration: 31536000000 # 365 days

aws:
  s3:
    bucket: ${S3_BUCKET}
    region: ${AWS_REGION}

cors:
  allowed-origins: ${CORS_ORIGINS}

rate-limit:
  public-api:
    requests-per-minute: 60
```
