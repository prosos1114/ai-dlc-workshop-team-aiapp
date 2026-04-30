# Code Generation Summary - Unit 3: Admin Backend

## 생성된 파일 목록

### Application Code (table-order-backend/module-admin-api/src/main/java/com/tableorder/admin/)

| 패키지 | 파일 | 용도 |
|---|---|---|
| s3/ | S3Config.java | AWS S3Client 빈 설정 |
| s3/ | S3Service.java | S3 파일 업로드 서비스 |
| sse/dto/ | OrderEventData.java | SSE 이벤트 DTO |
| sse/ | SSEService.java | SSE 구독/발행/하트비트 |
| sse/ | SSEController.java | SSE 엔드포인트 |
| auth/dto/ | LoginRequest.java | 로그인 요청 DTO |
| auth/dto/ | RegisterRequest.java | 회원가입 요청 DTO |
| auth/dto/ | TokenResponse.java | 토큰 응답 DTO |
| auth/dto/ | AdminResponse.java | 관리자 응답 DTO |
| auth/ | AdminAuthService.java | 인증 비즈니스 로직 |
| auth/ | AdminAuthController.java | 인증 API |
| store/dto/ | StoreCreateRequest.java | 매장 등록 요청 DTO |
| store/dto/ | StoreResponse.java | 매장 응답 DTO |
| store/ | StoreService.java | 매장 비즈니스 로직 |
| store/ | StoreController.java | 매장 API |
| table/dto/ | TableCreateRequest.java | 테이블 생성 요청 DTO |
| table/dto/ | TableUpdateRequest.java | 테이블 수정 요청 DTO |
| table/dto/ | TableResponse.java | 테이블 응답 DTO |
| table/ | TableManageService.java | 테이블 비즈니스 로직 |
| table/ | TableManageController.java | 테이블 API |
| menu/dto/ | MenuCreateRequest.java | 메뉴 생성 요청 DTO |
| menu/dto/ | MenuUpdateRequest.java | 메뉴 수정 요청 DTO |
| menu/dto/ | MenuOrderUpdateRequest.java | 메뉴 순서 변경 요청 DTO |
| menu/dto/ | CategoryCreateRequest.java | 카테고리 생성 요청 DTO |
| menu/dto/ | MenuResponse.java | 메뉴 응답 DTO |
| menu/dto/ | CategoryResponse.java | 카테고리 응답 DTO |
| menu/ | MenuManageService.java | 메뉴 비즈니스 로직 |
| menu/ | MenuManageController.java | 메뉴 API |
| order/dto/ | OrderStatusUpdateRequest.java | 주문 상태 변경 요청 DTO |
| order/dto/ | OrderItemResponse.java | 주문 항목 응답 DTO |
| order/dto/ | OrderResponse.java | 주문 응답 DTO |
| order/dto/ | OrderHistoryResponse.java | 주문 이력 응답 DTO |
| order/ | OrderManageService.java | 주문 비즈니스 로직 |
| order/ | OrderManageController.java | 주문 API |

### Test Code (table-order-backend/module-admin-api/src/test/java/com/tableorder/admin/)

| 패키지 | 파일 | 테스트 대상 |
|---|---|---|
| auth/ | AdminAuthServiceTest.java | 인증 서비스 (7 tests) |
| auth/ | AdminAuthControllerTest.java | 인증 API (3 tests) |
| store/ | StoreServiceTest.java | 매장 서비스 (4 tests) |
| store/ | StoreControllerTest.java | 매장 API (3 tests) |
| table/ | TableManageServiceTest.java | 테이블 서비스 (4 tests) |
| table/ | TableManageControllerTest.java | 테이블 API (2 tests) |
| menu/ | MenuManageServiceTest.java | 메뉴 서비스 (4 tests) |
| menu/ | MenuManageControllerTest.java | 메뉴 API (3 tests) |
| order/ | OrderManageServiceTest.java | 주문 서비스 (4 tests) |
| order/ | OrderManageControllerTest.java | 주문 API (3 tests) |
| sse/ | SSEServiceTest.java | SSE 서비스 (3 tests) |

### Modified Files

| 파일 | 변경 내용 |
|---|---|
| module-app/.../TableOrderApplication.java | @EnableScheduling 추가 (SSE 하트비트용) |

## 스토리 커버리지

| 스토리 | 설명 | 구현 상태 |
|---|---|---|
| US-A01 | 관리자 로그인 | ✅ |
| US-A02 | 관리자 회원가입 | ✅ |
| US-A03 | 관리자 로그아웃 | ✅ (클라이언트 측 토큰 삭제) |
| US-A04 | 매장 등록 | ✅ |
| US-A05 | 주문 대시보드 조회 | ✅ |
| US-A06 | 실시간 신규 주문 수신 | ✅ (SSE) |
| US-A07 | 주문 상태 변경 | ✅ |
| US-A08 | 테이블 초기 설정 | ✅ |
| US-A09 | 주문 삭제 | ✅ |
| US-A10 | 테이블 이용 완료 | ✅ |
| US-A11 | 과거 주문 내역 조회 | ✅ |
| US-A12 | 메뉴 등록 | ✅ |
| US-A13 | 메뉴 조회 | ✅ |
| US-A14 | 메뉴 수정 | ✅ |
| US-A15 | 메뉴 삭제 | ✅ |
| US-A16 | 메뉴 노출 순서 조정 | ✅ |
