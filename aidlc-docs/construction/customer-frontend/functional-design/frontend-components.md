# Frontend Components - Unit 4: Customer Frontend

## 1. 라우팅 구조

```
App
├── /setup              → SetupPage (초기 설정)
├── /menu               → MenuPage (메뉴 - 홈, 기본 경로)
├── /order-confirm      → OrderConfirmPage (주문 확인)
├── /order-success      → OrderSuccessPage (주문 성공)
└── /order-history      → OrderHistoryPage (주문 내역)

인증 가드:
  - /setup: 인증 불필요
  - 나머지 모든 경로: 인증 필요 (미인증 시 /setup으로 리다이렉트)
```

---

## 2. 컴포넌트 계층 구조

```
App
├── AuthGuard                          # 인증 상태 확인 래퍼
│
├── SetupPage                          # 초기 설정 페이지
│   ├── QRScanner                      # QR 코드 스캐너 (storeCode + totalTables)
│   ├── ManualSetupForm                # 수동 입력 폼 (폴백)
│   ├── PasswordInput                  # 비밀번호 입력
│   └── TableAssignedConfirm           # 배정된 테이블 번호 확인
│
├── AppLayout                          # 인증 후 공통 레이아웃
│   ├── Header                         # 상단 헤더
│   │   ├── Logo                       # 로고
│   │   ├── TableInfo                  # 테이블 번호 표시
│   │   └── CartBadge                  # 장바구니 아이콘 + 수량 배지
│   │
│   ├── CategorySidebar                # 좌측 카테고리 사이드바
│   │   └── CategoryTab               # 개별 카테고리 탭
│   │
│   ├── MainContent                    # 중앙 메인 영역 (라우팅)
│   │   ├── MenuPage
│   │   │   ├── MenuGrid              # 3열 메뉴 그리드
│   │   │   │   ├── MenuCard          # 메뉴 카드 (축소 상태)
│   │   │   │   └── MenuCardExpanded  # 메뉴 카드 (확장 상태)
│   │   │   ├── MenuSkeleton          # 로딩 스켈레톤
│   │   │   └── EmptyMenu             # 메뉴 없음 안내
│   │   │
│   │   ├── OrderConfirmPage
│   │   │   ├── OrderSummary          # 주문 요약 목록
│   │   │   ├── OrderTotal            # 총 금액
│   │   │   └── ConfirmActions        # 확정/뒤로가기 버튼
│   │   │
│   │   ├── OrderSuccessPage
│   │   │   ├── SuccessIcon           # 성공 아이콘/애니메이션
│   │   │   ├── OrderInfo             # 주문 번호, 시각, 금액
│   │   │   └── RedirectTimer         # 5초 카운트다운
│   │   │
│   │   └── OrderHistoryPage
│   │       ├── OrderList             # 주문 목록
│   │       │   └── OrderCard         # 개별 주문 카드
│   │       │       ├── OrderStatusBadge  # 상태 배지
│   │       │       └── OrderItemList     # 주문 항목 (펼침)
│   │       ├── SSEIndicator          # SSE 연결 상태
│   │       └── EmptyOrders           # 주문 없음 안내
│   │
│   ├── CartSlidePanel                 # 우측 장바구니 슬라이드 패널
│   │   ├── CartHeader                # 패널 헤더 + 닫기 버튼
│   │   ├── CartItemList              # 장바구니 항목 목록
│   │   │   └── CartItem              # 개별 항목 (수량 조절)
│   │   ├── CartTotal                 # 총 금액
│   │   ├── CartActions               # 전체 비우기 + 주문하기
│   │   └── EmptyCart                 # 장바구니 비어있음 안내
│   │
│   └── BottomBar                      # 하단 네비게이션 바
│       ├── NavButton (메뉴)
│       ├── NavButton (주문내역)
│       └── OrderButton (주문하기 + 총액)
│
└── 공통 컴포넌트
    ├── Toast                          # 토스트 알림
    ├── ConfirmDialog                  # 확인 팝업
    ├── LoadingSpinner                 # 로딩 스피너
    ├── ErrorMessage                   # 에러 메시지
    ├── PlaceholderImage               # 이미지 플레이스홀더
    └── PriceDisplay                   # 금액 표시 (₩ 형식)
```

