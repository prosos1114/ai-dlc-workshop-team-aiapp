# 요구사항 명확화 질문

아래 질문에 답변해 주세요. 각 질문의 [Answer]: 태그 뒤에 선택한 옵션 문자를 입력해 주세요.

## Question 1
프로젝트의 기술 스택(프로그래밍 언어 및 프레임워크)은 무엇을 사용하시겠습니까?

A) TypeScript + React (프론트엔드) / TypeScript + NestJS (백엔드) / PostgreSQL (DB)
B) TypeScript + React (프론트엔드) / TypeScript + Express.js (백엔드) / PostgreSQL (DB)
C) TypeScript + Next.js (풀스택) / PostgreSQL (DB)
D) TypeScript + React (프론트엔드) / Java + Spring Boot (백엔드) / PostgreSQL (DB)
E) TypeScript + React (프론트엔드) / Python + FastAPI (백엔드) / PostgreSQL (DB)
X) Other (please describe after [Answer]: tag below)

[Answer]: D

## Question 2
배포 환경은 어떻게 계획하고 계십니까?

A) AWS 클라우드 (EC2, RDS, S3 등)
B) AWS 서버리스 (Lambda, DynamoDB, API Gateway 등)
C) Docker 컨테이너 기반 (Docker Compose 또는 Kubernetes)
D) 온프레미스 서버
X) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 3
메뉴 이미지 저장 방식은 어떻게 하시겠습니까?

A) 외부 이미지 URL만 지원 (이미지 호스팅은 별도 서비스 사용)
B) 서버에 직접 파일 업로드 및 저장
C) 클라우드 스토리지 (S3 등)에 업로드
X) Other (please describe after [Answer]: tag below)

[Answer]: C

## Question 4
동시 접속 사용자 규모는 어느 정도를 예상하십니까?

A) 소규모 - 단일 매장 (동시 10~30명)
B) 중규모 - 여러 매장 (동시 100~500명)
C) 대규모 - 프랜차이즈 (동시 1000명 이상)
X) Other (please describe after [Answer]: tag below)

[Answer]: C

## Question 5
관리자 계정 관리 방식은 어떻게 하시겠습니까?

A) 매장당 1개의 관리자 계정 (고정, DB에 직접 등록)
B) 매장당 다수의 관리자 계정 (회원가입 기능 포함)
C) 슈퍼 관리자가 매장 관리자 계정을 생성/관리
X) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 6
매장(Store) 등록은 어떻게 이루어집니까?

A) DB에 직접 등록 (초기 시딩)
B) 별도의 매장 등록 API/관리 화면 제공
C) 슈퍼 관리자 화면에서 매장 생성
X) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 7
테이블 태블릿의 자동 로그인에서 "테이블 비밀번호"는 어떤 용도입니까?

A) 태블릿 초기 설정 시 인증용 (매장 관리자가 설정한 비밀번호로 태블릿을 해당 테이블에 바인딩)
B) 고객이 테이블에 앉을 때마다 입력하는 비밀번호
C) 태블릿 기기 자체의 잠금 비밀번호
X) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 8
주문 상태 변경 흐름은 어떻게 됩니까?

A) 대기중 → 준비중 → 완료 (관리자가 수동으로 상태 변경)
B) 대기중 → 완료 (2단계만, 관리자가 완료 처리)
C) 대기중 → 준비중 → 완료 (자동 + 수동 혼합)
X) Other (please describe after [Answer]: tag below)

[Answer]: C

## Question 9
MVP에서 메뉴 관리 기능은 포함됩니까? (요구사항 3.2.4에 정의되어 있으나 MVP 범위(섹션 4)에는 명시되지 않음)

A) MVP에 포함 - 메뉴 CRUD 기능 모두 구현
B) MVP에서 제외 - DB 직접 등록으로 대체
C) MVP에 부분 포함 - 메뉴 조회/수정만 (등록/삭제는 DB 직접)
X) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 10
Security Extension 규칙을 이 프로젝트에 적용하시겠습니까?

A) Yes — 모든 SECURITY 규칙을 blocking constraint로 적용 (프로덕션 수준 애플리케이션에 권장)
B) No — 모든 SECURITY 규칙 건너뛰기 (PoC, 프로토타입, 실험적 프로젝트에 적합)
X) Other (please describe after [Answer]: tag below)

[Answer]: A

