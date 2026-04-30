# Tech Stack Decisions - Unit 1: Common/Shared

## Backend Tech Stack

| 카테고리 | 기술 | 버전 | 선택 이유 |
|---|---|---|---|
| **Language** | Java | 17 (LTS) | 안정성, 생태계, Spring Boot 호환 |
| **Framework** | Spring Boot | 3.2.x | 생산성, 자동 설정, 풍부한 생태계 |
| **Build Tool** | Gradle | 8.x | 멀티 모듈 지원, 빌드 성능 |
| **ORM** | Spring Data JPA + Hibernate | - | 생산성, 타입 안전 쿼리 |
| **Database** | PostgreSQL | 15+ | ACID, JSON 지원, 확장성 |
| **Migration** | Flyway | - | DB 스키마 버전 관리 |
| **Security** | Spring Security | - | JWT 필터, 접근 제어 |
| **JWT** | jjwt (io.jsonwebtoken) | 0.12.x | 경량, 표준 준수 |
| **Validation** | Jakarta Bean Validation | - | 선언적 입력 검증 |
| **Logging** | SLF4J + Logback | - | 구조화된 로깅, Spring 기본 |
| **API Docs** | SpringDoc OpenAPI | 2.x | Swagger UI 자동 생성 |
| **S3 Client** | AWS SDK v2 | 2.x | 공식 SDK, 비동기 지원 |
| **Testing** | JUnit 5 + Mockito | - | 표준 테스트 프레임워크 |
| **Test DB** | H2 (테스트) / Testcontainers | - | 빠른 테스트 / 실제 DB 테스트 |

---

## Frontend Tech Stack

| 카테고리 | 기술 | 버전 | 선택 이유 |
|---|---|---|---|
| **Language** | TypeScript | 5.x | 타입 안전성, 개발 생산성 |
| **Framework** | React | 18.x | 컴포넌트 기반, 생태계 |
| **Build Tool** | Vite | 5.x | 빠른 빌드, HMR |
| **Package Manager** | pnpm | 8.x | 모노레포 지원, 디스크 효율 |
| **Routing** | React Router | 6.x | SPA 라우팅 표준 |
| **State Management** | Zustand | 4.x | 경량, 간단한 API |
| **HTTP Client** | Axios | 1.x | 인터셉터, 에러 처리 |
| **Form** | React Hook Form | 7.x | 성능, 검증 통합 |
| **Validation** | Zod | 3.x | TypeScript 네이티브 스키마 검증 |
| **UI Components** | Tailwind CSS | 3.x | 유틸리티 기반, 커스터마이징 |
| **Icons** | Lucide React | - | 경량, 트리 쉐이킹 |
| **Testing** | Vitest + React Testing Library | - | Vite 네이티브, 컴포넌트 테스트 |
| **Linting** | ESLint + Prettier | - | 코드 품질, 일관성 |

---

## Infrastructure / DevOps

| 카테고리 | 기술 | 선택 이유 |
|---|---|---|
| **Cloud** | AWS | 요구사항 명시 |
| **Compute** | EC2 | 요구사항 명시 |
| **Database** | RDS (PostgreSQL) | 관리형 DB, Multi-AZ |
| **Storage** | S3 | 이미지 저장 |
| **Container** | Docker | 일관된 배포 환경 |

---

## 주요 라이브러리 의존성

### Backend (build.gradle 주요 의존성)
```groovy
// module-core
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'org.springframework.boot:spring-boot-starter-web'
implementation 'org.springframework.boot:spring-boot-starter-validation'
implementation 'io.jsonwebtoken:jjwt-api:0.12.5'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.5'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.5'

// module-domain
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
runtimeOnly 'org.postgresql:postgresql'

// module-api
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0'
implementation 'software.amazon.awssdk:s3:2.25.0'
implementation 'org.flywaydb:flyway-core'

// testing
testImplementation 'org.springframework.boot:spring-boot-starter-test'
testImplementation 'org.springframework.security:spring-security-test'
testImplementation 'org.testcontainers:postgresql'
```

### Frontend (package.json 주요 의존성)
```json
{
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.22.0",
    "axios": "^1.6.0",
    "zustand": "^4.5.0",
    "react-hook-form": "^7.50.0",
    "zod": "^3.22.0",
    "@hookform/resolvers": "^3.3.0",
    "lucide-react": "^0.344.0"
  },
  "devDependencies": {
    "typescript": "^5.4.0",
    "vite": "^5.1.0",
    "@vitejs/plugin-react": "^4.2.0",
    "tailwindcss": "^3.4.0",
    "vitest": "^1.3.0",
    "@testing-library/react": "^14.2.0",
    "eslint": "^8.57.0",
    "prettier": "^3.2.0"
  }
}
```

---

## 기술 선택 근거 요약

| 결정 | 대안 | 선택 이유 |
|---|---|---|
| Zustand vs Redux | Redux Toolkit | 프로젝트 규모에 비해 Redux는 과도, Zustand가 간결 |
| Vite vs CRA | Create React App | CRA 유지보수 중단, Vite가 빠르고 현대적 |
| Tailwind vs MUI | Material UI | 커스터마이징 자유도, 번들 크기 절감 |
| Flyway vs Liquibase | Liquibase | SQL 기반 마이그레이션이 직관적, Spring Boot 통합 우수 |
| Zod vs Yup | Yup | TypeScript 네이티브, 타입 추론 우수 |
| pnpm vs yarn | Yarn Berry | 모노레포 지원 우수, 디스크 효율, 속도 |
| Vitest vs Jest | Jest | Vite 네이티브 통합, 설정 간소화 |
