# Service Layer Design

## 서비스 오케스트레이션 패턴

### 계층 구조
```
Controller → Service → Repository
                    → External Service (S3, SSE)
```

### 서비스 간 상호작용

#### 주문 생성 흐름
```
OrderController.createOrder()
  → OrderService.createOrder()
    → TableService.getOrCreateSession()  // 세션 확인/생성
    → OrderRepository.save()             // 주문 저장
    → SSEService.publishOrderEvent()     // 실시간 알림
```

#### 이용 완료 흐름
```
TableController.completeTable()
  → TableService.completeTableSession()
    → OrderService.moveOrdersToHistory()  // 주문 이력 이동
    → TableSessionRepository.close()      // 세션 종료
    → SSEService.publishOrderEvent()      // 대시보드 업데이트
```

#### 주문 상태 변경 흐름
```
OrderController.updateOrderStatus()
  → OrderService.updateOrderStatus()
    → OrderRepository.save()              // 상태 업데이트
    → SSEService.publishOrderEvent()      // 실시간 알림
```

#### 관리자 인증 흐름
```
AuthController.login()
  → AuthService.authenticateAdmin()
    → AuthService.checkLoginAttempts()    // 시도 제한 확인
    → AdminRepository.findByUsernameAndStoreCode()
    → PasswordEncoder.matches()           // 비밀번호 검증
    → JwtTokenProvider.createToken()      // 토큰 발급
```

#### 메뉴 이미지 업로드 흐름
```
MenuController.uploadImage()
  → MenuService.uploadImage()
    → S3Service.upload()                  // S3 업로드
    → MenuRepository.updateImageUrl()     // URL 저장
```

---

## 서비스 정의

### AuthService
- **역할**: 인증/인가 처리의 단일 진입점
- **의존성**: AdminRepository, StoreRepository, JwtTokenProvider, PasswordEncoder
- **트랜잭션**: 회원가입 시 트랜잭션 필요

### StoreService
- **역할**: 매장 생명주기 관리
- **의존성**: StoreRepository
- **트랜잭션**: 매장 생성 시 트랜잭션 필요

### TableService
- **역할**: 테이블 및 세션 생명주기 관리
- **의존성**: TableRepository, TableSessionRepository, OrderService
- **트랜잭션**: 이용 완료 처리 시 트랜잭션 필요 (세션 종료 + 이력 이동 원자적 처리)

### MenuService
- **역할**: 메뉴 CRUD 및 이미지 관리
- **의존성**: MenuRepository, CategoryRepository, S3Service
- **트랜잭션**: 메뉴 순서 변경 시 트랜잭션 필요

### OrderService
- **역할**: 주문 생명주기 관리
- **의존성**: OrderRepository, OrderItemRepository, OrderHistoryRepository, TableService, SSEService
- **트랜잭션**: 주문 생성, 삭제, 이력 이동 시 트랜잭션 필요

### SSEService
- **역할**: 실시간 이벤트 스트리밍 관리
- **의존성**: 없음 (인메모리 Emitter 관리)
- **트랜잭션**: 불필요 (비동기 이벤트)

### S3Service
- **역할**: AWS S3 파일 업로드
- **의존성**: AWS S3 Client
- **트랜잭션**: 불필요 (외부 서비스)
