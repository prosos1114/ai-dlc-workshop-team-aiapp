# Application Components

## 시스템 구성

### Frontend (React + TypeScript)

#### FE-01: Customer App (고객용 웹 앱)
**목적**: 테이블 태블릿에서 고객이 사용하는 주문 인터페이스
**책임**:
- 태블릿 초기 설정 및 자동 로그인
- 메뉴 조회 및 탐색
- 장바구니 관리 (로컬 저장)
- 주문 생성 및 확인
- 주문 내역 조회

#### FE-02: Admin App (관리자용 웹 앱)
**목적**: 매장 관리자가 사용하는 운영 관리 인터페이스
**책임**:
- 관리자 로그인/회원가입
- 매장 등록
- 실시간 주문 모니터링 대시보드 (SSE)
- 주문 상태 관리
- 테이블 관리
- 메뉴 CRUD
- 과거 주문 내역 조회

---

### Backend (Java + Spring Boot)

#### BE-01: AuthController
**목적**: 인증 관련 API 엔드포인트
**책임**:
- 관리자 로그인/로그아웃
- 관리자 회원가입
- 테이블 태블릿 인증
- JWT 토큰 발급/검증

#### BE-02: StoreController
**목적**: 매장 관리 API 엔드포인트
**책임**:
- 매장 등록
- 매장 정보 조회

#### BE-03: TableController
**목적**: 테이블 관리 API 엔드포인트
**책임**:
- 테이블 생성/수정
- 테이블 목록 조회
- 테이블 세션 관리 (이용 완료)

#### BE-04: MenuController
**목적**: 메뉴 관리 API 엔드포인트
**책임**:
- 메뉴 CRUD
- 카테고리 관리
- 메뉴 순서 조정
- 이미지 업로드 (S3)

#### BE-05: OrderController
**목적**: 주문 관리 API 엔드포인트
**책임**:
- 주문 생성
- 주문 조회 (현재 세션)
- 주문 상태 변경
- 주문 삭제
- 과거 주문 내역 조회

#### BE-06: SSEController
**목적**: 실시간 이벤트 스트리밍 엔드포인트
**책임**:
- SSE 연결 관리
- 신규 주문 이벤트 발행
- 주문 상태 변경 이벤트 발행

---

### Service Layer

#### SVC-01: AuthService
**목적**: 인증/인가 비즈니스 로직
**책임**:
- 사용자 인증 처리
- JWT 토큰 생성/검증
- 비밀번호 해싱/검증 (bcrypt)
- 로그인 시도 제한

#### SVC-02: StoreService
**목적**: 매장 비즈니스 로직
**책임**:
- 매장 등록 처리
- 매장 식별자 유효성 검증

#### SVC-03: TableService
**목적**: 테이블 비즈니스 로직
**책임**:
- 테이블 CRUD
- 테이블 세션 관리
- 이용 완료 처리 (주문 이력 이동)

#### SVC-04: MenuService
**목적**: 메뉴 비즈니스 로직
**책임**:
- 메뉴 CRUD
- 카테고리 관리
- 메뉴 순서 관리
- 이미지 업로드 처리

#### SVC-05: OrderService
**목적**: 주문 비즈니스 로직
**책임**:
- 주문 생성 및 검증
- 주문 상태 변경
- 주문 삭제
- 세션별 주문 조회
- 과거 주문 이력 관리

#### SVC-06: SSEService
**목적**: 실시간 이벤트 관리
**책임**:
- SSE 연결 관리 (매장별)
- 이벤트 발행 (주문 생성, 상태 변경)

#### SVC-07: S3Service
**목적**: 파일 업로드 관리
**책임**:
- S3 이미지 업로드
- 이미지 URL 생성

---

### Repository Layer

#### REPO-01: AdminRepository
#### REPO-02: StoreRepository
#### REPO-03: TableRepository
#### REPO-04: TableSessionRepository
#### REPO-05: CategoryRepository
#### REPO-06: MenuRepository
#### REPO-07: OrderRepository
#### REPO-08: OrderItemRepository
#### REPO-09: OrderHistoryRepository

---

### Infrastructure / Cross-cutting

#### INFRA-01: SecurityConfig
**목적**: Spring Security 설정
**책임**: JWT 필터, CORS, 보안 헤더, 엔드포인트 접근 제어

#### INFRA-02: GlobalExceptionHandler
**목적**: 전역 예외 처리
**책임**: 일관된 에러 응답, 로깅

#### INFRA-03: JwtTokenProvider
**목적**: JWT 토큰 유틸리티
**책임**: 토큰 생성, 파싱, 검증
