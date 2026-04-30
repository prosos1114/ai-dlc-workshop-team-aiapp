# Code Generation Plan - Unit 3: Admin Backend

## Unit Context
- **Unit**: Unit 3 - Admin Backend (module-admin-api)
- **프로젝트 타입**: Greenfield, Multi-module Gradle
- **코드 위치**: `table-order-backend/module-admin-api/src/`
- **의존성**: module-core (보안, 예외, DTO), module-domain (엔티티, Repository)
- **할당 스토리**: US-A01~A16 (관리자 인증, 매장/테이블/메뉴/주문 관리, SSE)

## 기존 코드 상태
- module-admin-api/build.gradle: 이미 존재 (S3 SDK, SpringDoc 의존성 포함)
- module-admin-api/src/: 비어있음 (코드 생성 필요)
- module-core, module-domain: Unit 1에서 완성됨

---

## 실행 계획

### Step 1: 프로젝트 구조 설정
- [x] module-admin-api/build.gradle 검증 및 필요 시 보완
- [x] 패키지 디렉토리 구조 생성

### Step 2: S3 설정 및 서비스
- [x] S3Config.java - AWS S3Client 빈 설정
- [x] S3Service.java - 파일 업로드 서비스

### Step 3: SSE 서비스
- [x] OrderEventData.java - SSE 이벤트 DTO
- [x] SSEService.java - SSE 구독/발행/하트비트 서비스
- [x] SSEController.java - SSE 엔드포인트

### Step 4: 인증 (Auth) - US-A01, US-A02, US-A03
- [x] LoginRequest.java, RegisterRequest.java - 요청 DTO
- [x] TokenResponse.java, AdminResponse.java - 응답 DTO
- [x] AdminAuthService.java - 인증 비즈니스 로직
- [x] AdminAuthController.java - 인증 API 엔드포인트

### Step 5: 매장 관리 (Store) - US-A04
- [x] StoreCreateRequest.java - 요청 DTO
- [x] StoreResponse.java - 응답 DTO
- [x] StoreService.java - 매장 비즈니스 로직
- [x] StoreController.java - 매장 API 엔드포인트

### Step 6: 테이블 관리 (Table) - US-A08, US-A10
- [x] TableCreateRequest.java, TableUpdateRequest.java - 요청 DTO
- [x] TableResponse.java - 응답 DTO
- [x] TableManageService.java - 테이블 비즈니스 로직 (이용 완료 포함)
- [x] TableManageController.java - 테이블 API 엔드포인트

### Step 7: 메뉴 관리 (Menu) - US-A12, US-A13, US-A14, US-A15, US-A16
- [x] MenuCreateRequest.java, MenuUpdateRequest.java, MenuOrderUpdateRequest.java - 요청 DTO
- [x] CategoryCreateRequest.java - 요청 DTO
- [x] MenuResponse.java, CategoryResponse.java - 응답 DTO
- [x] MenuManageService.java - 메뉴 CRUD + 이미지 업로드 비즈니스 로직
- [x] MenuManageController.java - 메뉴 API 엔드포인트

### Step 8: 주문 관리 (Order) - US-A05, US-A06, US-A07, US-A09, US-A11
- [x] OrderStatusUpdateRequest.java - 요청 DTO
- [x] OrderResponse.java, OrderItemResponse.java, OrderHistoryResponse.java - 응답 DTO
- [x] OrderManageService.java - 주문 조회/상태변경/삭제/이력 비즈니스 로직
- [x] OrderManageController.java - 주문 API 엔드포인트

### Step 9: 단위 테스트 - Service Layer
- [x] AdminAuthServiceTest.java
- [x] StoreServiceTest.java
- [x] TableManageServiceTest.java
- [x] MenuManageServiceTest.java
- [x] OrderManageServiceTest.java
- [x] SSEServiceTest.java

### Step 10: 단위 테스트 - Controller Layer
- [x] AdminAuthControllerTest.java
- [x] StoreControllerTest.java
- [x] TableManageControllerTest.java
- [x] MenuManageControllerTest.java
- [x] OrderManageControllerTest.java

### Step 11: 문서 및 마무리
- [x] Code Generation Summary 문서 생성
- [x] aidlc-state.md 업데이트

---

## 스토리 매핑

| Step | 스토리 | 설명 |
|---|---|---|
| Step 4 | US-A01, US-A02, US-A03 | 관리자 로그인, 회원가입, 로그아웃 |
| Step 5 | US-A04 | 매장 등록 |
| Step 6 | US-A08, US-A10 | 테이블 초기 설정, 이용 완료 |
| Step 7 | US-A12~A16 | 메뉴 등록/조회/수정/삭제/순서 조정 |
| Step 8 | US-A05~A07, US-A09, US-A11 | 주문 대시보드, 실시간 수신, 상태 변경, 삭제, 과거 내역 |
