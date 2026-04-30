# NFR Requirements - Unit 4: Customer Frontend

## 1. 성능 (Performance)

### NFR-FE-PERF-01: 초기 로딩 성능
- **First Contentful Paint (FCP)**: ≤ 1.5초
- **Largest Contentful Paint (LCP)**: ≤ 2.5초
- **Time to Interactive (TTI)**: ≤ 3.0초
- **조건**: 4G 네트워크, 중급 태블릿 디바이스 기준
- **전략**:
  - Route-based 코드 스플리팅 (React.lazy + Suspense)
  - 트리 쉐이킹 (Vite 기본 지원)
  - 정적 자산 압축 (gzip/brotli)
  - 크리티컬 CSS 인라인

### NFR-FE-PERF-02: 번들 크기
- **초기 번들**: ≤ 200KB (gzip 기준)
- **전체 번들**: ≤ 500KB (gzip 기준, 모든 청크 합산)
- **전략**:
  - 동적 import로 페이지별 청크 분리
  - 라이브러리 트리 쉐이킹 (lucide-react 아이콘 등)
  - QR 스캐너 라이브러리 lazy loading (SetupPage에서만 로드)

### NFR-FE-PERF-03: 런타임 성능
- **인터랙션 응답**: ≤ 100ms (탭, 클릭 피드백)
- **카테고리 전환**: ≤ 200ms (캐싱된 데이터)
- **장바구니 업데이트**: ≤ 50ms (로컬 연산)
- **금액 재계산**: ≤ 500ms
- **스크롤**: 60fps 유지
- **전략**:
  - React.memo로 불필요한 리렌더링 방지
  - useMemo/useCallback 적절 사용
  - 가상화 (메뉴 목록이 많을 경우)

### NFR-FE-PERF-04: 이미지 성능
- **이미지 로딩**: lazy loading 적용 (뷰포트 진입 시 로드)
- **포맷**: WebP 우선, JPEG 폴백
- **반응형**: srcset으로 디바이스 해상도별 이미지 제공
- **플레이스홀더**: 로딩 중 저해상도 블러 또는 스켈레톤 표시
- **에러 처리**: 로드 실패 시 기본 플레이스홀더 이미지 표시
- **캐싱**: Cache-Control 헤더로 브라우저 캐싱 활용

---

## 2. 보안 (Security)

### NFR-FE-SEC-01: XSS 방지
- React의 기본 이스케이프 활용 (JSX 자동 이스케이프)
- `dangerouslySetInnerHTML` 사용 금지
- 사용자 입력 데이터 렌더링 시 추가 sanitize
- CSP 헤더 준수 (`default-src 'self'`)

### NFR-FE-SEC-02: 인증 토큰 관리
- JWT 토큰 localStorage 저장 (태블릿 고정 디바이스 특성)
- Axios 인터셉터로 모든 API 요청에 Authorization 헤더 자동 첨부
- 401 응답 시 자동 재인증 시도 → 실패 시 초기 설정 화면 이동
- 토큰 만료 체크: API 호출 시 서버 측 검증에 의존

### NFR-FE-SEC-03: 민감 데이터 처리
- 비밀번호 입력 필드: type="password" (마스킹)
- localStorage 저장 시 비밀번호 클라이언트 암호화
- 콘솔 로그에 토큰/비밀번호 출력 금지
- 프로덕션 빌드에서 소스맵 비활성화

### NFR-FE-SEC-04: 의존성 보안
- 정확한 버전 고정 (lock file)
- 알려진 취약점 없는 라이브러리만 사용
- 외부 CDN 리소스 사용 시 SRI(Subresource Integrity) 해시 적용

---

## 3. 신뢰성 (Reliability)

### NFR-FE-REL-01: 오프라인/네트워크 대응
- **장바구니**: 완전 오프라인 동작 (localStorage 기반, 서버 통신 불필요)
- **API 호출 실패**: 에러 메시지 + "다시 시도" 버튼 제공
- **네트워크 감지**: navigator.onLine으로 네트워크 상태 감지
- **오프라인 시 안내**: "네트워크 연결을 확인해주세요" 배너 표시

### NFR-FE-REL-02: SSE 연결 안정성
- 연결 끊김 시 자동 재연결 (3초 간격, 최대 5회)
- 재연결 성공 시 전체 데이터 재조회 (누락 방지)
- 연결 상태 인디케이터 표시
- 브라우저 탭 비활성 → 활성 전환 시 연결 상태 확인 및 데이터 동기화

### NFR-FE-REL-03: 에러 복구
- API 에러 시 사용자 친화적 한국어 메시지
- 재시도 가능한 에러는 재시도 버튼 제공
- 3회 이상 연속 실패 시 추가 안내 ("직원에게 문의해주세요")
- 치명적 에러 시 Error Boundary로 앱 크래시 방지

