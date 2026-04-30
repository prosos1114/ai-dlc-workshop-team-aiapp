# Tech Stack Decisions - Unit 4: Customer Frontend

## 기술 스택 확정

### Core

| 카테고리 | 기술 | 버전 | 선택 이유 |
|---|---|---|---|
| **Language** | TypeScript | 5.4+ | 타입 안전성, strict 모드 |
| **Framework** | React | 18.2+ | 컴포넌트 기반, Suspense/lazy 지원 |
| **Build Tool** | Vite | 5.1+ | 빠른 HMR, 코드 스플리팅, 트리 쉐이킹 |
| **Package Manager** | pnpm | 8.x | 모노레포 workspace 지원 |

### 라우팅 & 상태 관리

| 카테고리 | 기술 | 버전 | 선택 이유 |
|---|---|---|---|
| **Routing** | React Router | 6.22+ | SPA 라우팅, lazy route 지원 |
| **State Management** | Zustand | 4.5+ | 경량, persist 미들웨어 (localStorage 동기화) |
| **Server State** | 직접 구현 | - | 단순한 API 호출 패턴, 별도 라이브러리 불필요 |

### UI & 스타일링

| 카테고리 | 기술 | 버전 | 선택 이유 |
|---|---|---|---|
| **CSS Framework** | Tailwind CSS | 3.4+ | 유틸리티 기반, 번들 최적화 (PurgeCSS) |
| **Icons** | Lucide React | 0.344+ | 경량, 트리 쉐이킹 |
| **Animations** | CSS Transitions + Tailwind | - | 경량, 추가 라이브러리 불필요 |

### 폼 & 검증

| 카테고리 | 기술 | 버전 | 선택 이유 |
|---|---|---|---|
| **Form** | React Hook Form | 7.50+ | 성능, 비제어 컴포넌트 기반 |
| **Validation** | Zod | 3.22+ | TypeScript 네이티브, 타입 추론 |
| **Resolver** | @hookform/resolvers | 3.3+ | Zod + React Hook Form 통합 |

### HTTP & 실시간 통신

| 카테고리 | 기술 | 버전 | 선택 이유 |
|---|---|---|---|
| **HTTP Client** | Axios | 1.6+ | 인터셉터, 에러 처리, 토큰 자동 첨부 |
| **SSE Client** | EventSource (브라우저 내장) | - | 추가 라이브러리 불필요, 표준 API |

### QR 코드

| 카테고리 | 기술 | 버전 | 선택 이유 |
|---|---|---|---|
| **QR Scanner** | html5-qrcode | 2.3+ | 경량, 카메라 API 래핑, 다양한 QR 포맷 지원 |

**선택 근거**: 
- `@zxing/browser` 대비 번들 크기 작음
- 카메라 권한 관리 내장
- React 래퍼 불필요 (직접 통합 간단)
- lazy loading으로 SetupPage에서만 로드 (초기 번들 영향 없음)

### 테스트

| 카테고리 | 기술 | 버전 | 선택 이유 |
|---|---|---|---|
| **Test Runner** | Vitest | 1.3+ | Vite 네이티브, 빠른 실행 |
| **Test Utilities** | @testing-library/react | 14.2+ | 사용자 관점 테스트 |
| **DOM Utilities** | @testing-library/jest-dom | 6.4+ | DOM 매처 확장 |
| **User Events** | @testing-library/user-event | 14.5+ | 사용자 인터랙션 시뮬레이션 |

### 코드 품질

| 카테고리 | 기술 | 버전 | 선택 이유 |
|---|---|---|---|
| **Linter** | ESLint | 8.57+ | 코드 품질 규칙 |
| **Formatter** | Prettier | 3.2+ | 코드 스타일 일관성 |
| **Type Check** | TypeScript (strict) | 5.4+ | 컴파일 타임 타입 검증 |

---

## 주요 의존성 (package.json)

```json
{
  "name": "@table-order/customer-app",
  "private": true,
  "version": "0.1.0",
  "type": "module",
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.22.0",
    "axios": "^1.6.0",
    "zustand": "^4.5.0",
    "react-hook-form": "^7.50.0",
    "zod": "^3.22.0",
    "@hookform/resolvers": "^3.3.0",
    "lucide-react": "^0.344.0",
    "html5-qrcode": "^2.3.8",
    "@table-order/shared": "workspace:*"
  },
  "devDependencies": {
    "typescript": "^5.4.0",
    "vite": "^5.1.0",
    "@vitejs/plugin-react": "^4.2.0",
    "tailwindcss": "^3.4.0",
    "postcss": "^8.4.0",
    "autoprefixer": "^10.4.0",
    "vitest": "^1.3.0",
    "@testing-library/react": "^14.2.0",
    "@testing-library/jest-dom": "^6.4.0",
    "@testing-library/user-event": "^14.5.0",
    "jsdom": "^24.0.0",
    "eslint": "^8.57.0",
    "eslint-plugin-react-hooks": "^4.6.0",
    "@typescript-eslint/eslint-plugin": "^7.0.0",
    "@typescript-eslint/parser": "^7.0.0",
    "prettier": "^3.2.0",
    "prettier-plugin-tailwindcss": "^0.5.0"
  }
}
```

---

## 기술 선택 근거 요약

| 결정 | 대안 | 선택 이유 |
|---|---|---|
| html5-qrcode vs @zxing/browser | @zxing/browser | 번들 크기 작음, 카메라 관리 내장, 간단한 API |
| EventSource vs SSE 라이브러리 | eventsource-polyfill | 브라우저 내장 API로 충분, 추가 의존성 불필요 |
| Zustand persist vs 직접 구현 | 직접 localStorage 동기화 | Zustand persist 미들웨어가 간편하고 안정적 |
| CSS Transitions vs Framer Motion | Framer Motion | 슬라이드/확장 애니메이션은 CSS로 충분, 번들 절감 |
| Vitest vs Jest | Jest | Vite 네이티브 통합, 설정 간소화, 빠른 실행 |
| 직접 API 관리 vs React Query | React Query (TanStack) | API 호출 패턴이 단순, 추가 라이브러리 오버헤드 불필요 |

---

## 빌드 최적화 설정

### Vite 설정 (vite.config.ts)
```typescript
// 주요 최적화 설정
{
  build: {
    target: 'es2020',
    minify: 'terser',
    sourcemap: false,           // 프로덕션 소스맵 비활성화 (SECURITY-09)
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['react', 'react-dom', 'react-router-dom'],
          state: ['zustand', 'axios'],
          form: ['react-hook-form', 'zod', '@hookform/resolvers'],
        }
      }
    },
    chunkSizeWarningLimit: 200  // 200KB 경고
  }
}
```

### 코드 스플리팅 전략
```
초기 로드:
  - vendor 청크 (React, React Router)
  - state 청크 (Zustand, Axios)
  - 메인 앱 + MenuPage

Lazy 로드:
  - SetupPage + html5-qrcode (초기 설정 시에만)
  - OrderConfirmPage (주문 확인 시)
  - OrderSuccessPage (주문 성공 시)
  - OrderHistoryPage (주문 내역 조회 시)
```
