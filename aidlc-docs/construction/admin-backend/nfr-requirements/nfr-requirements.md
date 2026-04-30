# NFR Requirements - Unit 3: Admin Backend

## 개요
Unit 1 (Common/Shared)의 NFR 요구사항을 기반으로, Admin Backend API 모듈에 특화된 비기능 요구사항을 정의합니다. Unit 1에서 정의된 공통 NFR(NFR-PERF, NFR-SCALE, NFR-AVAIL, NFR-SEC, NFR-REL, NFR-MAINT, NFR-OPS)은 모두 적용되며, 이 문서는 Admin Backend 고유의 보충 요구사항을 다룹니다.

---

## 1. 성능 (Performance)

### ANFR-PERF-01: 관리자 API 응답 시간
- **목표**: 95th percentile ≤ 500ms (Unit 1 NFR-PERF-01 준수)
- **적용 엔드포인트**: 모든 Admin REST API
- **특이사항**: 주문 목록 조회(getAllOrders)는 매장 규모에 따라 데이터량 증가 가능 → 적절한 인덱스 필수

### ANFR-PERF-02: SSE 이벤트 전달 지연
- **목표**: 이벤트 발생 → 관리자 대시보드 수신 ≤ 2초 (Unit 1 NFR-PERF-02 준수)
- **측정**: OrderService에서 SSEService.publish 호출 시점 ~ 클라이언트 수신 시점
- **조건**: 매장당 최대 50개 동시 SSE 연결

### ANFR-PERF-03: 이미지 업로드 응답 시간
- **목표**: 5MB 이미지 업로드 ≤ 3초 (Unit 1 NFR-PERF-04 준수)
- **전략**: S3 직접 업로드 (서버 사이드)
- **제한**: 최대 파일 크기 5MB, 요청 본문 10MB

### ANFR-PERF-04: 이용 완료 처리 시간
- **목표**: 이용 완료 트랜잭션 ≤ 1초
- **조건**: 세션당 최대 50건 주문 기준
- **전략**: 배치 INSERT (OrderHistory) + 배치 DELETE (Order) 최적화

### ANFR-PERF-05: 과거 주문 내역 조회
- **목표**: 페이지네이션 적용, 페이지당 20건 기준 ≤ 300ms
- **전략**: order_history 테이블의 (table_id, completed_at) 인덱스 활용

---

## 2. 확장성 (Scalability)

### ANFR-SCALE-01: SSE 연결 확장
- **현재**: 인메모리 ConcurrentHashMap 기반 (단일 인스턴스)
- **제한**: 수평 확장 시 SSE 이벤트가 다른 인스턴스의 구독자에게 전달되지 않음
- **향후**: Redis Pub/Sub 도입으로 멀티 인스턴스 지원 (MVP 범위 외)
- **MVP 목표**: 단일 인스턴스에서 매장당 50개 SSE 연결 안정 지원

### ANFR-SCALE-02: 주문 데이터 증가 대응
- **전략**: 이용 완료 시 OrderHistory로 이동하여 활성 주문 테이블 크기 제한
- **활성 주문**: orders 테이블 (현재 세션 주문만)
- **과거 이력**: order_history 테이블 (페이지네이션 필수)

---

## 3. 가용성 (Availability)

### ANFR-AVAIL-01: SSE 연결 복원
- **목표**: SSE 연결 끊김 시 클라이언트 자동 재연결
- **전략**: EventSource API의 자동 재연결 활용 + 재연결 후 REST API로 최신 상태 폴링
- **타임아웃**: 30분 (서버 측 SseEmitter timeout)
- **하트비트**: 15초 간격 (연결 유지 확인)

### ANFR-AVAIL-02: S3 업로드 실패 대응
- **전략**: S3 업로드 실패 시 명확한 에러 메시지 반환
- **재시도**: 클라이언트 측 재시도 (서버 측 자동 재시도 미적용)
- **Graceful Degradation**: 이미지 없이도 메뉴 등록/수정 가능

---

## 4. 보안 (Security)

