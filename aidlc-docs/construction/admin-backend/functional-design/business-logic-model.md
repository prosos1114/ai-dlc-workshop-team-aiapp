# Business Logic Model - Unit 3: Admin Backend

## 개요
Admin Backend의 서비스 레이어 비즈니스 로직 흐름을 상세하게 정의합니다.

---

## 1. 관리자 인증 흐름

### 1.1 로그인 (AdminAuthService.login)
```
Input: storeCode, username, password
  1. StoreRepository.findByCode(storeCode)
     → 미존재: throw InvalidCredentialsException
  2. AdminRepository.findByStoreIdAndUsername(store.id, username)
     → 미존재: throw InvalidCredentialsException
  3. admin.isLocked() 확인
     → 잠금 상태: throw AccountLockedException
  4. PasswordEncoder.matches(password, admin.password)
     → 불일치:
       a. admin.incrementLoginAttempts()
       b. loginAttempts >= 5 → admin.lock(now + 15분)
       c. AdminRepository.save(admin)
       d. throw InvalidCredentialsException
  5. 성공:
     a. admin.resetLoginAttempts()
     b. AdminRepository.save(admin)
     c. token = JwtTokenProvider.createAdminToken(admin.id, store.id)
     d. return TokenResponse(token, adminExpiration)
```

### 1.2 회원가입 (AdminAuthService.register)
```
Input: storeCode, username, password
  1. StoreRepository.findByCode(storeCode)
     → 미존재: throw NotFoundException("Store")
  2. AdminRepository.existsByStoreIdAndUsername(store.id, username)
     → 존재: throw DuplicateResourceException("Admin")
  3. encodedPassword = PasswordEncoder.encode(password)
  4. admin = Admin.builder()
       .storeId(store.id)
       .username(username)
       .password(encodedPassword)
       .build()
  5. AdminRepository.save(admin)
  6. return AdminResponse.from(admin)
```

---

## 2. 매장 관리 흐름

### 2.1 매장 등록 (StoreService.createStore)
```
Input: name, code
  1. StoreRepository.existsByCode(code)
     → 존재: throw DuplicateResourceException("Store")
  2. store = Store.builder().name(name).code(code).build()
  3. StoreRepository.save(store)
  4. return StoreResponse.from(store)
```

### 2.2 매장 조회 (StoreService.getStoreByCode)
```
Input: storeCode
  1. StoreRepository.findByCode(storeCode)
     → 미존재: throw NotFoundException("Store")
  2. return StoreResponse.from(store)
```

---

## 3. 테이블 관리 흐름

### 3.1 테이블 생성 (TableManageService.createTable)
```
Input: storeId, tableNumber, password
  1. TableRepository.existsByStoreIdAndTableNumber(storeId, tableNumber)
     → 존재: throw DuplicateResourceException("Table")
  2. encodedPassword = PasswordEncoder.encode(password)
  3. table = TableEntity.builder()
       .storeId(storeId)
       .tableNumber(tableNumber)
       .password(encodedPassword)
       .build()
  4. TableRepository.save(table)
  5. return TableResponse.from(table, false, 0, 0)
```

### 3.2 테이블 목록 조회 (TableManageService.getTablesByStore)
```
Input: storeId
  1. tables = TableRepository.findByStoreId(storeId)
  2. 각 테이블에 대해:
     a. activeSession = TableSessionRepository.findByTableIdAndStatus(table.id, ACTIVE)
     b. hasActiveSession = activeSession.isPresent()
     c. if hasActiveSession:
        - orders = OrderRepository.findBySessionId(session.id)
        - orderCount = orders.size()
        - totalAmount = sum(orders.totalAmount)
     d. else: orderCount = 0, totalAmount = 0
  3. return List<TableResponse>
```

### 3.3 테이블 수정 (TableManageService.updateTable)
```
Input: storeId, tableId, password
  1. table = TableRepository.findById(tableId)
     → 미존재 또는 storeId 불일치: throw NotFoundException("Table")
  2. encodedPassword = PasswordEncoder.encode(password)
  3. table.updatePassword(encodedPassword)
  4. TableRepository.save(table)
  5. return TableResponse.from(table, ...)
```

