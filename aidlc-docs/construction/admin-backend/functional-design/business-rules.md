# Business Rules - Unit 3: Admin Backend

## 개요
Unit 1의 공통 비즈니스 규칙(BR-01~BR-16)을 기반으로, Admin Backend API 레이어에서 적용하는 구체적인 규칙을 정의합니다.

---

## 인증 규칙 (Auth)

### ABR-01: 관리자 로그인 검증
- 매장 코드(storeCode)로 매장 존재 여부 확인 → 미존재 시 INVALID_CREDENTIALS
- 매장 ID + username으로 관리자 조회 → 미존재 시 INVALID_CREDENTIALS
- 계정 잠금 상태 확인 → 잠금 시 ACCOUNT_LOCKED (BR-03 참조)
- 비밀번호 bcrypt 검증 → 불일치 시 loginAttempts 증가, INVALID_CREDENTIALS
- 5회 실패 시 15분 잠금 (BR-03)
- 성공 시 loginAttempts 초기화, JWT 토큰 발급 (BR-04)
- 보안: 매장/사용자/비밀번호 중 어느 것이 틀렸는지 구분하지 않음 (동일 에러 메시지)

### ABR-02: 관리자 회원가입 검증
- 매장 코드로 매장 존재 여부 확인 → 미존재 시 STORE_NOT_FOUND
- 동일 매장 내 username 중복 확인 → 중복 시 DUPLICATE_ADMIN
- 비밀번호 정책 검증 (BR-02): 최소 8자, 영문+숫자 조합
- 비밀번호 bcrypt 해싱 후 저장
- 회원가입 성공 시 AdminResponse 반환 (토큰 미발급, 별도 로그인 필요)

### ABR-03: 관리자 로그아웃
- 서버 측 처리 없음 (Stateless JWT)
- 클라이언트에서 토큰 삭제로 처리
- 별도 API 엔드포인트 불필요

---

## 매장 규칙 (Store)

### ABR-04: 매장 등록
- 매장 코드 형식 검증 (BR-01): 영문소문자+숫자+하이픈, 3~50자
- 매장 코드 유일성 확인 → 중복 시 DUPLICATE_STORE
- 매장명 필수, 1~100자
- 인증 불필요 (공개 API) - 매장 등록 후 관리자 회원가입 진행

### ABR-05: 매장 조회
- storeCode로 매장 조회
- 미존재 시 STORE_NOT_FOUND
- 인증 불필요 (공개 API) - 로그인 전 매장 확인용

---

## 테이블 규칙 (Table)

### ABR-06: 테이블 생성
- 인증 필수 (ADMIN 역할)
- storeId는 토큰의 storeId와 일치해야 함 (StoreAccessInterceptor)
- 동일 매장 내 tableNumber 중복 확인 → 중복 시 DUPLICATE_TABLE
- 비밀번호 bcrypt 해싱 후 저장 (BR-05)
- tableNumber는 1 이상 양의 정수

### ABR-07: 테이블 목록 조회
- 인증 필수 (ADMIN 역할)
- 해당 매장의 모든 테이블 반환
- 각 테이블의 현재 세션 상태 포함 (ACTIVE 세션 유무, 주문 수, 총액)

### ABR-08: 테이블 수정
- 인증 필수 (ADMIN 역할)
- 비밀번호 변경만 허용
- 새 비밀번호 bcrypt 해싱 후 저장

### ABR-09: 이용 완료 처리
- 인증 필수 (ADMIN 역할)
- ACTIVE 세션이 있는 테이블만 이용 완료 가능 → 없으면 NO_ACTIVE_SESSION
- 처리 순서 (BR-11, 원자적 트랜잭션):
  1. 해당 세션의 모든 주문을 OrderHistory로 이동 (항목 JSON 직렬화)
  2. 원본 Order, OrderItem 삭제
  3. 세션 상태를 COMPLETED로 변경, completedAt 기록
- SSE 이벤트 발행: TABLE_COMPLETED

---

## 메뉴 규칙 (Menu)

