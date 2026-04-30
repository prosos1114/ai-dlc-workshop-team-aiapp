# Logical Components - Unit 3: Admin Backend

## Admin Backend 모듈 논리적 구성도

```
+------------------------------------------------------------------+
|                   module-admin-api                                 |
|  +------------------------------------------------------------+  |
|  |  Controllers                                                |  |
|  |  AdminAuth | Store | TableManage | MenuManage | OrderManage |  |
|  |  SSE                                                        |  |
|  +------------------------------------------------------------+  |
|  |  Services                                                   |  |
|  |  AdminAuth | Store | TableManage | MenuManage | OrderManage |  |
|  |  SSE | S3                                                   |  |
|  +------------------------------------------------------------+  |
|  |  DTOs (Request / Response)                                  |  |
|  |  auth/ | store/ | table/ | menu/ | order/ | sse/           |  |
|  +------------------------------------------------------------+  |
+------------------------------------------------------------------+
         |                    |
         v                    v
+------------------+  +------------------+
|  module-domain   |  |  module-core     |
|  (Entities,      |  |  (Security,      |
|   Repositories)  |  |   Exceptions,    |
|                  |  |   DTOs, Config)  |
+------------------+  +------------------+
         |                    |
         v                    v
+------------------+  +------------------+
|  PostgreSQL      |  |  AWS S3          |
+------------------+  +------------------+
```

---

## 논리적 컴포넌트 상세

### 1. Controller Layer

#### AdminAuthController
- **경로**: /api/admin/auth
- **인증**: 불필요 (공개 API)
- **메서드**:
  - POST /login → login(LoginRequest) → ApiResponse<TokenResponse>
  - POST /register → register(RegisterRequest) → ApiResponse<AdminResponse>

#### StoreController
- **경로**: /api/stores
- **인증**: POST 불필요 (매장 등록), GET 불필요 (매장 조회)
- **메서드**:
  - POST / → createStore(StoreCreateRequest) → ApiResponse<StoreResponse>
  - GET /{storeCode} → getStore(storeCode) → ApiResponse<StoreResponse>

#### TableManageController
- **경로**: /api/stores/{storeId}/tables
- **인증**: 필수 (ADMIN)
- **메서드**:
  - POST / → createTable(TableCreateRequest) → ApiResponse<TableResponse>
  - GET / → getTables() → ApiResponse<List<TableResponse>>
  - PUT /{tableId} → updateTable(TableUpdateRequest) → ApiResponse<TableResponse>
  - POST /{tableId}/complete → completeTable() → ApiResponse<Void>

#### MenuManageController
- **경로**: /api/stores/{storeId}/menus, /api/stores/{storeId}/categories
- **인증**: 필수 (ADMIN)
- **메서드**:
  - GET /menus → getMenus(categoryId?) → ApiResponse<List<MenuResponse>>
  - POST /menus → createMenu(MenuCreateRequest) → ApiResponse<MenuResponse>
  - PUT /menus/{menuId} → updateMenu(MenuUpdateRequest) → ApiResponse<MenuResponse>
  - DELETE /menus/{menuId} → deleteMenu() → ApiResponse<Void>
  - PUT /menus/order → updateMenuOrder(MenuOrderUpdateRequest) → ApiResponse<Void>
  - POST /menus/{menuId}/image → uploadImage(MultipartFile) → ApiResponse<String>
  - GET /categories → getCategories() → ApiResponse<List<CategoryResponse>>
  - POST /categories → createCategory(CategoryCreateRequest) → ApiResponse<CategoryResponse>

#### OrderManageController
- **경로**: /api/stores/{storeId}/orders, /api/stores/{storeId}/tables/{tableId}/history
- **인증**: 필수 (ADMIN)
- **메서드**:
  - GET /orders → getAllOrders(status?) → ApiResponse<List<OrderResponse>>
  - PATCH /orders/{orderId}/status → updateStatus(OrderStatusUpdateRequest) → ApiResponse<OrderResponse>
  - DELETE /orders/{orderId} → deleteOrder() → ApiResponse<Void>
  - GET /tables/{tableId}/history → getHistory(startDate?, endDate?, page, size) → ApiResponse<PageResponse<OrderHistoryResponse>>

#### SSEController
- **경로**: /api/stores/{storeId}/orders/stream
- **인증**: 필수 (ADMIN)
- **메서드**:
  - GET /stream → subscribe() → SseEmitter

---

### 2. Service Layer

#### AdminAuthService
- **의존성**: StoreRepository, AdminRepository, PasswordEncoder, JwtTokenProvider
- **트랜잭션**: login (로그인 시도 횟수 업데이트), register (관리자 생성)
- **주요 로직**: 로그인 검증, 계정 잠금, 회원가입

#### StoreService
- **의존성**: StoreRepository
- **트랜잭션**: createStore
- **주요 로직**: 매장 등록, 코드 유일성 검증

#### TableManageService
- **의존성**: TableRepository, TableSessionRepository, OrderRepository, OrderHistoryRepository, PasswordEncoder, SSEService, ObjectMapper
- **트랜잭션**: createTable, updateTable, completeTable (원자적)
- **주요 로직**: 테이블 CRUD, 이용 완료 (주문 이력 이동)

#### MenuManageService
- **의존성**: MenuRepository, CategoryRepository, S3Service, StoreRepository
- **트랜잭션**: createMenu, updateMenu, deleteMenu, updateMenuOrder
- **주요 로직**: 메뉴 CRUD, 카테고리 관리, 이미지 업로드

#### OrderManageService
- **의존성**: OrderRepository, OrderHistoryRepository, TableRepository, SSEService
- **트랜잭션**: updateOrderStatus, deleteOrder
- **주요 로직**: 주문 조회, 상태 변경, 삭제, 과거 내역 조회

#### SSEService
- **의존성**: 없음 (인메모리)
- **트랜잭션**: 불필요
- **주요 로직**: SSE 구독/발행/하트비트/정리
- **스케줄링**: @Scheduled(fixedRate = 15000) 하트비트

#### S3Service
- **의존성**: S3Client (AWS SDK v2)
- **트랜잭션**: 불필요
- **주요 로직**: S3 파일 업로드, URL 생성

---

### 3. Configuration Components

#### S3Config
- **역할**: AWS S3Client 빈 생성
- **설정**: region, credentials (환경변수)
- **빈**: S3Client

---

### 4. 서비스 간 상호작용

```
AdminAuthService ──→ StoreRepository, AdminRepository, JwtTokenProvider
StoreService ──→ StoreRepository
TableManageService ──→ TableRepository, TableSessionRepository,
                       OrderRepository, OrderHistoryRepository, SSEService
MenuManageService ──→ MenuRepository, CategoryRepository, S3Service, StoreRepository
OrderManageService ──→ OrderRepository, OrderHistoryRepository,
                       TableRepository, SSEService
SSEService ──→ (독립, 인메모리)
S3Service ──→ S3Client
```

**SSE 이벤트 발행 포인트**:
| 서비스 | 이벤트 | 트리거 |
|---|---|---|
| OrderManageService | ORDER_STATUS_CHANGED | 주문 상태 변경 시 |
| OrderManageService | ORDER_DELETED | 주문 삭제 시 |
| TableManageService | TABLE_COMPLETED | 이용 완료 시 |

**참고**: ORDER_CREATED 이벤트는 Unit 2 (Customer Backend)의 주문 생성 시 발행됨.
