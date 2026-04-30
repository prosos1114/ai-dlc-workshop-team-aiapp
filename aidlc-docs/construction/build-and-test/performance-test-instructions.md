# Performance Test Instructions - Unit 4: Customer Frontend

## Purpose
Customer Frontend의 성능 요구사항(NFR-FE-PERF-01~04) 충족 여부를 검증합니다.

## Performance Requirements
| 항목 | 목표 |
|---|---|
| First Contentful Paint (FCP) | ≤ 1.5초 |
| Largest Contentful Paint (LCP) | ≤ 2.5초 |
| Time to Interactive (TTI) | ≤ 3.0초 |
| 초기 번들 크기 | ≤ 200KB (gzip) |
| 전체 번들 크기 | ≤ 500KB (gzip) |
| 인터랙션 응답 | ≤ 100ms |
| 장바구니 업데이트 | ≤ 50ms |

## Test 1: 번들 크기 분석

### 빌드 후 크기 확인
```bash
cd table-order-frontend/packages/customer-app
pnpm build
```
빌드 출력에서 각 청크 크기 확인:
- vendor 청크 (react, react-dom, react-router-dom)
- state 청크 (zustand, axios)
- form 청크 (react-hook-form, zod)
- main 청크

### Vite 번들 분석 (선택)
```bash
npx vite-bundle-visualizer
```
> 번들 구성 시각화

## Test 2: Lighthouse 성능 측정

### Chrome DevTools Lighthouse
1. `pnpm preview`로 프로덕션 빌드 서빙
2. Chrome DevTools → Lighthouse 탭
3. "Performance" 카테고리 선택
4. "Analyze page load" 실행
5. 결과 확인:
   - FCP ≤ 1.5초
   - LCP ≤ 2.5초
   - TTI ≤ 3.0초
   - Performance Score ≥ 90

### 측정 조건
- **네트워크**: Simulated 4G throttling
- **CPU**: 4x slowdown
- **디바이스**: Desktop (태블릿 가로 모드 시뮬레이션)

## Test 3: 런타임 성능 측정

### Chrome DevTools Performance
1. 앱 실행 후 Performance 탭 녹화
2. 다음 시나리오 수행:
   - 카테고리 전환 (200ms 이내 확인)
   - 장바구니 추가/수량 변경 (50ms 이내 확인)
   - 메뉴 카드 확장/축소 (100ms 이내 확인)
3. 녹화 중지 후 프레임 드롭 확인 (60fps 유지)

## Test 4: 이미지 로딩 성능

### Network 탭 확인
1. Chrome DevTools → Network 탭
2. 메뉴 화면 로드
3. 확인 항목:
   - 이미지 lazy loading 동작 (뷰포트 밖 이미지 미로드)
   - 이미지 캐싱 (Cache-Control 헤더)
   - 이미지 로드 실패 시 플레이스홀더 표시

## Expected Results
| 항목 | 목표 | 상태 |
|---|---|---|
| FCP | ≤ 1.5초 | 빌드 후 측정 필요 |
| LCP | ≤ 2.5초 | 빌드 후 측정 필요 |
| 초기 번들 | ≤ 200KB | 빌드 후 확인 필요 |
| Lighthouse Score | ≥ 90 | 빌드 후 측정 필요 |
| 60fps 스크롤 | 유지 | 런타임 측정 필요 |
