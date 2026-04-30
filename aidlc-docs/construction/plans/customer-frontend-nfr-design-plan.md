# NFR Design Plan - Unit 4: Customer Frontend

## 계획 개요
고객용 프론트엔드 앱의 NFR 요구사항을 설계 패턴과 논리적 컴포넌트로 구체화합니다.
- **기반**: Unit 4 NFR Requirements, Unit 1 NFR Design Patterns 참조
- **초점**: 프론트엔드 성능 패턴, 보안 패턴, 에러 처리 패턴, SSE 연결 관리

---

## 명확화 질문

### Question 1
API 호출 에러 시 재시도 전략을 어떻게 하시겠습니까?

A) 수동 재시도만 - 사용자가 "다시 시도" 버튼을 탭해야 재시도
B) 자동 재시도 + 수동 - 네트워크 오류 시 자동 1~2회 재시도 후 실패하면 수동 재시도 버튼 표시
C) 지수 백오프 - 자동 재시도 (1초, 2초, 4초 간격) 최대 3회 후 수동 재시도
X) Other (please describe after [Answer]: tag below)

[Answer]: B

### Question 2
Zustand 스토어의 localStorage 동기화(persist) 전략을 어떻게 하시겠습니까?

A) 장바구니 + 인증 정보만 persist - 메뉴/주문 데이터는 매번 API 조회
B) 장바구니 + 인증 + 메뉴 캐시 persist - 메뉴 데이터도 로컬 캐싱하여 빠른 로딩
C) 장바구니 + 인증만 persist, 메뉴는 세션 스토리지 캐시 - 탭 닫으면 메뉴 캐시 초기화
X) Other (please describe after [Answer]: tag below)

[Answer]: A

### Question 3
Axios 인터셉터의 에러 처리 범위를 어떻게 하시겠습니까?

A) 글로벌 인터셉터 - 모든 에러를 인터셉터에서 처리 (401 자동 재인증, 토스트 자동 표시)
B) 혼합 - 401/403은 인터셉터에서 자동 처리, 나머지는 각 호출부에서 개별 처리
C) 최소 인터셉터 - 토큰 첨부만 인터셉터, 에러는 모두 각 호출부에서 처리
X) Other (please describe after [Answer]: tag below)

[Answer]: A

---

## 실행 계획

### Step 1: 성능 설계 패턴 정의
- [x] 코드 스플리팅 전략 상세화
- [x] 이미지 최적화 패턴
- [x] 렌더링 최적화 패턴

### Step 2: 보안 설계 패턴 정의
- [x] 인증 흐름 패턴 (토큰 관리, 자동 재인증)
- [x] 입력 검증 패턴 (Zod 스키마)
- [x] XSS 방지 패턴

### Step 3: 에러 처리 및 신뢰성 패턴 정의
- [x] API 에러 처리 패턴 (Axios 인터셉터)
- [x] Error Boundary 패턴
- [x] SSE 연결 관리 패턴

### Step 4: 상태 관리 패턴 정의
- [x] Zustand 스토어 구조 패턴
- [x] localStorage 동기화 패턴
- [x] 서버 상태 캐싱 패턴

### Step 5: 산출물 생성
- [x] nfr-design-patterns.md 생성
- [x] logical-components.md 생성

---

## 산출물
- `aidlc-docs/construction/customer-frontend/nfr-design/nfr-design-patterns.md`
- `aidlc-docs/construction/customer-frontend/nfr-design/logical-components.md`