### 3.4 이용 완료 (TableManageService.completeTable) - @Transactional
```
Input: storeId, tableId
  1. table = TableRepository.findById(tableId)
     → 미존재 또는 storeId 불일치: throw NotFoundException("Table")
  2. session = TableSessionRepository.findByTableIdAndStatus(tableId, ACTIVE)
     → 미존재: throw NoActiveSessionException(tableId)
  3. orders = OrderRepository.findBySessionId(session.id)
  4. 각 order에 대해:
     a. items JSON 직렬화 (menuName, quantity, unitPrice, subtotal)
     b. OrderHistory.builder()
          .storeId(order.storeId)
          .tableId(order.tableId)
          .sessionId(order.sessionId)
          .orderNumber(order.orderNumber)
          .totalAmount(order.totalAmount)
          .items(itemsJson)
          .orderedAt(order.createdAt)
          .completedAt(now)
          .build()
     c. OrderHistoryRepository.save(history)
     d. OrderRepository.delete(order) // CASCADE로 OrderItem도 삭제
  5. session.complete()
  6. TableSessionRepository.save(session)
  7. SSEService.publishOrderEvent(storeId, TABLE_COMPLETED event)
```

---

## 4. 메뉴 관리 흐름

### 4.1 메뉴 등록 (MenuManageService.createMenu)
```
Input: storeId, categoryId, name, price, description
  1. category = CategoryRepository.findById(categoryId)
     → 미존재 또는 storeId 불일치: throw NotFoundException("Category")
  2. displayOrder = MenuRepository.countByStoreIdAndCategoryId(storeId, categoryId)
  3. menu = Menu.builder()
       .storeId(storeId)
       .categoryId(categoryId)
       .name(name)
       .price(price)
       .description(description)
       .displayOrder(displayOrder)
       .build()
  4. MenuRepository.save(menu)
  5. return MenuResponse.from(menu, category.name)
```

### 4.2 메뉴 수정 (MenuManageService.updateMenu)
```
Input: storeId, menuId, categoryId, name, price, description
  1. menu = MenuRepository.findByIdAndStoreId(menuId, storeId)
     → 미존재: throw NotFoundException("Menu")
  2. if categoryId changed:
     category = CategoryRepository.findById(categoryId)
     → 미존재 또는 storeId 불일치: throw NotFoundException("Category")
  3. menu.update(name, price, description, categoryId)
  4. MenuRepository.save(menu)
  5. return MenuResponse.from(menu, categoryName)
```

### 4.3 메뉴 삭제 (MenuManageService.deleteMenu)
```
Input: storeId, menuId
  1. menu = MenuRepository.findByIdAndStoreId(menuId, storeId)
     → 미존재: throw NotFoundException("Menu")
  2. MenuRepository.delete(menu)
```

### 4.4 메뉴 순서 변경 (MenuManageService.updateMenuOrder) - @Transactional
```
Input: storeId, List<MenuOrderItem(menuId, displayOrder)>
  1. 각 항목에 대해:
     a. menu = MenuRepository.findByIdAndStoreId(menuId, storeId)
        → 미존재: throw NotFoundException("Menu")
     b. menu.updateDisplayOrder(displayOrder)
  2. MenuRepository.saveAll(menus)
```

### 4.5 카테고리 생성 (MenuManageService.createCategory)
```
Input: storeId, name
  1. displayOrder = CategoryRepository.findByStoreIdOrderByDisplayOrder(storeId).size()
  2. category = Category.builder()
       .storeId(storeId)
       .name(name)
       .displayOrder(displayOrder)
       .build()
  3. CategoryRepository.save(category)
  4. return CategoryResponse.from(category, 0)
```

### 4.6 카테고리 목록 조회 (MenuManageService.getCategories)
```
Input: storeId
  1. categories = CategoryRepository.findByStoreIdOrderByDisplayOrder(storeId)
  2. 각 카테고리에 대해:
     menuCount = MenuRepository.countByStoreIdAndCategoryId(storeId, category.id)
  3. return List<CategoryResponse>
```

