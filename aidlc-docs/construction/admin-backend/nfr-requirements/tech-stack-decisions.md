# Tech Stack Decisions - Unit 3: Admin Backend

## 개요
Unit 1 (Common/Shared)에서 결정된 기술 스택을 그대로 적용합니다. Admin Backend 모듈에 추가로 필요한 라이브러리와 설정만 보충합니다.

---

## Unit 1 기술 스택 재사용 (변경 없음)

| 카테고리 | 기술 | 버전 |
|---|---|---|
| Language | Java | 17 (LTS) |
| Framework | Spring Boot | 3.2.x |
| Build Tool | Gradle | 8.x |
| ORM | Spring Data JPA + Hibernate | - |
| Database | PostgreSQL | 15+ |
| Security | Spring Security + JWT | - |
| JWT | jjwt (io.jsonwebtoken) | 0.12.x |
| Validation | Jakarta Bean Validation | - |
| Logging | SLF4J + Logback | - |
| Testing | JUnit 5 + Mockito | - |

---

## Admin Backend 모듈 추가 의존성

### module-admin-api/build.gradle

| 라이브러리 | 용도 | 비고 |
|---|---|---|
| module-domain | 도메인 엔티티, Repository | 프로젝트 내부 의존성 |
| module-core | 보안, 예외, DTO, 설정 | 프로젝트 내부 의존성 |
| software.amazon.awssdk:s3 | S3 이미지 업로드 | 2.25.0 (고정 버전) |
| springdoc-openapi-starter-webmvc-ui | Swagger UI | 2.3.0 (고정 버전) |
| spring-boot-starter-test | 테스트 프레임워크 | Spring Boot BOM |
| spring-security-test | Security 테스트 | Spring Boot BOM |

### 의존성 구조
```
module-admin-api
  ├── implementation project(':module-domain')
  ├── implementation project(':module-core')
  ├── implementation 'software.amazon.awssdk:s3:2.25.0'
  ├── implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0'
  ├── testImplementation 'org.springframework.boot:spring-boot-starter-test'
  └── testImplementation 'org.springframework.security:spring-security-test'
```

---

## Admin Backend 모듈 구조

```
module-admin-api/
└── src/
    ├── main/java/com/tableorder/admin/
    │   ├── auth/
    │   │   ├── AdminAuthController.java
    │   │   ├── AdminAuthService.java
    │   │   └── dto/
    │   │       ├── LoginRequest.java
    │   │       ├── RegisterRequest.java
    │   │       ├── TokenResponse.java
    │   │       └── AdminResponse.java
    │   ├── store/
    │   │   ├── StoreController.java
    │   │   ├── StoreService.java
    │   │   └── dto/
    │   │       ├── StoreCreateRequest.java
    │   │       └── StoreResponse.java
    │   ├── table/
    │   │   ├── TableManageController.java
    │   │   ├── TableManageService.java
    │   │   └── dto/
    │   │       ├── TableCreateRequest.java
    │   │       ├── TableUpdateRequest.java
    │   │       └── TableResponse.java
    │   ├── menu/
    │   │   ├── MenuManageController.java
    │   │   ├── MenuManageService.java
    │   │   └── dto/
    │   │       ├── MenuCreateRequest.java
    │   │       ├── MenuUpdateRequest.java
    │   │       ├── MenuOrderUpdateRequest.java
    │   │       ├── CategoryCreateRequest.java
    │   │       ├── MenuResponse.java
    │   │       └── CategoryResponse.java
    │   ├── order/
    │   │   ├── OrderManageController.java
    │   │   ├── OrderManageService.java
    │   │   └── dto/
    │   │       ├── OrderStatusUpdateRequest.java
    │   │       ├── OrderResponse.java
    │   │       ├── OrderItemResponse.java
    │   │       └── OrderHistoryResponse.java
    │   ├── sse/
    │   │   ├── SSEController.java
    │   │   ├── SSEService.java
    │   │   └── dto/
    │   │       └── OrderEventData.java
    │   └── s3/
    │       ├── S3Service.java
    │       └── S3Config.java
    └── test/java/com/tableorder/admin/
        ├── auth/
        │   ├── AdminAuthControllerTest.java
        │   └── AdminAuthServiceTest.java
        ├── store/
        │   ├── StoreControllerTest.java
        │   └── StoreServiceTest.java
        ├── table/
        │   ├── TableManageControllerTest.java
        │   └── TableManageServiceTest.java
        ├── menu/
        │   ├── MenuManageControllerTest.java
        │   └── MenuManageServiceTest.java
        ├── order/
        │   ├── OrderManageControllerTest.java
        │   └── OrderManageServiceTest.java
        └── sse/
            └── SSEServiceTest.java
```

---

## 기술 선택 근거

| 결정 | 근거 |
|---|---|
| S3 SDK v2 직접 사용 | Spring Cloud AWS 대비 경량, 필요한 기능(PutObject)만 사용 |
| SpringDoc OpenAPI | Spring Boot 3.x 호환, Swagger UI 자동 생성 |
| 인메모리 SSE | MVP 단계에서 충분, Redis Pub/Sub는 수평 확장 시 도입 |
| 도메인별 패키지 분리 | 기능별 응집도 향상, 모듈 경계 명확화 |
| Controller/Service 분리 | 계층형 아키텍처 준수, 테스트 용이성 |
