# Business Logic Model - Unit 1: Common/Shared

## 1. 인증 로직

### 관리자 로그인 프로세스
```
Input: storeCode, username, password
1. Store 조회 (storeCode) → 없으면 INVALID_CREDENTIALS
2. Admin 조회 (storeId + username) → 없으면 INVALID_CREDENTIALS
3. 계정 잠금 확인 (lockedUntil > now) → 잠겨있으면 ACCOUNT_LOCKED
4. 비밀번호 검증 (bcrypt.matches)
   - 실패: loginAttempts++, 5회 도달 시 lockedUntil = now + 15min
   - 성공: loginAttempts = 0
5. JWT 토큰 생성 (adminId, storeId, role=ADMIN, exp=16h)
Output: JWT token
```

### 테이블 인증 프로세스
```
Input: storeCode, tableNumber, password
1. Store 조회 (storeCode) → 없으면 INVALID_CREDENTIALS
2. Table 조회 (storeId + tableNumber) → 없으면 INVALID_CREDENTIALS
3. 비밀번호 검증 (bcrypt.matches) → 실패 시 INVALID_CREDENTIALS
4. JWT 토큰 생성 (tableId, storeId, role=TABLE, exp=365d)
Output: JWT token, storeId, tableId
```

### 관리자 회원가입 프로세스
```
Input: storeCode, username, password
1. Store 조회 (storeCode) → 없으면 STORE_NOT_FOUND
2. 중복 확인 (storeId + username) → 있으면 USERNAME_ALREADY_EXISTS
3. 비밀번호 정책 검증 (8자+, 영문+숫자) → 불충족 시 INVALID_PASSWORD
4. 비밀번호 해싱 (bcrypt, cost=10)
5. Admin 엔티티 생성 및 저장
Output: Admin 정보
```

---

## 2. 주문 생성 로직

### 주문 생성 프로세스
```
Input: storeId, tableId, items[{menuId, quantity}]
1. 테이블 소속 매장 검증 (table.storeId == storeId)
2. 활성 세션 조회/생성
   - ACTIVE 세션 있으면 사용
   - 없으면 새 TableSession 생성 (status=ACTIVE, startedAt=now)
3. 메뉴 유효성 검증
   - 각 menuId가 해당 매장에 존재하는지 확인
   - 존재하지 않는 메뉴 → MENU_NOT_FOUND
4. 주문 번호 생성 (BR-07 규칙)
5. 주문 항목 생성
   - 각 항목: menuName(스냅샷), unitPrice(스냅샷), quantity, subtotal 계산
6. 총 금액 계산 (모든 항목 subtotal 합)
7. Order 엔티티 생성 (status=PENDING)
8. SSE 이벤트 발행 (ORDER_CREATED)
Output: Order (주문번호, 총금액, 상태, 생성시각)
```

---

## 3. 주문 상태 변경 로직

### 상태 변경 프로세스
```
Input: orderId, newStatus
1. Order 조회 → 없으면 ORDER_NOT_FOUND
2. 매장 소속 검증 (order.storeId == 요청자의 storeId)
3. 상태 전이 검증 (BR-08)
   - PENDING → PREPARING: 허용
   - PREPARING → COMPLETED: 허용
   - 그 외: INVALID_STATUS_TRANSITION
4. 상태 업데이트
5. SSE 이벤트 발행 (ORDER_STATUS_CHANGED)
Output: 업데이트된 Order
```

---

## 4. 이용 완료 로직

### 테이블 세션 종료 프로세스
```
Input: tableId
1. Table 조회 → 없으면 TABLE_NOT_FOUND
2. ACTIVE 세션 조회 → 없으면 NO_ACTIVE_SESSION
3. 해당 세션의 모든 Order 조회
4. 각 Order를 OrderHistory로 변환
   - OrderItem 목록을 JSON으로 직렬화
   - completedAt = now
5. OrderHistory 일괄 저장
6. 원본 OrderItem 일괄 삭제
7. 원본 Order 일괄 삭제
8. TableSession.status = COMPLETED, completedAt = now
9. SSE 이벤트 발행 (TABLE_COMPLETED)
Output: 성공
```

**트랜잭션 요구사항**: 4~8 단계는 하나의 트랜잭션으로 원자적 처리

---

## 5. 메뉴 관리 로직

### 메뉴 생성 프로세스
```
Input: storeId, name, price, description, categoryId, imageUrl?
1. 카테고리 존재 확인 (categoryId, storeId) → 없으면 CATEGORY_NOT_FOUND
2. 가격 검증 (0 ≤ price ≤ 10,000,000) → 불충족 시 INVALID_PRICE
3. displayOrder 결정 (해당 카테고리 내 최대값 + 1)
4. Menu 엔티티 생성 및 저장
Output: Menu
```

### 메뉴 순서 변경 프로세스
```
Input: List<{menuId, displayOrder}>
1. 모든 menuId가 같은 매장 소속인지 검증
2. 각 메뉴의 displayOrder 업데이트
3. 일괄 저장
Output: 성공
```

---

## 6. SSE 이벤트 관리 로직

### SSE 구독 프로세스
```
Input: storeId
1. SseEmitter 생성 (timeout=30min)
2. 매장별 emitter 목록에 추가
3. 연결 종료/타임아웃 시 목록에서 제거
Output: SseEmitter (스트림)
```

### 이벤트 발행 프로세스
```
Input: storeId, eventType, eventData
1. 해당 매장의 모든 활성 emitter 조회
2. 각 emitter에 이벤트 전송
3. 전송 실패한 emitter는 목록에서 제거
Output: void
```

---

## 7. 보안 로직

### JWT 토큰 검증 프로세스
```
Input: Authorization header (Bearer token)
1. 토큰 추출 (Bearer 접두사 제거)
2. 서명 검증 (HS256, secret key)
3. 만료 시간 확인 (exp > now) → 만료 시 UNAUTHORIZED
4. 페이로드 추출 (userId/tableId, storeId, role)
5. SecurityContext에 인증 정보 설정
Output: 인증된 요청 컨텍스트
```

### 접근 제어 로직
```
Input: 요청 경로, 인증 정보
1. 공개 경로 확인 (/api/admin/auth/**, /api/table/auth/**, POST /api/stores)
   - 공개 경로면 통과
2. 토큰 역할 확인
   - ADMIN: 관리자 API 접근 가능
   - TABLE: 고객 API 접근 가능
3. 매장 소속 검증
   - 요청 경로의 storeId == 토큰의 storeId → 통과
   - 불일치 → 403 FORBIDDEN
4. (TABLE 역할) 테이블 소속 검증
   - 요청 경로의 tableId == 토큰의 tableId → 통과
   - 불일치 → 403 FORBIDDEN
Output: 허용/거부
```
