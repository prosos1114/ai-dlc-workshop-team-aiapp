# Code Generation Plan - Unit 1: Common/Shared

## Unit Context
- **유닛**: Common/Shared (공통 기반)
- **기술 스택**: Java 17 + Spring Boot 3.2 + Gradle 8 + PostgreSQL + React 18 + TypeScript 5
- **범위**: Backend 공통 모듈 (module-core, module-domain, module-app) + Frontend 공유 패키지 (shared)
- **프로젝트 유형**: Greenfield multi-unit

## Code Location
- **Backend**: `table-order-backend/`
- **Frontend**: `table-order-frontend/`
- **Documentation**: `aidlc-docs/construction/common-shared/code/`

---

## Step 1: Backend 프로젝트 구조 초기화
- [x] Gradle 멀티 모듈 프로젝트 루트 설정 (settings.gradle, build.gradle)
- [x] module-core, module-domain, module-admin-api, module-customer-api, module-app 모듈 생성
- [x] 모듈 간 의존성 설정
- [x] 공통 의존성 정의 (Spring Boot, JPA, Security, JWT, Validation 등)

## Step 2: module-core - 보안 설정
- [x] SecurityConfig (Spring Security 설정, 필터 체인, CORS, 보안 헤더)
- [x] JwtTokenProvider (토큰 생성, 검증, 파싱)
- [x] JwtAuthenticationFilter (요청별 토큰 인증)
- [x] StoreAccessInterceptor (매장 소속 검증)
- [ ] RateLimitInterceptor (API 호출 빈도 제한) - Unit 3에서 구현
- [x] WebConfig (인터셉터 등록)
- [x] CorsConfig (CORS 정책)

## Step 3: module-core - 예외 처리 및 공통 DTO
- [x] BusinessException 계층 (커스텀 예외 클래스들)
- [x] GlobalExceptionHandler (@RestControllerAdvice)
- [x] ApiResponse, ApiErrorResponse, PageResponse DTO
- [x] RequestLoggingFilter (Correlation ID, MDC)

## Step 4: module-core - 단위 테스트
- [x] JwtTokenProvider 테스트
- [x] JwtAuthenticationFilter 테스트
- [x] GlobalExceptionHandler 테스트
- [x] StoreAccessInterceptor 테스트
- [x] RequestLoggingFilter 테스트
- [x] AuthenticatedUser 테스트
- [x] ApiResponse 테스트

## Step 5: module-domain - 엔티티 및 Repository
- [x] BaseEntity (공통 필드, JPA Auditing)
- [x] Store 엔티티 + StoreRepository
- [x] Admin 엔티티 + AdminRepository
- [x] Table 엔티티 + TableRepository
- [x] TableSession 엔티티 + TableSessionRepository
- [x] Category 엔티티 + CategoryRepository
- [x] Menu 엔티티 + MenuRepository
- [x] Order 엔티티 + OrderRepository
- [x] OrderItem 엔티티 + OrderItemRepository
- [x] OrderHistory 엔티티 + OrderHistoryRepository
- [x] Enum 정의 (SessionStatus, OrderStatus)

## Step 6: module-domain - 단위 테스트
- [x] 엔티티 생성/검증 테스트 (Admin, Order, OrderItem, TableSession, TableEntity, Menu, Store)
- [x] OrderStatus 상태 전환 테스트

## Step 7: module-app - 애플리케이션 설정
- [x] TableOrderApplication (메인 클래스)
- [x] application.yml (프로파일별 설정)
- [x] Flyway 마이그레이션 스크립트 (V1__init_schema.sql)
- [x] JPA Auditing 설정

## Step 8: Frontend 프로젝트 구조 초기화
- [x] pnpm workspace 루트 설정 (package.json, pnpm-workspace.yaml)
- [x] shared 패키지 초기화 (package.json, tsconfig.json)
- [x] customer-app 패키지 초기화 (package.json, tsconfig.json, vite.config.ts, tailwind.config.js)
- [x] admin-app 패키지 초기화 (package.json, tsconfig.json, vite.config.ts, tailwind.config.js)
- [ ] ESLint, Prettier 공통 설정 - Unit 4/5에서 추가

## Step 9: shared 패키지 - API 클라이언트 및 타입
- [x] axios 인스턴스 설정 (baseURL, 인터셉터, 에러 처리)
- [x] 공유 TypeScript 타입/인터페이스 (Store, Menu, Order, Table 등)
- [ ] 공통 커스텀 훅 (useLocalStorage) - Unit 4에서 구현
- [x] 유틸리티 함수 (가격 포맷터, 날짜 포맷터)
- [x] 상수 정의 (API 경로, 주문 상태)

## Step 10: shared 패키지 - 단위 테스트
- [x] API endpoints 테스트
- [x] 유틸리티 함수 테스트 (formatters)
- [x] 상수 검증 테스트

## Step 11: Documentation 및 배포 설정
- [x] Backend Dockerfile
- [ ] Frontend Dockerfile (customer-app, admin-app) - Unit 4/5 완료 후
- [x] docker-compose.yml (개발 환경)
- [ ] 코드 생성 요약 문서

---

## Story Traceability
Unit 1은 공통 기반으로 직접 스토리가 할당되지 않지만, 모든 유닛(U2~U5)의 기반 인프라를 제공합니다.
