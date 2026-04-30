# NFR Requirements - Unit 1: Common/Shared

## 1. 성능 (Performance)

### NFR-PERF-01: API 응답 시간
- **목표**: 95th percentile ≤ 500ms
- **측정**: 모든 REST API 엔드포인트
- **조건**: 동시 접속 1000명 기준

### NFR-PERF-02: SSE 이벤트 전달 시간
- **목표**: 주문 생성 → 관리자 대시보드 표시 ≤ 2초
- **측정**: 이벤트 발행 시점 ~ 클라이언트 수신 시점

### NFR-PERF-03: 데이터베이스 쿼리
- **목표**: 단일 쿼리 ≤ 100ms
- **조건**: 적절한 인덱스 설정 전제

### NFR-PERF-04: 이미지 업로드
- **목표**: 5MB 이미지 업로드 ≤ 3초
- **조건**: S3 직접 업로드 또는 Presigned URL

---

## 2. 확장성 (Scalability)

### NFR-SCALE-01: 동시 접속
- **목표**: 1000명 이상 동시 접속 지원
- **전략**: 수평 확장 가능한 Stateless 아키텍처

### NFR-SCALE-02: SSE 연결 관리
- **목표**: 매장당 최대 50개 동시 SSE 연결
- **전략**: 인메모리 Emitter 관리, 서버 인스턴스별 독립

### NFR-SCALE-03: 데이터 증가
- **목표**: 매장 1000개, 테이블 10,000개, 일일 주문 100,000건 처리
- **전략**: 적절한 인덱싱, 과거 이력 분리 저장

---

## 3. 가용성 (Availability)

### NFR-AVAIL-01: 서비스 가용성
- **목표**: 99.5% (월간 다운타임 ≤ 3.6시간)
- **전략**: AWS EC2 Multi-AZ, RDS Multi-AZ

### NFR-AVAIL-02: Graceful Degradation
- **목표**: SSE 연결 실패 시에도 REST API 정상 동작
- **전략**: SSE는 부가 기능, 핵심 주문 기능은 REST로 보장

---

## 4. 보안 (Security)

### NFR-SEC-01: 인증
- JWT 토큰 기반 인증 (HS256)
- 관리자: 16시간 만료
- 테이블: 365일 만료
- 비밀번호: bcrypt (cost factor 10)

### NFR-SEC-02: 인가
- 매장별 데이터 격리 (storeId 기반)
- 테이블별 주문 접근 제한 (tableId 기반)
- 역할 기반 접근 제어 (ADMIN, TABLE)

### NFR-SEC-03: 통신 보안
- HTTPS 필수 (TLS 1.2+)
- CORS 정책: 허용된 오리진만
- Security Headers 적용 (CSP, HSTS, X-Content-Type-Options, X-Frame-Options, Referrer-Policy)

### NFR-SEC-04: 입력 검증
- 모든 API 파라미터 검증 (Bean Validation)
- SQL Injection 방지 (JPA Parameterized Query)
- XSS 방지 (입력 이스케이프)
- 요청 본문 크기 제한 (10MB)

### NFR-SEC-05: Brute-force 방지
- 로그인 5회 실패 시 15분 잠금
- Rate Limiting: 공개 API 분당 60회

### NFR-SEC-06: Security Extension 준수
- SECURITY-01 ~ SECURITY-15 전체 적용
- 각 단계별 compliance 검증

---

## 5. 신뢰성 (Reliability)

### NFR-REL-01: 에러 처리
- 전역 예외 핸들러 (GlobalExceptionHandler)
- 구조화된 에러 응답 (code, message, timestamp)
- 프로덕션 환경에서 스택 트레이스 미노출

### NFR-REL-02: 트랜잭션 무결성
- 이용 완료 처리: 원자적 트랜잭션 (주문 이동 + 세션 종료)
- 주문 생성: 원자적 트랜잭션 (Order + OrderItems)

### NFR-REL-03: 데이터 일관성
- 주문 금액 = 항목 소계 합 (불변식)
- 테이블당 ACTIVE 세션 최대 1개 (불변식)

---

## 6. 유지보수성 (Maintainability)

### NFR-MAINT-01: 코드 품질
- 단위 테스트 커버리지 80% 이상
- 계층형 아키텍처 준수 (Controller → Service → Repository)
- 패키지별 도메인 분리

### NFR-MAINT-02: API 문서화
- OpenAPI 3.0 (Swagger) 자동 생성
- 모든 엔드포인트 문서화

### NFR-MAINT-03: 로깅
- 구조화된 로깅 (SLF4J + Logback)
- 요청 ID (Correlation ID) 포함
- 민감 정보 로깅 금지 (비밀번호, 토큰)

---

## 7. 운영성 (Operability)

### NFR-OPS-01: 헬스체크
- /actuator/health 엔드포인트
- DB 연결 상태 확인
- S3 연결 상태 확인

### NFR-OPS-02: 모니터링
- 요청 수, 응답 시간, 에러율 메트릭
- JVM 메트릭 (메모리, GC, 스레드)
- SSE 연결 수 메트릭
