# Application Design - 통합 문서

## 1. 시스템 아키텍처 개요

### 기술 스택
| 계층 | 기술 |
|---|---|
| Frontend | TypeScript + React |
| Backend | Java 17 + Spring Boot 3 |
| Database | PostgreSQL |
| File Storage | AWS S3 |
| Real-time | Server-Sent Events (SSE) |
| Auth | JWT + bcrypt |

### 아키텍처 패턴
- **Backend**: Layered Architecture (Controller → Service → Repository)
- **Frontend**: Component-based (React)
- **통신**: REST API (동기) + SSE (비동기, 서버→클라이언트)

---

## 2. 컴포넌트 구성

### Frontend
- **Customer App**: 고객 주문 인터페이스 (태블릿 최적화)
- **Admin App**: 관리자 운영 인터페이스 (데스크톱/태블릿)

### Backend Controllers (6개)
- AuthController, StoreController, TableController, MenuController, OrderController, SSEController

### Services (7개)
- AuthService, StoreService, TableService, MenuService, OrderService, SSEService, S3Service

### Repositories (9개)
- Admin, Store, Table, TableSession, Category, Menu, Order, OrderItem, OrderHistory

---

## 3. 핵심 비즈니스 흐름

### 주문 생성 → 실시간 알림
1. 고객이 주문 확정
2. OrderService가 주문 저장 + 세션 연결
3. SSEService가 해당 매장 관리자에게 이벤트 발행
4. 관리자 대시보드에 2초 이내 표시

### 테이블 이용 완료
1. 관리자가 이용 완료 클릭
2. TableSessionFacade가 세션 종료 + 주문 이력 이동 원자적 처리
3. 테이블 상태 리셋
4. SSE로 대시보드 업데이트

### 주문 상태 흐름
- 대기중(PENDING) → 준비중(PREPARING) → 완료(COMPLETED)
- 대기중→준비중: 관리자 수동 클릭
- 준비중→완료: 관리자 수동 클릭

---

## 4. 보안 설계

### 인증 방식
- **관리자**: JWT (16시간 만료)
- **테이블**: JWT (세션 기반, 장기 유효)

### 접근 제어
- 매장별 데이터 격리 (storeId 기반)
- 테이블별 주문 접근 제한 (tableId 기반)
- 관리자는 자기 매장 데이터만 접근

### 보안 헤더
- Content-Security-Policy, HSTS, X-Content-Type-Options, X-Frame-Options, Referrer-Policy

---

## 5. 상세 설계 문서 참조
- 컴포넌트 정의: `components.md`
- 메서드 시그니처: `component-methods.md`
- 서비스 오케스트레이션: `services.md`
- 의존성 관계: `component-dependency.md`
