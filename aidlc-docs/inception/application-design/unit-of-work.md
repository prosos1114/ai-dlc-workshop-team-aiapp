# Unit of Work - 테이블오더 서비스

## 유닛 분해 전략
- **개발 방식**: Parallel (백엔드/프론트엔드 동시 개발, API 스펙 먼저 합의)
- **분리 기준**: 사용자 유형(고객/관리자) + 공통 영역
- **백엔드**: 멀티 모듈 Gradle 프로젝트
- **프론트엔드**: 모노레포 + 공유 라이브러리 (pnpm workspace)

---

## Unit 1: Common/Shared (공통 기반)

### 목적
백엔드와 프론트엔드 모두에서 사용하는 공통 기반 코드

### Backend 공통 (Gradle 모듈)
```
table-order-backend/
├── build.gradle
├── settings.gradle
├── module-core/                    # 공통 설정
│   └── src/main/java/com/tableorder/core/
│       ├── config/                 # SecurityConfig, CorsConfig, WebConfig
│       ├── exception/              # GlobalExceptionHandler, 커스텀 예외
│       ├── security/               # JwtTokenProvider, JwtAuthFilter
│       ├── dto/                    # 공통 응답 DTO (ApiResponse, ErrorResponse)
│       └── util/                   # 유틸리티
├── module-domain/                  # 도메인 엔티티 전체
│   └── src/main/java/com/tableorder/domain/
│       ├── store/                  # Store 엔티티, StoreRepository
│       ├── admin/                  # Admin 엔티티, AdminRepository
│       ├── table/                  # Table, TableSession 엔티티, Repository
│       ├── menu/                   # Menu, Category 엔티티, Repository
│       └── order/                  # Order, OrderItem, OrderHistory 엔티티, Repository
└── module-app/                     # Spring Boot 실행 모듈
    └── src/main/java/com/tableorder/
        └── TableOrderApplication.java
```

### Frontend 공통 (pnpm 패키지)
```
table-order-frontend/
├── package.json
├── pnpm-workspace.yaml
└── packages/
    └── shared/                     # @table-order/shared
        └── src/
            ├── api/                # axios 인스턴스, API 클라이언트 베이스
            ├── types/              # 공유 TypeScript 타입/인터페이스
            ├── hooks/              # 공통 커스텀 훅 (useAuth, useLocalStorage)
            ├── components/         # 공통 UI 컴포넌트 (Button, Modal, Toast)
            ├── utils/              # 유틸리티 (포맷터, 검증)
            └── constants/          # 상수 (API URL, 상태 코드)
```

### 책임
- 보안 설정 (JWT, CORS, Security Headers)
- 전역 예외 처리
- 도메인 엔티티 및 Repository
- 공통 DTO/타입
- 공통 UI 컴포넌트
- API 클라이언트 기반

---

## Unit 2: Customer Backend (고객용 API)

### 프로젝트 구조
```
table-order-backend/
└── module-customer-api/            # 고객용 API 모듈
    └── src/main/java/com/tableorder/customer/
        ├── auth/
        │   ├── TableAuthController.java
        │   └── TableAuthService.java
        ├── menu/
        │   ├── CustomerMenuController.java
        │   └── CustomerMenuService.java
        └── order/
            ├── CustomerOrderController.java
            └── CustomerOrderService.java
```

### 책임
- 테이블 태블릿 인증 (초기 설정, 자동 로그인)
- 메뉴 조회 (카테고리별, 상세)
- 주문 생성
- 현재 세션 주문 내역 조회

### API 엔드포인트
| Method | Path | Purpose |
|---|---|---|
| POST | /api/table/auth/login | 테이블 인증 |
| GET | /api/stores/{storeId}/menus | 메뉴 목록 조회 |
| GET | /api/stores/{storeId}/menus/{menuId} | 메뉴 상세 |
| GET | /api/stores/{storeId}/categories | 카테고리 목록 |
| POST | /api/stores/{storeId}/tables/{tableId}/orders | 주문 생성 |
| GET | /api/stores/{storeId}/tables/{tableId}/orders | 세션 주문 조회 |

---

## Unit 3: Admin Backend (관리자용 API)

### 프로젝트 구조
```
table-order-backend/
└── module-admin-api/               # 관리자용 API 모듈
    └── src/main/java/com/tableorder/admin/
        ├── auth/
        │   ├── AdminAuthController.java
        │   └── AdminAuthService.java
        ├── store/
        │   ├── StoreController.java
        │   └── StoreService.java
        ├── table/
        │   ├── TableManageController.java
        │   └── TableManageService.java
        ├── menu/
        │   ├── MenuManageController.java
        │   └── MenuManageService.java
        ├── order/
        │   ├── OrderManageController.java
        │   └── OrderManageService.java
        ├── sse/
        │   ├── SSEController.java
        │   └── SSEService.java
        └── s3/
            └── S3Service.java
```

### 책임
- 관리자 인증 (로그인, 회원가입, 로그아웃)
- 매장 등록/관리
- 테이블 관리 (생성, 이용 완료)
- 메뉴 CRUD + 이미지 업로드
- 주문 모니터링, 상태 변경, 삭제
- SSE 실시간 이벤트 스트리밍
- 과거 주문 내역 조회

