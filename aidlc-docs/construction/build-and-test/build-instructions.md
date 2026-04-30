# Build Instructions - Unit 4: Customer Frontend

## Prerequisites
- **Node.js**: 18.x 이상
- **pnpm**: 8.x 이상
- **OS**: Windows / macOS / Linux

## Environment Variables

`.env` 파일을 `table-order-frontend/packages/customer-app/` 에 생성:
```
VITE_API_BASE_URL=http://localhost:8080
```

## Build Steps

### 1. Install Dependencies
```bash
cd table-order-frontend
pnpm install
```

### 2. Build Shared Package (선행 필수)
```bash
cd packages/shared
pnpm build
```
> shared 패키지가 빌드되어야 customer-app에서 import 가능

### 3. Build Customer App
```bash
cd packages/customer-app
pnpm build
```

### 4. Verify Build Success
- **Expected Output**: `dist/` 디렉토리에 빌드 결과물 생성
- **Build Artifacts**:
  - `dist/index.html`
  - `dist/assets/*.js` (vendor, state, form, main 청크)
  - `dist/assets/*.css`
- **청크 크기 확인**: 각 청크가 200KB(gzip) 이하인지 확인
- **소스맵**: 프로덕션 빌드에서 소스맵 미생성 확인

### 5. Preview Build (선택)
```bash
pnpm preview
```
> http://localhost:4173 에서 빌드 결과물 확인

## Troubleshooting

### pnpm install 실패
- **원인**: Node.js 버전 불일치 또는 pnpm 미설치
- **해결**: `node -v`로 버전 확인, `npm install -g pnpm`으로 pnpm 설치

### shared 패키지 import 에러
- **원인**: shared 패키지 미빌드
- **해결**: `cd packages/shared && pnpm build` 실행 후 재빌드

### TypeScript 컴파일 에러
- **원인**: 타입 불일치 또는 strict 모드 위반
- **해결**: 에러 메시지의 파일/라인 확인 후 타입 수정

### 청크 크기 경고
- **원인**: 200KB 초과 청크 존재
- **해결**: vite.config.ts의 manualChunks 설정 조정 또는 dynamic import 추가
