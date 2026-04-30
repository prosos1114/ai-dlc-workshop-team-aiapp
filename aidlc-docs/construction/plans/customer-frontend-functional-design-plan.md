# Functional Design Plan - Unit 4: Customer Frontend

## 계획 개요
고객용 프론트엔드 앱(React + TypeScript)의 기능 설계를 수행합니다.
- **할당 스토리**: US-C01 ~ US-C12 (12개)
- **기술 스택**: React 18, TypeScript, Vite, Zustand, Axios, React Router, Tailwind CSS
- **의존성**: Unit 1 (shared 패키지), Unit 2 (Customer Backend REST API)

---

## 명확화 질문

### Question 1
고객 앱의 전체 네비게이션 구조를 어떻게 구성하시겠습니까?

A) 하단 탭 네비게이션 (메뉴 / 장바구니 / 주문내역) - 태블릿 터치에 최적화
B) 상단 탭 네비게이션 (메뉴 / 장바구니 / 주문내역) - 웹 표준 방식
C) 사이드바 네비게이션 - 태블릿 가로 모드에 적합
X) Other (please describe after [Answer]: tag below)

[Answer]: C

### Question 2
장바구니 UI 접근 방식을 어떻게 하시겠습니까?

A) 별도 페이지 - 장바구니 탭 클릭 시 전체 화면으로 이동
B) 슬라이드 패널 - 화면 오른쪽에서 슬라이드로 열림 (메뉴 화면 유지)
C) 모달 오버레이 - 메뉴 화면 위에 모달로 표시
X) Other (please describe after [Answer]: tag below)

[Answer]: B - 장바구니 슬라이드해서 보이고 창닫기 버튼, 3초동안 움직임없으면 자동 닫기.

### Question 3
메뉴 상세 정보 표시 방식을 어떻게 하시겠습니까?

A) 모달/바텀시트 - 메뉴 카드 탭 시 현재 화면 위에 오버레이로 표시
B) 별도 페이지 - 메뉴 카드 탭 시 상세 페이지로 이동
C) 확장 카드 - 메뉴 카드가 제자리에서 확장되어 상세 정보 표시
X) Other (please describe after [Answer]: tag below)

[Answer]: C

### Question 4
태블릿 화면 방향(orientation)은 어떻게 설정하시겠습니까?

A) 세로 모드(Portrait) 고정 - 일반적인 태블릿 주문 앱 방식
B) 가로 모드(Landscape) 고정 - 넓은 화면 활용
C) 반응형 - 세로/가로 모두 지원
X) Other (please describe after [Answer]: tag below)

[Answer]: B

### Question 5
메뉴 카드 레이아웃을 어떻게 구성하시겠습니까?

A) 2열 그리드 - 이미지 중심의 큰 카드 (이미지 + 메뉴명 + 가격)
B) 3열 그리드 - 컴팩트한 카드 (작은 이미지 + 메뉴명 + 가격)
C) 리스트형 - 좌측 이미지 + 우측 정보 (메뉴명 + 설명 + 가격)
X) Other (please describe after [Answer]: tag below)

[Answer]: B

### Question 6
주문 상태 표시에서 실시간 업데이트가 필요합니까?

A) 실시간 업데이트 불필요 - 주문 내역 화면 진입/새로고침 시에만 최신 상태 조회
B) 폴링 방식 - 주문 내역 화면에서 일정 간격(예: 10초)으로 자동 조회
C) SSE 방식 - 고객 앱에서도 SSE로 실시간 상태 변경 수신
X) Other (please describe after [Answer]: tag below)

[Answer]: C

### Question 7
초기 설정(태블릿 바인딩) 화면의 보안 수준을 어떻게 하시겠습니까?

A) 단순 입력 - 매장 코드, 테이블 번호, 비밀번호를 한 화면에서 입력
B) 단계별 입력 - 매장 코드 확인 → 테이블 번호 선택 → 비밀번호 입력 (단계별 검증)
C) QR 코드 - QR 스캔으로 매장/테이블 정보 자동 입력 + 비밀번호만 수동 입력
X) Other (please describe after [Answer]: tag below)

[Answer]: C

---

## 실행 계획

### Step 1: 프론트엔드 컴포넌트 구조 설계
- [x] 페이지 컴포넌트 정의 (라우팅 구조)
- [x] 공통 레이아웃 컴포넌트 정의
- [x] 컴포넌트 계층 구조 설계

### Step 2: 상태 관리 설계
- [x] Zustand 스토어 구조 정의
- [x] 로컬 스토리지 연동 전략 (장바구니, 인증 정보)
- [x] 서버 상태 관리 전략 (API 데이터)

### Step 3: 사용자 인터랙션 흐름 설계
- [x] 태블릿 초기 설정 → 자동 로그인 흐름
- [x] 메뉴 조회 → 장바구니 → 주문 흐름
- [x] 주문 내역 조회 흐름

### Step 4: 폼 검증 규칙 정의
- [x] 초기 설정 폼 검증 (Zod 스키마)
- [x] 장바구니 수량 검증
- [x] 주문 생성 전 검증

### Step 5: API 연동 포인트 정의
- [x] Backend API 엔드포인트 매핑
- [x] 요청/응답 타입 정의
- [x] 에러 처리 전략

### Step 6: 비즈니스 로직 모델 문서화
- [x] business-logic-model.md 생성
- [x] business-rules.md 생성
- [x] domain-entities.md 생성 (프론트엔드 타입)
- [x] frontend-components.md 생성

---

## 산출물
- `aidlc-docs/construction/customer-frontend/functional-design/business-logic-model.md`
- `aidlc-docs/construction/customer-frontend/functional-design/business-rules.md`
- `aidlc-docs/construction/customer-frontend/functional-design/domain-entities.md`
- `aidlc-docs/construction/customer-frontend/functional-design/frontend-components.md`