### NFR-FE-REL-04: 데이터 동기화
- 장바구니: localStorage ↔ Zustand 스토어 양방향 동기화
- 인증 정보: localStorage에서 앱 시작 시 로드
- 메뉴 데이터: 세션 내 클라이언트 캐싱 (카테고리별)
- 주문 내역: SSE로 실시간 동기화 + 화면 진입 시 전체 조회

---

## 4. 사용성 (Usability)

### NFR-FE-USE-01: 터치 인터페이스
- 모든 인터랙티브 요소: 최소 44x44px 터치 영역
- 터치 피드백: 탭 시 시각적 피드백 (ripple 또는 opacity 변화)
- 스와이프 제스처: 장바구니 패널 열기/닫기
- 더블 탭 방지: 주문 확정 등 중요 액션에서 중복 탭 방지

### NFR-FE-USE-02: 반응성
- 가로 모드(Landscape) 고정 최적화 (1024x768 이상)
- 다양한 태블릿 해상도 대응 (10인치 ~ 12인치)
- 폰트 크기: 본문 16px 이상 (태블릿 가독성)

### NFR-FE-USE-03: 피드백
- 로딩 상태: 스켈레톤 UI (메뉴), 스피너 (주문 처리)
- 성공 피드백: 토스트 메시지 (3초 자동 사라짐)
- 에러 피드백: 인라인 에러 메시지 + 토스트
- 확인 필요 액션: 확인 팝업 (장바구니 비우기 등)

---

## 5. 접근성 (Accessibility)

### NFR-FE-ACC-01: WCAG 2.1 Level A 준수
- 시맨틱 HTML 사용 (header, nav, main, section, button 등)
- 모든 이미지에 alt 텍스트
- 색상만으로 정보 전달하지 않음 (상태 배지에 텍스트 병행)
- 충분한 색상 대비 (4.5:1 이상)
- 포커스 표시 가시적
- 폼 요소에 label 연결

---

## 6. 유지보수성 (Maintainability)

### NFR-FE-MAINT-01: 코드 품질
- 단위 테스트 커버리지 80% 이상 (커스텀 훅, 유틸리티, 스토어)
- ESLint + Prettier 코드 스타일 일관성
- TypeScript strict 모드 활성화
- 컴포넌트별 단일 책임 원칙

### NFR-FE-MAINT-02: 프로젝트 구조
- 기능별 디렉토리 구조 (components, hooks, store, pages, api, utils)
- 공통 컴포넌트와 페이지별 컴포넌트 분리
- @table-order/shared 패키지로 공통 타입/유틸리티 공유

### NFR-FE-MAINT-03: 빌드 및 개발 환경
- Vite 기반 빠른 HMR (Hot Module Replacement)
- 환경변수 관리 (.env 파일)
- 프로덕션 빌드 최적화 (minify, tree-shaking, chunk splitting)

---

## Security Extension Compliance

| Rule | 적용 여부 | 비고 |
|---|---|---|
| SECURITY-01 | N/A | 프론트엔드에 데이터 저장소 없음 |
| SECURITY-02 | N/A | 네트워크 인프라는 백엔드/인프라 영역 |
| SECURITY-03 | N/A | 프론트엔드 로깅은 콘솔 기반, 중앙 로그 서비스 해당 없음 |
| SECURITY-04 | Compliant | 백엔드에서 Security Headers 설정, 프론트엔드는 CSP 준수 코드 작성 |
| SECURITY-05 | Compliant | Zod 스키마로 클라이언트 입력 검증, 서버 측 검증은 백엔드 |
| SECURITY-06 | N/A | IAM 정책은 인프라 영역 |
| SECURITY-07 | N/A | 네트워크 설정은 인프라 영역 |
| SECURITY-08 | Compliant | 인증 가드(AuthGuard)로 미인증 접근 차단, 토큰 기반 API 호출 |
| SECURITY-09 | Compliant | 프로덕션 소스맵 비활성화, 에러 메시지에 내부 정보 미노출 |
| SECURITY-10 | Compliant | lock file 커밋, 정확한 버전 고정, 공식 npm 레지스트리 사용 |
| SECURITY-11 | Compliant | 인증 로직 전용 모듈 분리, 입력 검증 + 서버 인증 이중 방어 |
| SECURITY-12 | Compliant | 비밀번호 마스킹, localStorage 암호화 저장, 토큰 만료 처리 |
| SECURITY-13 | Compliant | 외부 CDN 리소스 SRI 적용, 안전한 데이터 역직렬화 |
| SECURITY-14 | N/A | 프론트엔드 알림/모니터링은 백엔드 영역 |
| SECURITY-15 | Compliant | Error Boundary로 전역 에러 처리, API 호출 try/catch, 에러 시 안전한 상태 유지 |