---

## 3. 컴포넌트 상세 명세

### 3.1 SetupPage

#### QRScanner
| Props | Type | Description |
|---|---|---|
| onScan | (data: QRCodeData) => void | QR 스캔 성공 콜백 (storeCode + totalTables) |
| onError | (error: string) => void | 스캔 에러 콜백 |
| onFallback | () => void | 수동 입력 전환 콜백 |

- **State**: isScanning, cameraPermission
- **동작**: 카메라 활성화 → QR 디코딩 → storeCode, totalTables 추출 → onScan 호출
- **에러**: 카메라 권한 거부 시 수동 입력 안내
- **data-testid**: `qr-scanner`, `qr-fallback-button`

#### ManualSetupForm
| Props | Type | Description |
|---|---|---|
| onSubmit | (storeCode: string, totalTables: number) => void | 폼 제출 콜백 |

- **State**: storeCode, totalTables, errors
- **검증**: Zod 스키마 (storeCode: 영문/숫자/하이픈, totalTables: 양의 정수)
- **용도**: QR 스캔 불가 시 매장 코드 + 총 테이블 수 수동 입력 폴백
- **data-testid**: `manual-setup-form`, `store-code-input`, `total-tables-input`, `manual-setup-submit`

#### PasswordInput
| Props | Type | Description |
|---|---|---|
| storeCode | string | 매장 코드 |
| totalTables | number | 총 테이블 수 |
| onSubmit | (password: string) => void | 비밀번호 제출 콜백 |
| isLoading | boolean | 로딩 상태 |
| error | string \| null | 에러 메시지 |

- **State**: password
- **검증**: 최소 4자리
- **동작**: 비밀번호 입력 후 서버에 인증 요청 → 서버가 테이블 번호 자동 배정
- **data-testid**: `password-input`, `password-submit-button`

#### TableAssignedConfirm
| Props | Type | Description |
|---|---|---|
| tableNumber | number | 배정된 테이블 번호 |
| onConfirm | () => void | 확인 후 메뉴 화면 이동 |

- **동작**: "테이블 N번으로 설정되었습니다" 표시 → 확인 버튼 또는 3초 후 자동 이동
- **data-testid**: `table-assigned-confirm`, `table-assigned-number`, `table-assigned-ok-button`

---

### 3.2 AppLayout

#### Header
| Props | Type | Description |
|---|---|---|
| tableNumber | number | 테이블 번호 |
| cartQuantity | number | 장바구니 총 수량 |
| onCartClick | () => void | 장바구니 아이콘 클릭 |

- **data-testid**: `app-header`, `table-info`, `cart-badge`

#### CategorySidebar
| Props | Type | Description |
|---|---|---|
| categories | Category[] | 카테고리 목록 |
| selectedId | number \| null | 선택된 카테고리 ID |
| onSelect | (categoryId: number) => void | 카테고리 선택 콜백 |

- **동작**: 카테고리 탭 시 onSelect 호출, 선택된 탭 하이라이트
- **data-testid**: `category-sidebar`, `category-tab-{id}`

#### CartSlidePanel
| Props | Type | Description |
|---|---|---|
| isOpen | boolean | 패널 열림 상태 |
| onClose | () => void | 닫기 콜백 |
| items | CartItem[] | 장바구니 항목 |
| totalAmount | number | 총 금액 |

- **State**: autoCloseTimer (3초)
- **동작**: 
  - 열림 시 우측에서 슬라이드 인
  - 3초 무활동 시 자동 닫기
  - 패널 내 터치/마우스 이벤트 시 타이머 리셋
- **data-testid**: `cart-slide-panel`, `cart-close-button`, `cart-clear-all-button`

