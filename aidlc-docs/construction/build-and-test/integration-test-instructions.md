# Integration Test Instructions - Unit 4: Customer Frontend

## Purpose
Customer Frontend(Unit 4)와 Customer Backend(Unit 2) 간의 통합을 검증합니다.

## Prerequisites
- Customer Backend(Unit 2) 실행 중 (http://localhost:8080)
- PostgreSQL 데이터베이스 실행 중
- 테스트 데이터 준비 (매장, 테이블, 메뉴)

## Setup Integration Test Environment

### 1. Start Backend Services
```bash
cd table-order-backend
./gradlew bootRun
```
> 또는 Docker Compose 사용:
```bash
docker-compose up -d
```

### 2. Prepare Test Data
백엔드 API로 테스트 데이터 생성:
```bash
# 매장 등록
curl -X POST http://localhost:8080/api/stores \
  -H "Content-Type: application/json" \
  -d '{"name": "테스트 카페", "code": "test-cafe"}'

# 관리자 등록
curl -X POST http://localhost:8080/api/admin/auth/register \
  -H "Content-Type: application/json" \
  -d '{"storeCode": "test-cafe", "username": "admin", "password": "admin1234"}'

# 테이블 생성 (관리자 토큰 필요)
# ... 관리자 로그인 후 테이블 및 메뉴 생성
```

### 3. Start Customer Frontend
```bash
cd table-order-frontend/packages/customer-app
pnpm dev
```
> http://localhost:5173 에서 접근

## Test Scenarios

### Scenario 1: 초기 설정 → 메뉴 조회 (US-C01, US-C03)
1. http://localhost:5173/setup 접속
2. QR 스캔 또는 수동 입력 (매장 코드: test-cafe)
3. 비밀번호 입력
4. 메뉴 화면 자동 이동 확인
5. 카테고리 목록 표시 확인
6. 메뉴 카드 3열 그리드 표시 확인
- **Expected**: 카테고리 전환 시 메뉴 목록 변경, 이미지 로딩

### Scenario 2: 장바구니 → 주문 생성 (US-C05~C10)
1. 메뉴 카드 탭 → 확장 카드 표시
2. "장바구니 담기" 탭 → 슬라이드 패널 열림
3. 수량 조절 (+/-) → 금액 재계산
4. "주문하기" 탭 → 주문 확인 화면
5. "주문 확정" 탭 → 주문 성공 화면
6. 5초 후 메뉴 화면 자동 리다이렉트
- **Expected**: 장바구니 비워짐, 주문 번호 표시

### Scenario 3: 주문 내역 실시간 업데이트 (US-C12)
1. 주문 내역 탭 이동
2. 주문 목록 표시 확인
3. 관리자 대시보드에서 주문 상태 변경 (PENDING → PREPARING)
4. 고객 앱에서 실시간 상태 변경 확인 (SSE)
- **Expected**: 상태 배지 색상 변경 (노란색 → 파란색)

### Scenario 4: 네트워크 오류 처리 (US-C11)
1. 백엔드 서버 중지
2. 주문 확정 시도
3. 에러 메시지 표시 확인
4. 장바구니 유지 확인
5. 백엔드 재시작 후 재시도
- **Expected**: "네트워크 연결을 확인해주세요" 메시지, 장바구니 유지

## Cleanup
```bash
docker-compose down
# 또는 백엔드 프로세스 종료
```
