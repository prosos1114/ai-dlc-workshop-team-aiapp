# Unit of Work - 의존성 매트릭스

## 유닛 목록
| Unit ID | 유닛명 | 기술 스택 | 범위 |
|---|---|---|---|
| U1 | Common/Shared | Java + React + TypeScript | 공통 기반 |
| U2 | Customer Backend | Java + Spring Boot | 고객용 API |
| U3 | Admin Backend | Java + Spring Boot | 관리자용 API |
| U4 | Customer Frontend | React + TypeScript | 고객용 UI |
| U5 | Admin Frontend | React + TypeScript | 관리자용 UI |

---

## 유닛 간 의존성

```
                    +-------------------+
                    |  U1: Common/      |
                    |  Shared           |
                    +-------------------+
                   /    |        |       \
                  v     v        v        v
+----------+  +----------+  +----------+  +----------+
| U2:      |  | U3:      |  | U4:      |  | U5:      |
| Customer |  | Admin    |  | Customer |  | Admin    |
| Backend  |  | Backend  |  | Frontend |  | Frontend |
+----------+  +----------+  +----------+  +----------+
      |              |             |              |
      |   REST API   |             |    SSE       |
      +--------------+-------------+--------------+
```

### 의존성 상세
| 소스 유닛 | 대상 유닛 | 의존 유형 | 설명 |
|---|---|---|---|
| U2 (Customer BE) | U1 (Common) | 빌드 의존성 | module-core, module-domain 사용 |
| U3 (Admin BE) | U1 (Common) | 빌드 의존성 | module-core, module-domain 사용 |
| U4 (Customer FE) | U1 (Common) | 패키지 의존성 | @table-order/shared 사용 |
| U5 (Admin FE) | U1 (Common) | 패키지 의존성 | @table-order/shared 사용 |
| U4 (Customer FE) | U2 (Customer BE) | 런타임 의존성 | REST API 호출 |
| U5 (Admin FE) | U3 (Admin BE) | 런타임 의존성 | REST API + SSE |

### 유닛 간 독립성
| 유닛 쌍 | 독립성 | 비고 |
|---|---|---|
| U2 ↔ U3 | 독립 | 같은 DB 공유하지만 코드 의존성 없음 |
| U4 ↔ U5 | 독립 | shared만 공유, 직접 의존 없음 |
| U2 ↔ U5 | 독립 | 직접 통신 없음 |
| U3 ↔ U4 | 독립 | 직접 통신 없음 |

---

## 개발 순서 및 병렬화

### 개발 타임라인
```
Phase 1:  [====== U1: Common/Shared ======]
Phase 2:  ............[== U2: Customer BE ==][== U4: Customer FE ==]
Phase 3:  ............[== U3: Admin BE ====][== U5: Admin FE ====]
Phase 4:  ..........................................[= Integration =]
```

### 병렬화 가능 범위
| Phase | 병렬 가능 유닛 | 선행 조건 |
|---|---|---|
| Phase 1 | U1 단독 | 없음 |
| Phase 2 | U2 + U4 동시 | U1 완료 |
| Phase 3 | U3 + U5 동시 | U1 완료 |
| Phase 2+3 | U2+U4와 U3+U5 동시 가능 | U1 완료 |

### 통합 포인트
| 통합 포인트 | 관련 유닛 | 통신 방식 | 테스트 시점 |
|---|---|---|---|
| 고객 주문 API | U2 + U4 | REST | Phase 2 완료 시 |
| 관리자 관리 API | U3 + U5 | REST | Phase 3 완료 시 |
| 실시간 주문 알림 | U3 + U5 | SSE | Phase 3 완료 시 |
| 주문→알림 연동 | U2 + U3 | DB 이벤트 | Phase 4 |

---

## Gradle 모듈 의존성 (Backend)

```
module-app
  ├── depends on: module-customer-api
  └── depends on: module-admin-api

module-customer-api
  ├── depends on: module-domain
  └── depends on: module-core

module-admin-api
  ├── depends on: module-domain
  └── depends on: module-core

module-domain
  └── depends on: module-core

module-core
  └── depends on: (외부 라이브러리만)
```

### 빌드 순서
1. module-core
2. module-domain
3. module-customer-api + module-admin-api (병렬 가능)
4. module-app

---

## pnpm Workspace 의존성 (Frontend)

```
customer-app
  └── depends on: @table-order/shared

admin-app
  └── depends on: @table-order/shared

shared
  └── depends on: (외부 라이브러리만)
```

### 빌드 순서
1. shared
2. customer-app + admin-app (병렬 가능)
