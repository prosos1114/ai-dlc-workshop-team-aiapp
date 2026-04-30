# Logical Components - Unit 4: Customer Frontend

## 시스템 논리적 구성도

```
+------------------------------------------------------------------+
|                    Customer App (React SPA)                        |
|  +------------------------------------------------------------+  |
|  |  App Entry                                                  |  |
|  |  ErrorBoundary → Router → AuthGuard → AppLayout            |  |
|  +------------------------------------------------------------+  |
|  |  Pages (Route-based Code Splitting)                         |  |
|  |  SetupPage(lazy) | MenuPage | OrderConfirm(lazy)           |  |
|  |  OrderSuccess(lazy) | OrderHistory(lazy)                   |  |
|  +------------------------------------------------------------+  |
|  |  State Management (Zustand)                                 |  |
|  |  AuthStore(persist) | CartStore(persist)                   |  |
|  |  MenuStore | OrderStore | ToastStore                       |  |
|  +------------------------------------------------------------+  |
|  |  API Layer                                                  |  |
|  |  Axios Client → Request Interceptor (토큰 첨부)             |  |
|  |              → Response Interceptor (에러 처리/재시도)       |  |
|  +------------------------------------------------------------+  |
|  |  Real-time Layer                                            |  |
|  |  EventSource (SSE) → 자동 재연결 → 상태 동기화             |  |
|  +------------------------------------------------------------+  |
|  |  Utilities                                                  |  |
|  |  Zod Schemas | Price Formatter | QR Decoder                |  |
|  +------------------------------------------------------------+  |
+------------------------------------------------------------------+
         |                              |
         v                              v
+------------------+          +------------------+
|  Backend API     |          |  SSE Endpoint    |
|  (REST)          |          |  (EventSource)   |
+------------------+          +------------------+
         |
         v
+------------------+
|  localStorage    |
|  (Auth + Cart)   |
+------------------+
```

---

## 논리적 컴포넌트 상세

### 1. App Entry Layer

#### ErrorBoundary
- **역할**: 전체 앱 크래시 방지, 에러 격리
- **위치**: App 최상위 + 각 페이지 단위
- **동작**: 렌더링 에러 캐치 → 에러 폴백 UI 표시 → 재시도 옵션

#### AuthGuard
- **역할**: 인증 상태 확인, 미인증 시 리다이렉트
- **위치**: Router 내부, 인증 필요 라우트 래핑
- **동작**: AuthStore.isAuthenticated 확인 → false면 /setup 리다이렉트

#### Router
- **역할**: SPA 라우팅, 코드 스플리팅
- **구현**: React Router v6 + React.lazy + Suspense
- **라우트**: /setup, /menu(홈), /order-confirm, /order-success, /order-history

---

### 2. State Management Layer

#### AuthStore (persist)
- **역할**: 인증 상태 관리
- **persist**: localStorage (`auth-storage`)
- **상태**: isAuthenticated, isLoading, authInfo, error
- **액션**: login, autoLogin, logout, clearError

#### CartStore (persist)
- **역할**: 장바구니 상태 관리
- **persist**: localStorage (`cart-storage`, items만)
- **상태**: items, isPanelOpen
- **계산**: totalAmount(), totalQuantity()
- **액션**: addItem, removeItem, updateQuantity, clearCart, openPanel, closePanel

#### MenuStore (no persist)
- **역할**: 메뉴/카테고리 데이터 관리
- **캐싱**: 메모리 내 (세션 동안 카테고리별 캐시)
- **상태**: categories, menus, selectedCategoryId, expandedMenuId, isLoading, error
- **액션**: fetchCategories, fetchMenus, selectCategory, expandMenu

#### OrderStore (no persist)
- **역할**: 주문 데이터 관리
- **동기화**: SSE 이벤트로 실시간 업데이트
- **상태**: orders, isLoading, isSubmitting, error, failureCount, lastOrderResult
- **액션**: createOrder, fetchOrders, updateOrderStatus, removeOrder

#### ToastStore (no persist)
- **역할**: 전역 토스트 알림 관리
- **상태**: toasts[]
- **액션**: showToast(message, type, duration)
- **자동 제거**: duration(기본 3초) 후 자동 삭제

---

### 3. API Layer

#### Axios Client
- **역할**: HTTP 클라이언트, 인터셉터 관리
- **설정**: baseURL(환경변수), timeout(10초)
- **Request 인터셉터**: Authorization 헤더 자동 첨부
- **Response 인터셉터**:
  - 401 → 자동 재인증 시도 → 실패 시 /setup 리다이렉트
  - 403 → "접근 권한이 없습니다" 토스트
  - 네트워크 오류 → 자동 재시도 (최대 2회, 1초/2초 간격) → 실패 시 토스트
  - 5xx → "잠시 후 다시 시도해주세요" 토스트
  - 4xx → 서버 에러 메시지 토스트

