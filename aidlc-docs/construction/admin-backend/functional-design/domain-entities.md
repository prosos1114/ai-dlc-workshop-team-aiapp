# Domain Entities - Unit 3: Admin Backend

## 개요
Unit 3은 Unit 1 (Common/Shared)에서 정의된 도메인 엔티티를 직접 사용합니다.
이 문서는 Admin Backend API에서 사용하는 엔티티와 Admin API 전용 DTO를 정의합니다.

---

## 사용 엔티티 (Unit 1 참조)

| 엔티티 | 패키지 | Admin API 사용 목적 |
|---|---|---|
| Admin | domain.admin | 관리자 인증, 회원가입 |
| Store | domain.store | 매장 등록, 조회 |
| TableEntity | domain.table | 테이블 생성, 수정, 이용 완료 |
| TableSession | domain.table | 세션 관리, 이용 완료 처리 |
| Category | domain.menu | 카테고리 생성, 조회 |
| Menu | domain.menu | 메뉴 CRUD, 이미지 업로드 |
| Order | domain.order | 주문 조회, 상태 변경, 삭제 |
| OrderItem | domain.order | 주문 항목 조회 |
| OrderHistory | domain.order | 과거 주문 내역 조회 |

---

## Admin API Request DTOs

### 인증 (Auth)

#### LoginRequest
```
{
  storeCode: String (필수, 3~50자)
  username: String (필수, 1~50자)
  password: String (필수, 8자 이상)
}
```

#### RegisterRequest
```
{
  storeCode: String (필수, 3~50자)
  username: String (필수, 1~50자)
  password: String (필수, 8자 이상, 영문+숫자 조합)
}
```

### 매장 (Store)

#### StoreCreateRequest
```
{
  name: String (필수, 1~100자)
  code: String (필수, 3~50자, 영문소문자+숫자+하이픈)
}
```

### 테이블 (Table)

#### TableCreateRequest
```
{
  tableNumber: Integer (필수, 1 이상)
  password: String (필수, 4자 이상)
}
```

#### TableUpdateRequest
```
{
  password: String (선택, 4자 이상, 변경 시에만)
}
```

### 메뉴 (Menu)

#### MenuCreateRequest
```
{
  categoryId: Long (필수)
  name: String (필수, 1~100자)
  price: Integer (필수, 0~10,000,000)
  description: String (선택, 최대 500자)
}
```

#### MenuUpdateRequest
```
{
  categoryId: Long (필수)
  name: String (필수, 1~100자)
  price: Integer (필수, 0~10,000,000)
  description: String (선택, 최대 500자)
}
```

#### MenuOrderUpdateRequest
```
{
  menuOrders: List<MenuOrderItem> (필수)
}

MenuOrderItem:
{
  menuId: Long (필수)
  displayOrder: Integer (필수, 0 이상)
}
```

### 카테고리 (Category)

#### CategoryCreateRequest
```
{
  name: String (필수, 1~50자)
}
```

### 주문 (Order)

#### OrderStatusUpdateRequest
```
{
  status: String (필수, "PREPARING" 또는 "COMPLETED")
}
```

---

## Admin API Response DTOs

### TokenResponse
```
{
  token: String
  expiresIn: Long (밀리초)
}
```

### AdminResponse
```
{
  id: Long
  storeId: Long
  username: String
  createdAt: LocalDateTime
}
```

### StoreResponse
```
{
  id: Long
  name: String
  code: String
  createdAt: LocalDateTime
}
```

### TableResponse
```
{
  id: Long
  storeId: Long
  tableNumber: Integer
  hasActiveSession: Boolean
  currentSessionOrderCount: Integer
  currentSessionTotalAmount: Integer
}
```

### CategoryResponse
```
{
  id: Long
  name: String
  displayOrder: Integer
  menuCount: Integer
}
```

### MenuResponse
```
{
  id: Long
  categoryId: Long
  categoryName: String
  name: String
  price: Integer
  description: String
  imageUrl: String
  displayOrder: Integer
}
```

### OrderResponse
```
{
  id: Long
  orderNumber: String
  tableId: Long
  tableNumber: Integer
  status: String
  totalAmount: Integer
  items: List<OrderItemResponse>
  createdAt: LocalDateTime
}
```

### OrderItemResponse
```
{
  id: Long
  menuName: String
  quantity: Integer
  unitPrice: Integer
  subtotal: Integer
}
```

### OrderHistoryResponse
```
{
  id: Long
  orderNumber: String
  tableNumber: Integer
  totalAmount: Integer
  items: String (JSON)
  orderedAt: LocalDateTime
  completedAt: LocalDateTime
}
```

### SSE Event DTOs

#### OrderEventData
```
{
  type: String ("ORDER_CREATED" | "ORDER_STATUS_CHANGED" | "ORDER_DELETED" | "TABLE_COMPLETED")
  orderId: Long
  orderNumber: String
  tableNumber: Integer
  status: String
  totalAmount: Integer
  items: List<OrderItemSummary> (ORDER_CREATED 시에만)
  timestamp: LocalDateTime
}
```