#### BottomBar
| Props | Type | Description |
|---|---|---|
| totalAmount | number | 장바구니 총 금액 |
| cartQuantity | number | 장바구니 총 수량 |
| onOrderClick | () => void | 주문하기 클릭 |
| currentRoute | string | 현재 경로 |

- **동작**: 메뉴/주문내역 네비게이션 + 주문하기 버튼 (총액 표시)
- **data-testid**: `bottom-bar`, `nav-menu-button`, `nav-history-button`, `order-button`

---

### 3.3 MenuPage

#### MenuGrid
| Props | Type | Description |
|---|---|---|
| menus | Menu[] | 메뉴 목록 |
| expandedMenuId | number \| null | 확장된 메뉴 ID |
| onMenuClick | (menuId: number) => void | 메뉴 카드 클릭 |
| onAddToCart | (menu: Menu) => void | 장바구니 추가 |

- **레이아웃**: CSS Grid 3열
- **data-testid**: `menu-grid`

#### MenuCard
| Props | Type | Description |
|---|---|---|
| menu | Menu | 메뉴 데이터 |
| isExpanded | boolean | 확장 상태 |
| onClick | () => void | 카드 클릭 |
| onAddToCart | () => void | 장바구니 추가 |

- **축소 상태**: 이미지 썸네일, 메뉴명, 가격
- **확장 상태**: 큰 이미지, 메뉴명, 가격, 설명, "장바구니 담기" 버튼
- **애니메이션**: 확장/축소 300ms transition
- **data-testid**: `menu-card-{id}`, `menu-card-add-button-{id}`

---

### 3.4 OrderConfirmPage

#### OrderSummary
| Props | Type | Description |
|---|---|---|
| items | CartItem[] | 주문 항목 |
| tableNumber | number | 테이블 번호 |

- **표시**: 테이블 번호, 각 항목 (메뉴명, 수량, 단가, 소계)
- **data-testid**: `order-summary`, `order-summary-item-{menuId}`

#### ConfirmActions
| Props | Type | Description |
|---|---|---|
| onConfirm | () => void | 주문 확정 |
| onBack | () => void | 뒤로가기 |
| isSubmitting | boolean | 제출 중 상태 |
| totalAmount | number | 총 금액 |

- **동작**: 확정 버튼 클릭 시 isSubmitting=true → 중복 클릭 방지
- **data-testid**: `order-confirm-button`, `order-back-button`

---

### 3.5 OrderSuccessPage

| Props | Type | Description |
|---|---|---|
| orderNumber | string | 주문 번호 |
| totalAmount | number | 총 금액 |
| createdAt | string | 주문 시각 |

- **State**: countdown (5초 → 0)
- **동작**: 5초 카운트다운 후 /menu로 자동 리다이렉트
- **data-testid**: `order-success-page`, `order-success-number`, `order-success-back-button`

---

### 3.6 OrderHistoryPage

#### OrderCard
| Props | Type | Description |
|---|---|---|
| order | Order | 주문 데이터 |
| isExpanded | boolean | 펼침 상태 |
| onClick | () => void | 카드 클릭 (토글) |

- **축소**: 주문 번호, 시각, 총 금액, 상태 배지
- **펼침**: + 메뉴 항목 목록 (메뉴명, 수량, 단가, 소계)
- **data-testid**: `order-card-{id}`, `order-status-badge-{id}`

#### SSEIndicator
| Props | Type | Description |
|---|---|---|
| status | 'connected' \| 'reconnecting' \| 'disconnected' | 연결 상태 |

- **표시**: 연결됨(초록 점), 재연결 중(노란 점+텍스트), 끊김(빨간 점)
- **data-testid**: `sse-indicator`

---

## 4. 커스텀 훅

### useTableAuth
```typescript
function useTableAuth(): {
  isAuthenticated: boolean;
  isLoading: boolean;
  authInfo: AuthInfo | null;
  error: string | null;
  login: (request: TableLoginRequest) => Promise<void>;
  autoLogin: () => Promise<void>;
  logout: () => void;
}
```
- AuthStore 래퍼
- 자동 로그인 로직 포함

