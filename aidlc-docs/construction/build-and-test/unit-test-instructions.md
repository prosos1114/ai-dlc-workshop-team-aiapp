# Unit Test Execution - Unit 4: Customer Frontend

## Run Unit Tests

### 1. Execute All Unit Tests
```bash
cd table-order-frontend/packages/customer-app
pnpm test
```
> `vitest run` 실행 (단일 실행 모드, watch 아님)

### 2. Execute with Coverage
```bash
pnpm vitest run --coverage
```

### 3. Execute Specific Test File
```bash
pnpm vitest run src/store/__tests__/cartStore.test.ts
pnpm vitest run src/utils/__tests__/schemas.test.ts
```

## Test Coverage Target
- **목표**: 80% 이상 (훅, 스토어, 유틸리티)
- **측정 범위**: `src/store/`, `src/hooks/`, `src/utils/`, `src/api/`
- **제외**: 컴포넌트 렌더링 (Step 14에서 일부 훅 테스트 미완성)

## Test Files

### 스토어 테스트
| 파일 | 테스트 항목 | 테스트 수 |
|---|---|---|
| `store/__tests__/cartStore.test.ts` | 장바구니 CRUD, 수량 제한, 금액 계산, 패널 상태 | 12 |
| `store/__tests__/authStore.test.ts` | 인증 상태 관리, 로그인/로그아웃, 에러 처리 | 5 |
| `store/__tests__/orderStore.test.ts` | 주문 상태 변경, 삭제, 실패 카운터 | 5 |

### 유틸리티 테스트
| 파일 | 테스트 항목 | 테스트 수 |
|---|---|---|
| `utils/__tests__/schemas.test.ts` | Zod 스키마 검증 (비밀번호, 매장 코드, 주문) | 10 |

## Expected Results
- **총 테스트**: 32개
- **통과**: 32개
- **실패**: 0개
- **테스트 리포트**: 콘솔 출력

## Fix Failing Tests
1. 콘솔 출력에서 실패한 테스트 확인
2. 에러 메시지와 expected/received 값 비교
3. 코드 또는 테스트 수정
4. `pnpm test` 재실행