### ANFR-SEC-01: 관리자 인증 강화
- **인증 방식**: JWT Bearer Token (Unit 1 NFR-SEC-01 준수)
- **토큰 만료**: 16시간 (관리자용)
- **Brute-force 방지**: 5회 실패 시 15분 잠금 (Unit 1 NFR-SEC-05)
- **비밀번호 정책**: 최소 8자, 영문+숫자 조합, bcrypt(10)

### ANFR-SEC-02: 관리자 권한 검증
- **매장 격리**: StoreAccessInterceptor로 모든 /api/stores/{storeId}/** 요청에서 storeId 검증
- **역할 검증**: ADMIN 역할만 관리자 API 접근 가능
- **IDOR 방지**: 모든 리소스 접근 시 storeId 소속 확인

### ANFR-SEC-03: 파일 업로드 보안
- **파일 형식 제한**: JPEG, PNG, WebP만 허용 (Content-Type + 확장자 검증)
- **파일 크기 제한**: 최대 5MB
- **파일명 처리**: 원본 파일명 미사용, UUID 기반 키 생성
- **S3 접근**: 서버 측 IAM 역할 기반 (클라이언트 직접 접근 불가)

### ANFR-SEC-04: SSE 보안
- **인증 필수**: SSE 구독 시 JWT 토큰 검증
- **매장 격리**: 해당 매장의 이벤트만 수신
- **데이터 최소화**: SSE 이벤트에 민감 정보 미포함

---

## 5. 신뢰성 (Reliability)

### ANFR-REL-01: 이용 완료 트랜잭션 무결성
- **원자성**: 주문 이력 이동 + 세션 종료가 하나의 트랜잭션으로 처리
- **실패 시**: 전체 롤백 (주문 데이터 유실 방지)
- **동시성**: 동일 테이블에 대한 동시 이용 완료 요청 방지 (DB 레벨 잠금)

### ANFR-REL-02: SSE 이벤트 전달 보장
- **Best-effort**: SSE는 전달 보장 없음 (연결 끊김 시 이벤트 유실 가능)
- **보완**: 클라이언트 재연결 시 REST API로 최신 상태 동기화
- **Dead emitter 정리**: 전송 실패 시 즉시 emitter 제거

### ANFR-REL-03: 주문 상태 전이 무결성
- **검증**: 서비스 레이어에서 상태 전이 규칙 검증 (OrderStatus.canTransitionTo)
- **동시성**: 동일 주문에 대한 동시 상태 변경 시 낙관적 잠금 또는 순차 처리

---

## 6. 유지보수성 (Maintainability)

### ANFR-MAINT-01: 단위 테스트
- **커버리지 목표**: 80% 이상 (Unit 1 NFR-MAINT-01 준수)
- **테스트 범위**:
  - Service 레이어: 모든 비즈니스 로직 (Mockito 기반)
  - Controller 레이어: API 엔드포인트 (MockMvc 기반)
  - SSE: 이벤트 발행/구독 테스트
- **테스트 DB**: H2 (단위 테스트), Testcontainers PostgreSQL (통합 테스트)

### ANFR-MAINT-02: API 문서화
- **도구**: SpringDoc OpenAPI 3.0 (Swagger UI)
- **범위**: 모든 Admin API 엔드포인트
- **내용**: 요청/응답 스키마, 에러 코드, 인증 요구사항

---

## 7. 운영성 (Operability)

### ANFR-OPS-01: 로깅
- **구조화된 로깅**: SLF4J + Logback (Unit 1 NFR-MAINT-03 준수)
- **Correlation ID**: RequestLoggingFilter에서 자동 설정
- **주요 로그 이벤트**:
  - INFO: 관리자 로그인 성공/실패, 매장 등록, 주문 상태 변경, 이용 완료
  - WARN: 인증 실패, 계정 잠금, 잘못된 상태 전이 시도
  - ERROR: S3 업로드 실패, DB 연결 오류, 예상치 못한 예외

### ANFR-OPS-02: SSE 모니터링
- **메트릭**: 매장별 활성 SSE 연결 수, 이벤트 발행 수, 전송 실패 수
- **알림**: SSE 연결 수 급감 시 (서버 문제 가능성)