### useCart
```typescript
function useCart(): {
  items: CartItem[];
  totalAmount: number;
  totalQuantity: number;
  isPanelOpen: boolean;
  addItem: (menu: Menu) => void;
  removeItem: (menuId: number) => void;
  incrementQuantity: (menuId: number) => void;
  decrementQuantity: (menuId: number) => void;
  clearCart: () => void;
  openPanel: () => void;
  closePanel: () => void;
}
```
- CartStore 래퍼
- localStorage 동기화

### useCartAutoClose
```typescript
function useCartAutoClose(
  isOpen: boolean,
  onClose: () => void,
  timeout?: number  // default: 3000ms
): {
  resetTimer: () => void;
}
```
- 3초 무활동 자동 닫기 타이머 관리
- 패널 내 이벤트 시 resetTimer 호출

### useOrders
```typescript
function useOrders(): {
  orders: Order[];
  isLoading: boolean;
  isSubmitting: boolean;
  error: string | null;
  failureCount: number;
  lastOrderResult: OrderResult | null;
  createOrder: (items: CartItem[]) => Promise<void>;
  fetchOrders: () => Promise<void>;
  clearLastResult: () => void;
}
```
- OrderStore 래퍼
- storeId, tableId 자동 주입

### useSSE
```typescript
function useSSE(storeId: number): {
  status: 'connected' | 'reconnecting' | 'disconnected';
  subscribe: (onEvent: (event: SSEEvent) => void) => void;
  unsubscribe: () => void;
}
```
- SSE 연결 관리
- 자동 재연결 (3초 간격, 최대 5회)
- 컴포넌트 언마운트 시 자동 해제

### useQRScanner
```typescript
function useQRScanner(): {
  isScanning: boolean;
  hasPermission: boolean | null;
  startScan: () => void;
  stopScan: () => void;
  error: string | null;
}
```
- 카메라 권한 관리
- QR 디코딩 로직

---

## 5. API 연동 매핑

| 컴포넌트 | API 엔드포인트 | 용도 |
|---|---|---|
| PasswordInput | POST /api/table/auth/login | 테이블 인증 (서버가 테이블 번호 자동 배정) |
| useTableAuth (autoLogin) | POST /api/table/auth/login | 자동 재인증 |
| CategorySidebar | GET /api/stores/{storeId}/categories | 카테고리 목록 |
| MenuGrid | GET /api/stores/{storeId}/menus?categoryId={id} | 메뉴 목록 |
| OrderConfirmPage | POST /api/stores/{storeId}/tables/{tableId}/orders | 주문 생성 |
| OrderHistoryPage | GET /api/stores/{storeId}/tables/{tableId}/orders | 주문 조회 |
| useSSE | GET /api/stores/{storeId}/orders/stream | SSE 구독 |

---

## 6. 스토리 → 컴포넌트 매핑

| Story ID | 주요 컴포넌트 |
|---|---|
| US-C01 | SetupPage, QRScanner, ManualSetupForm, PasswordInput, TableAssignedConfirm |
| US-C02 | AuthGuard, useTableAuth |
| US-C03 | MenuPage, CategorySidebar, MenuGrid, MenuCard |
| US-C04 | MenuCardExpanded (확장 카드) |
| US-C05 | MenuCard (장바구니 담기), CartSlidePanel, Toast |
| US-C06 | CartItem (수량 조절), CartTotal |
| US-C07 | CartSlidePanel (삭제, 비우기), ConfirmDialog |
| US-C08 | CartTotal, PriceDisplay, BottomBar |
| US-C09 | OrderConfirmPage, OrderSummary, ConfirmActions |
| US-C10 | OrderSuccessPage, RedirectTimer |
| US-C11 | OrderConfirmPage (에러 처리), ErrorMessage |
| US-C12 | OrderHistoryPage, OrderCard, SSEIndicator |
