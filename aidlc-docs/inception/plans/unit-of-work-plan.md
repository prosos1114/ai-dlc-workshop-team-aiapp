# Unit of Work Plan - 테이블오더 서비스

## 계획 개요
테이블오더 서비스를 개발 가능한 유닛으로 분해합니다.

---

## 명확화 질문

### Question 1
프론트엔드 프로젝트 구조는 어떻게 하시겠습니까?

A) 모노레포 - 고객앱과 관리자앱을 하나의 React 프로젝트에서 라우팅으로 분리
B) 멀티레포 - 고객앱과 관리자앱을 별도 React 프로젝트로 분리
C) 모노레포 + 공유 라이브러리 - 하나의 워크스페이스에 customer-app, admin-app, shared 패키지
X) Other (please describe after [Answer]: tag below)

[Answer]: C

### Question 2
백엔드와 프론트엔드의 개발 순서는 어떻게 하시겠습니까?

A) Backend First - 백엔드 API 완성 후 프론트엔드 개발
B) Frontend First - 프론트엔드 UI 완성 후 백엔드 연동
C) Parallel - 백엔드와 프론트엔드 동시 개발 (API 스펙 먼저 합의)
X) Other (please describe after [Answer]: tag below)

[Answer]: C

### Question 3
백엔드 프로젝트 구조는 어떻게 하시겠습니까?

A) 단일 Spring Boot 애플리케이션 (패키지로 도메인 분리)
B) 멀티 모듈 Gradle 프로젝트 (core, api, domain 등 모듈 분리)
C) 마이크로서비스 (도메인별 독립 서비스)
X) Other (please describe after [Answer]: tag below)

[Answer]: B

---

## 실행 계획

### Step 1: 유닛 정의
- [x] 시스템을 개발 유닛으로 분해
- [x] 각 유닛의 책임과 범위 정의
- [x] 코드 조직 전략 문서화

### Step 2: 의존성 매트릭스
- [x] 유닛 간 의존성 정의
- [x] 개발 순서 결정
- [x] 통합 포인트 식별

### Step 3: 스토리 매핑
- [x] 각 User Story를 유닛에 할당
- [x] 모든 스토리가 유닛에 배정되었는지 검증

---

## 산출물
- `aidlc-docs/inception/application-design/unit-of-work.md`
- `aidlc-docs/inception/application-design/unit-of-work-dependency.md`
- `aidlc-docs/inception/application-design/unit-of-work-story-map.md`