### 4.7 이미지 업로드 (MenuManageService.uploadImage)
```
Input: storeId, menuId, MultipartFile
  1. menu = MenuRepository.findByIdAndStoreId(menuId, storeId)
     → 미존재: throw NotFoundException("Menu")
  2. 파일 형식 검증 (JPEG, PNG, WebP)
     → 미허용: throw BusinessException("INVALID_FILE_TYPE")
  3. 파일 크기 검증 (≤ 5MB)
     → 초과: throw BusinessException("FILE_TOO_LARGE")
  4. store = StoreRepository.findById(storeId)
  5. key = "{store.code}/menus/{menuId}/{UUID}.{ext}"
  6. imageUrl = S3Service.upload(key, file)
  7. menu.updateImageUrl(imageUrl)
  8. MenuRepository.save(menu)
  9. return ImageResponse(imageUrl)
```

---

## 5. 주문 관리 흐름

### 5.1 매장 전체 주문 조회 (OrderManageService.getAllOrders)
```
Input: storeId, status (optional)
  1. if status != null:
     orders = OrderRepository.findByStoreIdAndStatusOrderByCreatedAtDesc(storeId, status)
  2. else:
     orders = OrderRepository.findByStoreIdOrderByCreatedAtDesc(storeId)
  3. 각 order에 대해:
     a. table = TableRepository.findById(order.tableId)
     b. tableNumber = table.tableNumber
  4. return List<OrderResponse> (items 포함)
```

### 5.2 주문 상태 변경 (OrderManageService.updateOrderStatus)
```
Input: storeId, orderId, newStatus
  1. order = OrderRepository.findByIdAndStoreId(orderId, storeId)
     → 미존재: throw NotFoundException("Order")
  2. order.status.canTransitionTo(newStatus) 확인
     → 불가: throw InvalidStatusTransitionException(order.status, newStatus)
  3. order.updateStatus(newStatus)
  4. OrderRepository.save(order)
  5. SSEService.publishOrderEvent(storeId, ORDER_STATUS_CHANGED event)
  6. return OrderResponse.from(order)
```

### 5.3 주문 삭제 (OrderManageService.deleteOrder)
```
Input: storeId, orderId
  1. order = OrderRepository.findByIdAndStoreId(orderId, storeId)
     → 미존재: throw NotFoundException("Order")
  2. OrderRepository.delete(order) // CASCADE로 OrderItem도 삭제
  3. SSEService.publishOrderEvent(storeId, ORDER_DELETED event)
```

### 5.4 과거 주문 내역 조회 (OrderManageService.getOrderHistory)
```
Input: storeId, tableId, startDate, endDate, page, size
  1. table = TableRepository.findById(tableId)
     → 미존재 또는 storeId 불일치: throw NotFoundException("Table")
  2. if startDate != null && endDate != null:
     history = OrderHistoryRepository
       .findByTableIdAndCompletedAtBetweenOrderByCompletedAtDesc(tableId, startDate, endDate, pageable)
  3. else:
     history = OrderHistoryRepository
       .findByTableIdOrderByCompletedAtDesc(tableId, pageable)
  4. return PageResponse.from(history.map(OrderHistoryResponse::from))
```

---

## 6. SSE 실시간 이벤트 흐름

### 6.1 SSE 구독 (SSEService.subscribe)
```
Input: storeId
  1. emitter = new SseEmitter(30분 타임아웃)
  2. storeEmitters.computeIfAbsent(storeId, k -> new CopyOnWriteArrayList<>()).add(emitter)
  3. emitter.onCompletion(() -> removeEmitter(storeId, emitter))
  4. emitter.onTimeout(() -> removeEmitter(storeId, emitter))
  5. emitter.onError(e -> removeEmitter(storeId, emitter))
  6. 초기 연결 이벤트 전송 (connected)
  7. return emitter
```

### 6.2 이벤트 발행 (SSEService.publishOrderEvent)
```
Input: storeId, OrderEventData
  1. emitters = storeEmitters.get(storeId)
  2. if emitters == null || emitters.isEmpty(): return
  3. deadEmitters = new ArrayList<>()
  4. 각 emitter에 대해:
     try:
       emitter.send(SseEmitter.event()
         .name(eventData.type)
         .data(eventData))
     catch:
       deadEmitters.add(emitter)
  5. emitters.removeAll(deadEmitters)
```

### 6.3 하트비트 (SSEService - @Scheduled)
```
15초 간격으로 실행:
  1. 모든 storeEmitters 순회
  2. 각 emitter에 comment("heartbeat") 전송
  3. 실패한 emitter 제거
```
