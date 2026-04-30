# NFR Requirements Plan - Unit 4: Customer Frontend

## 계획 개요
고객용 프론트엔드 앱의 비기능 요구사항을 정의하고 기술 스택을 확정합니다.
- **기반**: Unit 1 Common/Shared NFR Requirements 참조
- **초점**: 프론트엔드 성능, 보안, 접근성, 사용성

---

## 명확화 질문

### Question 1
고객 앱의 초기 로딩 성능 목표를 어떻게 설정하시겠습니까?

A) 빠른 로딩 우선 - First Contentful Paint(FCP) 1.5초 이내, 번들 최적화 적극 적용
B) 기능 우선 - 로딩 성능은 3초 이내면 충분, 개발 속도 우선
C) 균형 - FCP 2초 이내, 코드 스플리팅 기본 적용
X) Other (please describe after [Answer]: tag below)

[Answer]: A

### Question 2
오프라인/네트워크 불안정 상황에 대한 대응 수준을 어떻게 하시겠습니까?

A) 기본 대응 - 네트워크 오류 시 에러 메시지 + 재시도 버튼만 제공
B) 부분 오프라인 - 장바구니는 오프라인 동작, API 호출은 네트워크 필요
C) 완전 오프라인 - Service Worker로 메뉴 캐싱, 오프라인에서도 메뉴 조회 가능
X) Other (please describe after [Answer]: tag below)

[Answer]: B - API 호출 시 네트워크 오류는 A 에러메세지 + 재시도 버튼 제공

### Question 3
이미지 최적화 전략을 어떻게 하시겠습니까?

A) 기본 - img 태그로 원본 이미지 로드, lazy loading만 적용
B) 중간 - lazy loading + 플레이스홀더 + 이미지 에러 처리
C) 적극 - lazy loading + WebP 포맷 + 반응형 이미지(srcset) + 플레이스홀더
X) Other (please describe after [Answer]: tag below)

[Answer]: C

### Question 4
테스트 전략을 어떻게 하시겠습니까?

A) 단위 테스트 중심 - 커스텀 훅, 유틸리티, 스토어 로직 테스트 (커버리지 80%+)
B) 컴포넌트 테스트 포함 - 단위 테스트 + 주요 컴포넌트 렌더링/인터랙션 테스트
C) E2E 포함 - 단위 + 컴포넌트 + Playwright/Cypress E2E 테스트
X) Other (please describe after [Answer]: tag below)

[Answer]: A

### Question 5
접근성(Accessibility) 준수 수준을 어떻게 하시겠습니까?

A) 기본 - 시맨틱 HTML, alt 텍스트, 충분한 색상 대비 (WCAG 2.1 A 수준)
B) 중간 - WCAG 2.1 AA 수준 + 키보드 네비게이션 + 스크린 리더 지원
C) 높음 - WCAG 2.1 AAA 수준 + 자동화된 접근성 테스트
X) Other (please describe after [Answer]: tag below)

[Answer]: A

---

## 실행 계획

### Step 1: 성능 요구사항 정의
- [x] 초기 로딩 성능 목표 설정
- [x] 런타임 성능 목표 설정 (인터랙션 응답 시간)
- [x] 번들 크기 목표 설정

### Step 2: 보안 요구사항 정의
- [x] 프론트엔드 보안 패턴 정의 (XSS, CSRF 방지)
- [x] 인증 토큰 관리 전략
- [x] 민감 데이터 처리 규칙

### Step 3: 사용성/접근성 요구사항 정의
- [x] 접근성 준수 수준 확정
- [x] 터치 인터페이스 요구사항
- [x] 에러 처리 UX 요구사항

### Step 4: 신뢰성 요구사항 정의
- [x] 오프라인/네트워크 대응 전략
- [x] SSE 연결 안정성 요구사항
- [x] 데이터 동기화 전략

### Step 5: 기술 스택 확정
- [x] 프론트엔드 기술 스택 최종 확정
- [x] 추가 라이브러리 결정 (QR 스캐너 등)
- [x] 테스트 도구 확정

### Step 6: 산출물 생성
- [x] nfr-requirements.md 생성
- [x] tech-stack-decisions.md 생성

---

## 산출물
- `aidlc-docs/construction/customer-frontend/nfr-requirements/nfr-requirements.md`
- `aidlc-docs/construction/customer-frontend/nfr-requirements/tech-stack-decisions.md`