### ABR-10: 메뉴 등록
- 인증 필수 (ADMIN 역할)
- categoryId 유효성 확인 → 미존재 시 CATEGORY_NOT_FOUND
- 카테고리가 해당 매장 소속인지 확인
- 메뉴명 필수, 1~100자
- 가격 검증 (BR-13): 0~10,000,000 정수
- displayOrder: 해당 카테고리 내 기존 메뉴 수 + 1 (맨 뒤 추가)

### ABR-11: 메뉴 조회 (관리자)
- 인증 필수 (ADMIN 역할)
- 해당 매장의 전체 메뉴 반환 (카테고리별 정렬)
- categoryId 필터 선택적 적용

### ABR-12: 메뉴 수정
- 인증 필수 (ADMIN 역할)
- 메뉴가 해당 매장 소속인지 확인 → 미소속 시 MENU_NOT_FOUND
- 카테고리 변경 시 새 카테고리 유효성 확인
- 가격 검증 (BR-13)

### ABR-13: 메뉴 삭제
- 인증 필수 (ADMIN 역할)
- 메뉴가 해당 매장 소속인지 확인
- 삭제 시 기존 주문의 OrderItem은 유지 (BR-10 스냅샷)
- 물리적 삭제 (soft delete 미적용)

### ABR-14: 메뉴 순서 변경
- 인증 필수 (ADMIN 역할)
- 요청의 모든 menuId가 해당 매장 소속인지 확인
- 일괄 displayOrder 업데이트 (트랜잭션)

### ABR-15: 카테고리 생성
- 인증 필수 (ADMIN 역할)
- 카테고리명 필수, 1~50자
- displayOrder: 해당 매장 내 기존 카테고리 수 + 1

### ABR-16: 이미지 업로드
- 인증 필수 (ADMIN 역할)
- 메뉴가 해당 매장 소속인지 확인
- 파일 형식 검증 (BR-14): JPEG, PNG, WebP만 허용
- 파일 크기 검증: 최대 5MB
- S3 업로드 경로: `{storeCode}/menus/{menuId}/{UUID}.{ext}`
- 업로드 성공 시 메뉴의 imageUrl 업데이트

---

## 주문 규칙 (Order)

### ABR-17: 매장 전체 주문 조회
- 인증 필수 (ADMIN 역할)
- 해당 매장의 모든 활성 주문 반환 (최신순)
- status 필터 선택적 적용 (PENDING, PREPARING, COMPLETED)
- 각 주문에 테이블 번호, 주문 항목 포함

### ABR-18: 주문 상태 변경
- 인증 필수 (ADMIN 역할)
- 주문이 해당 매장 소속인지 확인 → 미소속 시 ORDER_NOT_FOUND
- 상태 전이 규칙 검증 (BR-08):
  - PENDING → PREPARING (허용)
  - PREPARING → COMPLETED (허용)
  - 그 외 전이 → INVALID_STATUS_TRANSITION
- SSE 이벤트 발행: ORDER_STATUS_CHANGED

### ABR-19: 주문 삭제
- 인증 필수 (ADMIN 역할)
- 주문이 해당 매장 소속인지 확인
- 모든 상태에서 삭제 가능 (관리자 직권, BR-08)
- 주문 + 주문 항목 함께 삭제 (CASCADE)
- SSE 이벤트 발행: ORDER_DELETED

### ABR-20: 과거 주문 내역 조회
- 인증 필수 (ADMIN 역할)
- 특정 테이블의 과거 주문 이력 조회
- 날짜 범위 필터 선택적 적용 (startDate, endDate)
- 페이지네이션 적용 (PageResponse)
- completedAt 기준 최신순 정렬

---

## SSE 규칙

### ABR-21: SSE 구독
- 인증 필수 (ADMIN 역할)
- 매장별 SSE 스트림 구독
- 타임아웃: 30분 (BR-16)
- 하트비트: 15초 간격
- 연결 해제 시 자동 정리

### ABR-22: SSE 이벤트 발행
- 주문 생성 시: ORDER_CREATED (Customer API에서 발행, Admin이 수신)
- 주문 상태 변경 시: ORDER_STATUS_CHANGED
- 주문 삭제 시: ORDER_DELETED
- 이용 완료 시: TABLE_COMPLETED
- 해당 매장의 모든 구독자에게 브로드캐스트
