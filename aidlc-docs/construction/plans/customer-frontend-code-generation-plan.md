# Code Generation Plan - Unit 4: Customer Frontend

## 계획 개요
- **유닛**: Unit 4 - Customer Frontend (@table-order/customer-app)
- **코드 위치**: `table-order-frontend/packages/customer-app/`
- **의존성**: @table-order/shared (Unit 1), Unit 2 Customer Backend API (런타임)
- **할당 스토리**: US-C01 ~ US-C12 (12개)
- **기존 코드**: 스켈레톤 App.tsx, main.tsx, index.css, 설정 파일들

## 기존 shared 패키지 활용
- 타입: `@table-order/shared` (Category, Menu, Order, CartItem, ApiResponse 등)
- API 클라이언트: `apiClient` (Axios + 인터셉터)
- API 엔드포인트: `API` 상수
- 유틸리티: `formatPrice`, `formatDate`, `formatOrderStatus`, `getStatusColor`
- 상수: `TOKEN_KEY`, `MAX_QUANTITY`, `ORDER_SUCCESS_REDIRECT_DELAY` 등

---

## 실행 단계

### Step 1: 프로젝트 설정 업데이트
- [x] package.json에 html5-qrcode, axios 의존성 추가
- [x] vite.config.ts 업데이트 (코드 스플리팅, 빌드 최적화)
- [x] tailwind.config.js 업데이트 (커스텀 테마)
- [x] index.html 업데이트 (메타 태그, 가로 모드)
- **스토리**: 전체 기반

### Step 2: 타입 및 스키마 정의
- [x] src/types/index.ts (customer-app 전용 타입: AuthInfo, QRCodeData, OrderResult 등)
- [x] src/utils/schemas.ts (Zod 검증 스키마)
- **스토리**: US-C01, US-C05, US-C10

### Step 3: Zustand 스토어 생성
- [x] src/store/authStore.ts (인증 상태, persist)
- [x] src/store/cartStore.ts (장바구니 상태, persist)
- [x] src/store/menuStore.ts (메뉴/카테고리 상태)
- [x] src/store/orderStore.ts (주문 상태)
- [x] src/store/toastStore.ts (토스트 알림)
- **스토리**: US-C01~C12 전체

### Step 4: API 함수 생성
- [x] src/api/auth.ts (테이블 인증 API)
- [x] src/api/menu.ts (카테고리, 메뉴 조회 API)
- [x] src/api/order.ts (주문 생성, 조회 API)
- **스토리**: US-C01, US-C03, US-C10, US-C12

### Step 5: 커스텀 훅 생성
- [x] src/hooks/useTableAuth.ts (인증 로직)
- [x] src/hooks/useCart.ts (장바구니 로직)
- [x] src/hooks/useCartAutoClose.ts (3초 자동 닫기)
- [x] src/hooks/useOrders.ts (주문 로직)
- [x] src/hooks/useSSE.ts (SSE 연결 관리)
- [x] src/hooks/useQRScanner.ts (QR 스캐너)
- **스토리**: US-C01~C12 전체

### Step 6: 공통 컴포넌트 생성
- [x] src/components/common/ErrorBoundary.tsx
- [x] src/components/common/Toast.tsx
- [x] src/components/common/ConfirmDialog.tsx
- [x] src/components/common/LoadingSpinner.tsx
- [x] src/components/common/PlaceholderImage.tsx
- [x] src/components/common/OptimizedImage.tsx
- **스토리**: 전체 기반

### Step 7: 레이아웃 컴포넌트 생성
- [x] src/components/layout/AppLayout.tsx (전체 레이아웃)
- [x] src/components/layout/Header.tsx (헤더 + 장바구니 배지)
- [x] src/components/layout/CategorySidebar.tsx (좌측 카테고리)
- [x] src/components/layout/BottomBar.tsx (하단 네비게이션)
- [x] src/components/layout/AuthGuard.tsx (인증 가드)
- **스토리**: US-C03 (카테고리), 전체 레이아웃

### Step 8: 인증 페이지 (SetupPage)
- [x] src/components/auth/QRScanner.tsx
- [x] src/components/auth/ManualSetupForm.tsx
- [x] src/components/auth/PasswordInput.tsx
- [x] src/components/auth/TableAssignedConfirm.tsx
- [x] src/pages/SetupPage.tsx
- **스토리**: US-C01, US-C02

### Step 9: 메뉴 페이지 (MenuPage)
- [x] src/components/menu/MenuCard.tsx (축소/확장 카드)
- [x] src/components/menu/MenuGrid.tsx (3열 그리드)
- [x] src/components/menu/MenuSkeleton.tsx (로딩 스켈레톤)
- [x] src/pages/MenuPage.tsx
- **스토리**: US-C03, US-C04, US-C05

### Step 10: 장바구니 컴포넌트
- [x] src/components/cart/CartItem.tsx (개별 항목)
- [x] src/components/cart/CartSlidePanel.tsx (슬라이드 패널)
- **스토리**: US-C05, US-C06, US-C07, US-C08

### Step 11: 주문 페이지
- [x] src/pages/OrderConfirmPage.tsx (주문 확인)
- [x] src/pages/OrderSuccessPage.tsx (주문 성공)
- **스토리**: US-C09, US-C10, US-C11

### Step 12: 주문 내역 페이지
- [x] src/components/order/OrderCard.tsx (주문 카드)
- [x] src/components/order/SSEIndicator.tsx (연결 상태)
- [x] src/pages/OrderHistoryPage.tsx
- **스토리**: US-C12

### Step 13: App.tsx 업데이트 및 통합
- [x] src/App.tsx (라우팅, ErrorBoundary, Suspense, Toast)
- [x] src/main.tsx (BrowserRouter 확인)
- **스토리**: 전체 통합

### Step 14: 단위 테스트 생성
- [x] src/store/__tests__/cartStore.test.ts
- [x] src/store/__tests__/authStore.test.ts
- [x] src/store/__tests__/orderStore.test.ts
- [ ] src/hooks/__tests__/useCart.test.ts
- [ ] src/hooks/__tests__/useCartAutoClose.test.ts
- [ ] src/hooks/__tests__/useSSE.test.ts
- [x] src/utils/__tests__/schemas.test.ts
- [ ] src/api/__tests__/auth.test.ts
- [ ] src/api/__tests__/order.test.ts
- **목표**: 커버리지 80%+ (훅, 스토어, 유틸리티)

### Step 15: 코드 생성 요약 문서
- [ ] aidlc-docs/construction/customer-frontend/code/code-generation-summary.md

---

## 스토리 추적

| Story ID | 구현 Step | 상태 |
|---|---|---|
| US-C01 | Step 2, 4, 5, 8 | [ ] |
| US-C02 | Step 5, 7, 8 | [ ] |
| US-C03 | Step 4, 5, 7, 9 | [ ] |
| US-C04 | Step 9 | [ ] |
| US-C05 | Step 3, 5, 9, 10 | [ ] |
| US-C06 | Step 3, 5, 10 | [ ] |
| US-C07 | Step 3, 5, 10 | [ ] |
| US-C08 | Step 3, 10 | [ ] |
| US-C09 | Step 11 | [ ] |
| US-C10 | Step 4, 11 | [ ] |
| US-C11 | Step 11 | [ ] |
| US-C12 | Step 4, 5, 12 | [ ] |