#### API Functions
- **역할**: 엔드포인트별 API 호출 함수
- **구조**: `api/auth.ts`, `api/menu.ts`, `api/order.ts`
- **타입**: 요청/응답 타입 정의 (domain-entities.md 참조)

---

### 4. Real-time Layer

#### SSE Manager (useSSE 훅)
- **역할**: Server-Sent Events 연결 관리
- **연결**: EventSource (브라우저 내장 API)
- **재연결**: 3초 간격, 최대 5회
- **탭 전환**: visibilitychange 이벤트로 연결 상태 확인
- **이벤트 처리**:
  - ORDER_STATUS_CHANGED → OrderStore.updateOrderStatus
  - ORDER_DELETED → OrderStore.removeOrder
  - TABLE_COMPLETED → AuthStore 세션 리셋

---

### 5. Utilities Layer

#### Zod Schemas
- **역할**: 입력 검증 스키마 정의
- **파일**: `utils/schemas.ts`
- **스키마**: tableSetupSchema, cartQuantitySchema, orderCreateSchema

#### Price Formatter
- **역할**: 금액 형식 변환
- **파일**: `utils/format.ts`
- **함수**: formatPrice(amount) → "₩12,000"
- **구현**: Intl.NumberFormat('ko-KR', { style: 'currency', currency: 'KRW' })

#### QR Decoder
- **역할**: QR 코드 스캔 및 디코딩
- **라이브러리**: html5-qrcode (lazy loading)
- **파일**: `utils/qr.ts`
- **출력**: QRCodeData { storeCode, totalTables }

---

## 환경 설정

### 환경변수 (.env)
```
VITE_API_URL=http://localhost:8080    # 백엔드 API URL
VITE_APP_TITLE=테이블오더              # 앱 타이틀
```

### Vite 설정 요약
```
빌드 타겟: es2020
소스맵: false (프로덕션)
청크 분리: vendor, state, form, main, setup(lazy), order(lazy)
청크 경고: 200KB
압축: terser
```

### Tailwind 설정 요약
```
콘텐츠 경로: src/**/*.{ts,tsx}
테마 확장: 커스텀 색상 (주문 상태별), 터치 영역 유틸리티
플러그인: @tailwindcss/forms (폼 스타일링)
```

---

## 디렉토리 구조

```
table-order-frontend/packages/customer-app/
├── src/
│   ├── api/                    # API 호출 함수
│   │   ├── client.ts           # Axios 인스턴스 + 인터셉터
│   │   ├── auth.ts             # 인증 API
│   │   ├── menu.ts             # 메뉴 API
│   │   └── order.ts            # 주문 API
│   ├── components/             # 컴포넌트
│   │   ├── auth/               # 인증 관련
│   │   ├── menu/               # 메뉴 관련
│   │   ├── cart/               # 장바구니 관련
│   │   ├── order/              # 주문 관련
│   │   ├── layout/             # 레이아웃 (Header, Sidebar, BottomBar)
│   │   └── common/             # 공통 (Toast, ConfirmDialog, ErrorBoundary)
│   ├── hooks/                  # 커스텀 훅
│   │   ├── useTableAuth.ts
│   │   ├── useCart.ts
│   │   ├── useCartAutoClose.ts
│   │   ├── useOrders.ts
│   │   ├── useSSE.ts
│   │   └── useQRScanner.ts
│   ├── pages/                  # 페이지 컴포넌트
│   │   ├── SetupPage.tsx
│   │   ├── MenuPage.tsx
│   │   ├── OrderConfirmPage.tsx
│   │   ├── OrderSuccessPage.tsx
│   │   └── OrderHistoryPage.tsx
│   ├── store/                  # Zustand 스토어
│   │   ├── authStore.ts
│   │   ├── cartStore.ts
│   │   ├── menuStore.ts
│   │   ├── orderStore.ts
│   │   └── toastStore.ts
│   ├── utils/                  # 유틸리티
│   │   ├── format.ts           # 금액 포맷터
│   │   ├── schemas.ts          # Zod 스키마
│   │   └── qr.ts               # QR 디코더
│   ├── types/                  # TypeScript 타입
│   │   └── index.ts
│   ├── App.tsx                 # 앱 엔트리
│   ├── main.tsx                # React 렌더링
│   └── index.css               # Tailwind 임포트
├── public/
│   └── placeholder.svg         # 이미지 플레이스홀더
├── index.html
├── vite.config.ts
├── tailwind.config.ts
├── tsconfig.json
└── package.json
```