### API 엔드포인트
| Method | Path | Purpose |
|---|---|---|
| POST | /api/admin/auth/login | 관리자 로그인 |
| POST | /api/admin/auth/register | 관리자 회원가입 |
| POST | /api/stores | 매장 등록 |
| GET | /api/stores/{storeCode} | 매장 조회 |
| POST | /api/stores/{storeId}/tables | 테이블 생성 |
| GET | /api/stores/{storeId}/tables | 테이블 목록 |
| PUT | /api/stores/{storeId}/tables/{tableId} | 테이블 수정 |
| POST | /api/stores/{storeId}/tables/{tableId}/complete | 이용 완료 |
| POST | /api/stores/{storeId}/menus | 메뉴 등록 |
| PUT | /api/stores/{storeId}/menus/{menuId} | 메뉴 수정 |
| DELETE | /api/stores/{storeId}/menus/{menuId} | 메뉴 삭제 |
| PUT | /api/stores/{storeId}/menus/order | 메뉴 순서 변경 |
| POST | /api/stores/{storeId}/categories | 카테고리 생성 |
| POST | /api/stores/{storeId}/menus/{menuId}/image | 이미지 업로드 |
| GET | /api/stores/{storeId}/orders | 매장 전체 주문 |
| PATCH | /api/stores/{storeId}/orders/{orderId}/status | 주문 상태 변경 |
| DELETE | /api/stores/{storeId}/orders/{orderId} | 주문 삭제 |
| GET | /api/stores/{storeId}/tables/{tableId}/history | 과거 내역 |
| GET | /api/stores/{storeId}/orders/stream | SSE 구독 |

---

## Unit 4: Customer Frontend (고객용 앱)

### 프로젝트 구조
```
table-order-frontend/
└── packages/
    └── customer-app/               # @table-order/customer-app
        └── src/
            ├── components/
            │   ├── menu/           # MenuCard, MenuDetail, CategoryTab
            │   ├── cart/           # CartItem, CartSummary, CartBadge
            │   ├── order/          # OrderConfirm, OrderSuccess, OrderHistory
            │   └── auth/           # TableSetup
            ├── pages/
            │   ├── MenuPage.tsx
            │   ├── CartPage.tsx
            │   ├── OrderConfirmPage.tsx
            │   ├── OrderHistoryPage.tsx
            │   └── SetupPage.tsx
            ├── hooks/
            │   ├── useCart.ts       # 장바구니 로직 (localStorage)
            │   ├── useTableAuth.ts  # 테이블 인증
            │   └── useOrders.ts     # 주문 관련
            ├── store/               # 상태 관리
            └── App.tsx
```

### 책임
- 태블릿 초기 설정 UI
- 자동 로그인 처리
- 메뉴 조회/탐색 UI
- 장바구니 관리 (로컬 저장)
- 주문 생성/확인 UI
- 주문 내역 조회 UI

---

## Unit 5: Admin Frontend (관리자용 앱)

### 프로젝트 구조
```
table-order-frontend/
└── packages/
    └── admin-app/                  # @table-order/admin-app
        └── src/
            ├── components/
            │   ├── auth/           # LoginForm, RegisterForm
            │   ├── dashboard/      # TableCard, OrderDetail, StatusBadge
            │   ├── table/          # TableList, TableSetup, HistoryModal
            │   ├── menu/           # MenuForm, MenuList, CategoryManager, OrderSort
            │   └── store/          # StoreRegisterForm
            ├── pages/
            │   ├── LoginPage.tsx
            │   ├── RegisterPage.tsx
            │   ├── DashboardPage.tsx
            │   ├── TableManagePage.tsx
            │   ├── MenuManagePage.tsx
            │   └── StoreRegisterPage.tsx
            ├── hooks/
            │   ├── useAdminAuth.ts  # 관리자 인증
            │   ├── useSSE.ts        # SSE 연결 관리
            │   ├── useOrders.ts     # 주문 관리
            │   └── useMenus.ts      # 메뉴 관리
            ├── store/               # 상태 관리
            └── App.tsx
```

### 책임
- 관리자 로그인/회원가입 UI
- 매장 등록 UI
- 실시간 주문 대시보드 (SSE)
- 주문 상태 관리 UI
- 테이블 관리 UI
- 메뉴 CRUD UI
- 과거 주문 내역 조회 UI

---

## 전체 프로젝트 구조 요약

```
workspace/
├── table-order-backend/
│   ├── build.gradle
│   ├── settings.gradle
│   ├── module-core/            # [Unit 1] 공통 설정, 보안
│   ├── module-domain/          # [Unit 1] 엔티티, Repository
│   ├── module-customer-api/    # [Unit 2] 고객용 API
│   ├── module-admin-api/       # [Unit 3] 관리자용 API
│   └── module-app/             # [Unit 1] Spring Boot 실행
│
└── table-order-frontend/
    ├── package.json
    ├── pnpm-workspace.yaml
    └── packages/
        ├── shared/             # [Unit 1] 공통 타입, API 클라이언트
        ├── customer-app/       # [Unit 4] 고객용 앱
        └── admin-app/          # [Unit 5] 관리자용 앱
```

---

## 개발 순서 (Parallel)

### Phase 1: 공통 기반 (Unit 1)
- Backend: module-core + module-domain
- Frontend: shared 패키지
- **산출물**: API 스펙 (OpenAPI), 공통 타입, 보안 설정

### Phase 2: 고객 기능 (Unit 2 + Unit 4, 동시)
- Backend: module-customer-api
- Frontend: customer-app
- **산출물**: 고객 주문 플로우 완성

### Phase 3: 관리자 기능 (Unit 3 + Unit 5, 동시)
- Backend: module-admin-api
- Frontend: admin-app
- **산출물**: 관리자 운영 플로우 완성

### Phase 4: 통합 및 테스트
- 전체 E2E 시나리오 검증
- SSE 실시간 통신 테스트
