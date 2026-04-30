# Component Dependencies

## 의존성 매트릭스

### Controller → Service 의존성
| Controller | 의존 Service |
|---|---|
| AuthController | AuthService |
| StoreController | StoreService |
| TableController | TableService |
| MenuController | MenuService |
| OrderController | OrderService |
| SSEController | SSEService |

### Service → Service 의존성
| Service | 의존 Service |
|---|---|
| OrderService | TableService, SSEService |
| TableService | OrderService (이력 이동 시) |
| MenuService | S3Service |
| AuthService | (없음) |
| StoreService | (없음) |
| SSEService | (없음) |

### Service → Repository 의존성
| Service | 의존 Repository |
|---|---|
| AuthService | AdminRepository, StoreRepository |
| StoreService | StoreRepository |
| TableService | TableRepository, TableSessionRepository |
| MenuService | MenuRepository, CategoryRepository |
| OrderService | OrderRepository, OrderItemRepository, OrderHistoryRepository |

---

## 통신 패턴

### 동기 통신 (REST API)
- 고객 앱 → Backend: 메뉴 조회, 주문 생성, 주문 내역 조회
- 관리자 앱 → Backend: 모든 관리 기능

### 비동기 통신 (SSE)
- Backend → 관리자 앱: 신규 주문 알림, 주문 상태 변경 알림

### 데이터 흐름
```
+------------------+     REST API      +------------------+
|  Customer App    | -----------------> |                  |
|  (React)         | <----------------- |  Spring Boot     |
+------------------+     Response       |  Backend         |
                                        |                  |
+------------------+     REST API      |                  |     +----------+
|  Admin App       | -----------------> |                  | --> | PostgreSQL|
|  (React)         | <----------------- |                  | <-- |          |
|                  | <--- SSE --------- |                  |     +----------+
+------------------+                    +------------------+
                                              |
                                              v
                                        +----------+
                                        |  AWS S3  |
                                        +----------+
```

---

## 순환 의존성 해결

### TableService ↔ OrderService 순환 참조 방지
- **문제**: TableService.completeTableSession()이 OrderService.moveOrdersToHistory()를 호출
- **해결**: 이벤트 기반 분리 또는 별도 Facade 서비스 도입
- **선택**: TableSessionFacade 도입
  - TableSessionFacade.completeSession() → TableService + OrderService 조합

---

## 보안 계층

### 엔드포인트 접근 제어
| 경로 패턴 | 접근 권한 |
|---|---|
| /api/admin/auth/** | 인증 불필요 (로그인/회원가입) |
| /api/table/auth/** | 인증 불필요 (테이블 인증) |
| /api/stores (POST) | 인증 불필요 (매장 등록) |
| /api/stores/{storeId}/menus (GET) | 테이블 토큰 또는 관리자 토큰 |
| /api/stores/{storeId}/tables/{tableId}/orders/** | 테이블 토큰 (해당 테이블만) |
| /api/stores/{storeId}/orders/** | 관리자 토큰 (해당 매장만) |
| /api/stores/{storeId}/tables/** (관리) | 관리자 토큰 |
| /api/stores/{storeId}/menus/** (CUD) | 관리자 토큰 |
| /api/stores/{storeId}/categories/** | 관리자 토큰 |
