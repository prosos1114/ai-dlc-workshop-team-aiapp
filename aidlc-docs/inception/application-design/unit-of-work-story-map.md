# Unit of Work - Story Map

## Story → Unit 매핑

### Unit 1: Common/Shared
공통 기반으로 직접 스토리가 할당되지 않지만, 모든 스토리의 기반 인프라를 제공합니다.

| 제공 기능 | 사용 유닛 |
|---|---|
| JWT 인증/인가 | U2, U3 |
| 도메인 엔티티 | U2, U3 |
| 보안 설정 | U2, U3 |
| API 클라이언트 | U4, U5 |
| 공통 타입 | U4, U5 |
| 공통 UI 컴포넌트 | U4, U5 |

---

### Unit 2: Customer Backend

| Story ID | Story 제목 | 관련 API |
|---|---|---|
| US-C01 | 태블릿 초기 설정 | POST /api/table/auth/login |
| US-C02 | 자동 로그인 | POST /api/table/auth/login |
| US-C03 | 카테고리별 메뉴 목록 조회 | GET /api/stores/{storeId}/menus, GET /categories |
| US-C04 | 메뉴 상세 정보 확인 | GET /api/stores/{storeId}/menus/{menuId} |
| US-C10 | 주문 확정 및 성공 처리 | POST /api/stores/{storeId}/tables/{tableId}/orders |
| US-C11 | 주문 실패 처리 | POST /api/stores/{storeId}/tables/{tableId}/orders |
| US-C12 | 현재 세션 주문 내역 조회 | GET /api/stores/{storeId}/tables/{tableId}/orders |

---

### Unit 3: Admin Backend

| Story ID | Story 제목 | 관련 API |
|---|---|---|
| US-A01 | 관리자 로그인 | POST /api/admin/auth/login |
| US-A02 | 관리자 회원가입 | POST /api/admin/auth/register |
| US-A03 | 관리자 로그아웃 | (클라이언트 토큰 삭제) |
| US-A04 | 매장 등록 | POST /api/stores |
| US-A05 | 주문 대시보드 조회 | GET /api/stores/{storeId}/orders |
| US-A06 | 실시간 신규 주문 수신 | GET /api/stores/{storeId}/orders/stream (SSE) |
| US-A07 | 주문 상태 변경 | PATCH /api/stores/{storeId}/orders/{orderId}/status |
| US-A08 | 테이블 초기 설정 | POST /api/stores/{storeId}/tables |
| US-A09 | 주문 삭제 (직권 수정) | DELETE /api/stores/{storeId}/orders/{orderId} |
| US-A10 | 테이블 이용 완료 처리 | POST /api/stores/{storeId}/tables/{tableId}/complete |
| US-A11 | 과거 주문 내역 조회 | GET /api/stores/{storeId}/tables/{tableId}/history |
| US-A12 | 메뉴 등록 | POST /api/stores/{storeId}/menus |
| US-A13 | 메뉴 조회 (관리자) | GET /api/stores/{storeId}/menus |
| US-A14 | 메뉴 수정 | PUT /api/stores/{storeId}/menus/{menuId} |
| US-A15 | 메뉴 삭제 | DELETE /api/stores/{storeId}/menus/{menuId} |
| US-A16 | 메뉴 노출 순서 조정 | PUT /api/stores/{storeId}/menus/order |

---

### Unit 4: Customer Frontend

| Story ID | Story 제목 | 주요 컴포넌트 |
|---|---|---|
| US-C01 | 태블릿 초기 설정 | SetupPage, TableSetup |
| US-C02 | 자동 로그인 | useTableAuth hook |
| US-C03 | 카테고리별 메뉴 목록 조회 | MenuPage, CategoryTab, MenuCard |
| US-C04 | 메뉴 상세 정보 확인 | MenuDetail |
| US-C05 | 메뉴를 장바구니에 추가 | useCart, CartBadge |
| US-C06 | 장바구니 수량 조절 | CartItem, useCart |
| US-C07 | 장바구니 항목 삭제 및 비우기 | CartPage, useCart |
| US-C08 | 장바구니 총 금액 확인 | CartSummary |
| US-C09 | 주문 내역 최종 확인 | OrderConfirmPage |
| US-C10 | 주문 확정 및 성공 처리 | OrderSuccess |
| US-C11 | 주문 실패 처리 | OrderConfirmPage (에러 처리) |
| US-C12 | 현재 세션 주문 내역 조회 | OrderHistoryPage |

---

### Unit 5: Admin Frontend

| Story ID | Story 제목 | 주요 컴포넌트 |
|---|---|---|
| US-A01 | 관리자 로그인 | LoginPage, LoginForm |
| US-A02 | 관리자 회원가입 | RegisterPage, RegisterForm |
| US-A03 | 관리자 로그아웃 | useAdminAuth hook |
| US-A04 | 매장 등록 | StoreRegisterPage |
| US-A05 | 주문 대시보드 조회 | DashboardPage, TableCard |
| US-A06 | 실시간 신규 주문 수신 | useSSE hook, DashboardPage |
| US-A07 | 주문 상태 변경 | OrderDetail, StatusBadge |
| US-A08 | 테이블 초기 설정 | TableManagePage, TableSetup |
| US-A09 | 주문 삭제 (직권 수정) | OrderDetail |
| US-A10 | 테이블 이용 완료 처리 | TableCard |
| US-A11 | 과거 주문 내역 조회 | HistoryModal |
| US-A12 | 메뉴 등록 | MenuManagePage, MenuForm |
| US-A13 | 메뉴 조회 (관리자) | MenuManagePage, MenuList |
| US-A14 | 메뉴 수정 | MenuForm |
| US-A15 | 메뉴 삭제 | MenuList |
| US-A16 | 메뉴 노출 순서 조정 | OrderSort |

---

## 검증

### 커버리지 확인
- **총 User Stories**: 28개
- **Unit 2 (Customer BE)**: 7개 스토리
- **Unit 3 (Admin BE)**: 16개 스토리
- **Unit 4 (Customer FE)**: 12개 스토리
- **Unit 5 (Admin FE)**: 16개 스토리
- **미할당 스토리**: 0개 ✅

### 참고
- 장바구니 스토리(US-C05~C09)는 프론트엔드 전용 (Backend API 불필요)
- US-A03(로그아웃)은 클라이언트 측 토큰 삭제로 처리 (별도 API 불필요)
- 일부 스토리는 Backend + Frontend 양쪽에 매핑됨 (정상)
