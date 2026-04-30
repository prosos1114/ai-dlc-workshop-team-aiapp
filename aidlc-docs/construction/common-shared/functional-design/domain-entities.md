# Domain Entities - Unit 1: Common/Shared

## Entity Relationship Diagram (Text)

```
Store (1) ──── (N) Admin
Store (1) ──── (N) Table
Store (1) ──── (N) Category
Category (1) ──── (N) Menu
Table (1) ──── (N) TableSession
TableSession (1) ──── (N) Order
Order (1) ──── (N) OrderItem
OrderItem (N) ──── (1) Menu
Table (1) ──── (N) OrderHistory
```

---

## Entity Definitions

### Store (매장)
| 필드 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | Long | PK, Auto | 매장 고유 ID |
| name | String | NOT NULL, max 100 | 매장명 |
| code | String | NOT NULL, UNIQUE, max 50 | 매장 식별 코드 (영문, 숫자, 하이픈) |
| createdAt | LocalDateTime | NOT NULL | 생성 시각 |
| updatedAt | LocalDateTime | NOT NULL | 수정 시각 |

---

### Admin (관리자)
| 필드 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | Long | PK, Auto | 관리자 고유 ID |
| storeId | Long | FK(Store), NOT NULL | 소속 매장 |
| username | String | NOT NULL, max 50 | 사용자명 |
| password | String | NOT NULL | 비밀번호 (bcrypt 해시) |
| loginAttempts | Integer | NOT NULL, default 0 | 연속 로그인 실패 횟수 |
| lockedUntil | LocalDateTime | NULLABLE | 계정 잠금 해제 시각 |
| createdAt | LocalDateTime | NOT NULL | 생성 시각 |

**Unique Constraint**: (storeId, username)

---

### Table (테이블)
| 필드 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | Long | PK, Auto | 테이블 고유 ID |
| storeId | Long | FK(Store), NOT NULL | 소속 매장 |
| tableNumber | Integer | NOT NULL | 테이블 번호 |
| password | String | NOT NULL | 테이블 비밀번호 (bcrypt 해시) |
| createdAt | LocalDateTime | NOT NULL | 생성 시각 |
| updatedAt | LocalDateTime | NOT NULL | 수정 시각 |

**Unique Constraint**: (storeId, tableNumber)

---

### TableSession (테이블 세션)
| 필드 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | Long | PK, Auto | 세션 고유 ID |
| tableId | Long | FK(Table), NOT NULL | 테이블 |
| status | Enum | NOT NULL | ACTIVE / COMPLETED |
| startedAt | LocalDateTime | NOT NULL | 세션 시작 시각 |
| completedAt | LocalDateTime | NULLABLE | 이용 완료 시각 |

---

### Category (카테고리)
| 필드 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | Long | PK, Auto | 카테고리 고유 ID |
| storeId | Long | FK(Store), NOT NULL | 소속 매장 |
| name | String | NOT NULL, max 50 | 카테고리명 |
| displayOrder | Integer | NOT NULL, default 0 | 노출 순서 |
| createdAt | LocalDateTime | NOT NULL | 생성 시각 |

---

### Menu (메뉴)
| 필드 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | Long | PK, Auto | 메뉴 고유 ID |
| storeId | Long | FK(Store), NOT NULL | 소속 매장 |
| categoryId | Long | FK(Category), NOT NULL | 카테고리 |
| name | String | NOT NULL, max 100 | 메뉴명 |
| price | Integer | NOT NULL, min 0 | 가격 (원) |
| description | String | NULLABLE, max 500 | 메뉴 설명 |
| imageUrl | String | NULLABLE, max 500 | 이미지 URL (S3) |
| displayOrder | Integer | NOT NULL, default 0 | 노출 순서 |
| createdAt | LocalDateTime | NOT NULL | 생성 시각 |
| updatedAt | LocalDateTime | NOT NULL | 수정 시각 |

---

### Order (주문)
| 필드 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | Long | PK, Auto | 주문 고유 ID |
| storeId | Long | FK(Store), NOT NULL | 매장 |
| tableId | Long | FK(Table), NOT NULL | 테이블 |
| sessionId | Long | FK(TableSession), NOT NULL | 세션 |
| orderNumber | String | NOT NULL, UNIQUE | 주문 번호 (표시용) |
| status | Enum | NOT NULL | PENDING / PREPARING / COMPLETED |
| totalAmount | Integer | NOT NULL | 총 주문 금액 |
| createdAt | LocalDateTime | NOT NULL | 주문 시각 |
| updatedAt | LocalDateTime | NOT NULL | 수정 시각 |

---

### OrderItem (주문 항목)
| 필드 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | Long | PK, Auto | 항목 고유 ID |
| orderId | Long | FK(Order), NOT NULL | 주문 |
| menuId | Long | FK(Menu), NOT NULL | 메뉴 |
| menuName | String | NOT NULL | 주문 시점 메뉴명 (스냅샷) |
| quantity | Integer | NOT NULL, min 1 | 수량 |
| unitPrice | Integer | NOT NULL | 주문 시점 단가 (스냅샷) |
| subtotal | Integer | NOT NULL | 소계 (quantity * unitPrice) |

---

### OrderHistory (과거 주문 이력)
| 필드 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | Long | PK, Auto | 이력 고유 ID |
| storeId | Long | NOT NULL | 매장 |
| tableId | Long | NOT NULL | 테이블 |
| sessionId | Long | NOT NULL | 세션 ID |
| orderNumber | String | NOT NULL | 주문 번호 |
| totalAmount | Integer | NOT NULL | 총 금액 |
| items | JSON | NOT NULL | 주문 항목 목록 (JSON) |
| orderedAt | LocalDateTime | NOT NULL | 원래 주문 시각 |
| completedAt | LocalDateTime | NOT NULL | 이용 완료 시각 |

---

## Enum Definitions

### SessionStatus
```
ACTIVE      - 현재 활성 세션
COMPLETED   - 이용 완료된 세션
```

### OrderStatus
```
PENDING     - 대기중 (주문 접수됨)
PREPARING   - 준비중 (관리자가 확인)
COMPLETED   - 완료 (음식 제공 완료)
```
