# Domain Entities (Frontend Types) - Unit 4: Customer Frontend

## 1. API 응답 타입

### 공통 응답 래퍼
```typescript
interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string | null;
}

interface ApiErrorResponse {
  code: string;
  message: string;
  timestamp: string;
}
```

### 인증 관련
```typescript
interface TableLoginRequest {
  storeCode: string;
  password: string;
  // tableNumber는 서버가 스캔 순서대로 자동 배정
}

interface TableLoginResponse {
  token: string;
  storeId: number;
  tableId: number;
  tableNumber: number;     // 서버가 배정한 테이블 번호
}
```

### 카테고리
```typescript
interface Category {
  id: number;
  storeId: number;
  name: string;
  displayOrder: number;
}
```

### 메뉴
```typescript
interface Menu {
  id: number;
  storeId: number;
  categoryId: number;
  name: string;
  price: number;
  description: string | null;
  imageUrl: string | null;
  displayOrder: number;
}
```

### 주문
```typescript
interface OrderCreateRequest {
  items: OrderItemRequest[];
}

interface OrderItemRequest {
  menuId: number;
  menuName: string;
  quantity: number;
  unitPrice: number;
}

interface Order {
  id: number;
  storeId: number;
  tableId: number;
  sessionId: number;
  orderNumber: string;
  status: OrderStatus;
  totalAmount: number;
  items: OrderItem[];
  createdAt: string;       // ISO 8601
  updatedAt: string;
}

interface OrderItem {
  id: number;
  orderId: number;
  menuId: number;
  menuName: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
}

type OrderStatus = 'PENDING' | 'PREPARING' | 'COMPLETED';
```

---

## 2. 클라이언트 전용 타입

### 장바구니
```typescript
interface CartItem {
  menuId: number;
  menuName: string;
  unitPrice: number;
  quantity: number;        // 1 ~ 99
  imageUrl: string | null;
}

interface CartState {
  items: CartItem[];
  totalAmount: number;     // 자동 계산
  totalQuantity: number;   // 자동 계산
}
```

### 인증 정보 (localStorage)
```typescript
interface AuthInfo {
  token: string;
  storeId: number;
  tableId: number;
  storeCode: string;
  tableNumber: number;
  password: string;        // 클라이언트 암호화
}
```

### QR 코드 데이터
```typescript
interface QRCodeData {
  storeCode: string;       // 매장 코드
  totalTables: number;     // 총 테이블 수 (서버가 순서대로 배정)
}
```

---

## 3. SSE 이벤트 타입

```typescript
interface SSEEvent {
  type: SSEEventType;
  data: SSEEventData;
}

type SSEEventType =
  | 'ORDER_CREATED'
  | 'ORDER_STATUS_CHANGED'
  | 'ORDER_DELETED'
  | 'TABLE_COMPLETED';

interface OrderStatusChangedEvent {
  orderId: number;
  orderNumber: string;
  status: OrderStatus;
  updatedAt: string;
}

interface OrderDeletedEvent {
  orderId: number;
  orderNumber: string;
}

interface TableCompletedEvent {
  tableId: number;
  completedAt: string;
}

type SSEEventData =
  | OrderStatusChangedEvent
  | OrderDeletedEvent
  | TableCompletedEvent;
```

---

## 4. UI 상태 타입

### 네비게이션
```typescript
type AppRoute =
  | '/setup'              // 초기 설정
  | '/menu'               // 메뉴 (홈)
  | '/order-confirm'      // 주문 확인
  | '/order-success'      // 주문 성공
  | '/order-history';     // 주문 내역
```

### 주문 상태 표시
```typescript
interface OrderStatusDisplay {
  status: OrderStatus;
  label: string;          // 한국어 레이블
  color: string;          // Tailwind 색상 클래스
}

const ORDER_STATUS_MAP: Record<OrderStatus, OrderStatusDisplay> = {
  PENDING: {
    status: 'PENDING',
    label: '대기중',
    color: 'bg-yellow-100 text-yellow-800',
  },
  PREPARING: {
    status: 'PREPARING',
    label: '준비중',
    color: 'bg-blue-100 text-blue-800',
  },
  COMPLETED: {
    status: 'COMPLETED',
    label: '완료',
    color: 'bg-green-100 text-green-800',
  },
};
```

### 에러 상태
```typescript
interface AppError {
  type: 'network' | 'server' | 'auth' | 'unknown';
  message: string;
  retryable: boolean;
}
```

---

## 5. Zustand 스토어 타입

### AuthStore
```typescript
interface AuthStore {
  // State
  isAuthenticated: boolean;
  isLoading: boolean;
  authInfo: AuthInfo | null;
  error: string | null;

  // Actions
  login: (request: TableLoginRequest) => Promise<void>;
  autoLogin: () => Promise<void>;
  logout: () => void;
  clearError: () => void;
}
```

### CartStore
```typescript
interface CartStore {
  // State
  items: CartItem[];
  isPanelOpen: boolean;

  // Computed (getter)
  totalAmount: () => number;
  totalQuantity: () => number;

  // Actions
  addItem: (menu: Menu) => void;
  removeItem: (menuId: number) => void;
  updateQuantity: (menuId: number, quantity: number) => void;
  incrementQuantity: (menuId: number) => void;
  decrementQuantity: (menuId: number) => void;
  clearCart: () => void;
  openPanel: () => void;
  closePanel: () => void;
}
```

### MenuStore
```typescript
interface MenuStore {
  // State
  categories: Category[];
  menus: Menu[];
  selectedCategoryId: number | null;
  expandedMenuId: number | null;
  isLoading: boolean;
  error: string | null;

  // Actions
  fetchCategories: (storeId: number) => Promise<void>;
  fetchMenus: (storeId: number, categoryId: number) => Promise<void>;
  selectCategory: (categoryId: number) => void;
  expandMenu: (menuId: number | null) => void;
}
```

### OrderStore
```typescript
interface OrderStore {
  // State
  orders: Order[];
  isLoading: boolean;
  isSubmitting: boolean;
  error: string | null;
  failureCount: number;
  lastOrderResult: OrderResult | null;

  // Actions
  createOrder: (storeId: number, tableId: number, items: CartItem[]) => Promise<void>;
  fetchOrders: (storeId: number, tableId: number) => Promise<void>;
  updateOrderStatus: (orderId: number, status: OrderStatus) => void;
  removeOrder: (orderId: number) => void;
  clearLastResult: () => void;
  resetFailureCount: () => void;
}

interface OrderResult {
  success: boolean;
  orderNumber?: string;
  totalAmount?: number;
  createdAt?: string;
  error?: AppError;
}
```
