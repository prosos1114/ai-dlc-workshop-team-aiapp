# NFR Design Patterns - Unit 4: Customer Frontend

## 1. 성능 패턴

### Pattern: Route-Based Code Splitting
**적용 NFR**: NFR-FE-PERF-01, NFR-FE-PERF-02
```typescript
// App.tsx - 라우트별 lazy loading
const SetupPage = lazy(() => import('./pages/SetupPage'));
const OrderConfirmPage = lazy(() => import('./pages/OrderConfirmPage'));
const OrderSuccessPage = lazy(() => import('./pages/OrderSuccessPage'));
const OrderHistoryPage = lazy(() => import('./pages/OrderHistoryPage'));

// MenuPage는 홈이므로 eager loading (초기 번들에 포함)
import MenuPage from './pages/MenuPage';

// Suspense로 로딩 상태 처리
<Suspense fallback={<LoadingSpinner />}>
  <Routes>
    <Route path="/setup" element={<SetupPage />} />
    <Route path="/menu" element={<MenuPage />} />
    <Route path="/order-confirm" element={<OrderConfirmPage />} />
    <Route path="/order-success" element={<OrderSuccessPage />} />
    <Route path="/order-history" element={<OrderHistoryPage />} />
  </Routes>
</Suspense>
```

**청크 분리 전략**:
- `vendor`: react, react-dom, react-router-dom (~45KB gzip)
- `state`: zustand, axios (~15KB gzip)
- `form`: react-hook-form, zod, @hookform/resolvers (~20KB gzip)
- `main`: MenuPage + AppLayout + 공통 컴포넌트 (~50KB gzip)
- `setup`: SetupPage + html5-qrcode (lazy, ~80KB gzip)
- `order`: OrderConfirm/Success/HistoryPage (lazy, ~20KB gzip)

### Pattern: Image Optimization Pipeline
**적용 NFR**: NFR-FE-PERF-04
```typescript
// OptimizedImage 컴포넌트
interface OptimizedImageProps {
  src: string | null;
  alt: string;
  className?: string;
  sizes?: string;
}

// 동작 흐름:
// 1. src가 null → PlaceholderImage 표시
// 2. loading="lazy" → 뷰포트 진입 시 로드
// 3. WebP srcset 우선, JPEG 폴백
// 4. 로딩 중 → 스켈레톤 플레이스홀더
// 5. 로드 실패 → onError → PlaceholderImage
// 6. 로드 성공 → fade-in 애니메이션
```

**srcset 전략**:
```html
<picture>
  <source srcset="{url}?w=300&f=webp 300w, {url}?w=600&f=webp 600w" type="image/webp" />
  <img src="{url}?w=300" srcset="{url}?w=300 300w, {url}?w=600 600w"
       sizes="(min-width: 1024px) 33vw, 50vw"
       loading="lazy" alt="메뉴 이미지" />
</picture>
```

### Pattern: Render Optimization
**적용 NFR**: NFR-FE-PERF-03
```
최적화 전략:
  1. React.memo: MenuCard, CartItem, OrderCard 등 리스트 아이템
  2. useMemo: 총 금액 계산, 필터링된 메뉴 목록
  3. useCallback: 이벤트 핸들러 (addToCart, removeItem 등)
  4. key 최적화: 안정적인 ID 기반 key (menuId, orderId)
  5. 상태 분리: 자주 변경되는 상태와 정적 상태 분리
     - CartStore: 자주 변경 (수량, 패널 상태)
     - MenuStore: 카테고리 전환 시에만 변경
     - AuthStore: 거의 변경 없음
```

---

## 2. 보안 패턴

### Pattern: Authentication Flow with Auto-Retry
**적용 NFR**: NFR-FE-SEC-02, SECURITY-08, SECURITY-12
```
앱 시작
  → AuthGuard 컴포넌트
    → localStorage에서 AuthInfo 로드
    → token 존재?
      → YES: isAuthenticated = true → 자식 렌더링
        → API 호출 시 401 발생?
          → Axios 인터셉터가 자동 재인증 시도
          → 성공: 새 토큰 저장 → 원래 요청 재시도
          → 실패: localStorage 클리어 → /setup 리다이렉트
      → NO: /setup 리다이렉트

토큰 관리:
  - 저장: localStorage (태블릿 고정 디바이스)
  - 첨부: Axios request 인터셉터 (Authorization: Bearer {token})
  - 갱신: 401 응답 시 저장된 credentials로 재인증
  - 삭제: 재인증 실패 시 또는 수동 로그아웃 시
```

### Pattern: Input Validation with Zod
**적용 NFR**: NFR-FE-SEC-01, SECURITY-05
```typescript
// 초기 설정 폼 검증
const tableSetupSchema = z.object({
  password: z.string().min(4, '비밀번호는 최소 4자리입니다'),
});

// 장바구니 수량 검증
const cartQuantitySchema = z.number().int().min(1).max(99);

// 주문 생성 검증
const orderCreateSchema = z.object({
  items: z.array(z.object({
    menuId: z.number().positive(),
    menuName: z.string().min(1),
    quantity: z.number().int().min(1).max(99),
    unitPrice: z.number().int().min(0),
  })).min(1, '주문 항목이 비어있습니다'),
});

// React Hook Form + Zod 통합
const { register, handleSubmit } = useForm({
  resolver: zodResolver(tableSetupSchema),
});
```

### Pattern: XSS Prevention
**적용 NFR**: NFR-FE-SEC-01, SECURITY-04
```
방지 전략:
  1. React JSX 자동 이스케이프 활용 (기본)
  2. dangerouslySetInnerHTML 사용 금지 (ESLint 규칙)
  3. URL 검증: 이미지 URL은 허용된 도메인만 (S3 버킷)
  4. 사용자 입력 렌더링 시 추가 sanitize 불필요 (React 기본 처리)
  5. CSP 준수: inline script/style 사용 금지
```

---

## 3. 에러 처리 및 신뢰성 패턴

### Pattern: Global Axios Interceptor
**적용 NFR**: NFR-FE-REL-01, NFR-FE-REL-03, SECURITY-15
```typescript
// api/client.ts
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 10000,
});

// Request 인터셉터: 토큰 자동 첨부
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response 인터셉터: 글로벌 에러 처리
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    // 401: 자동 재인증
    if (error.response?.status === 401) {
      const reAuthSuccess = await attemptReAuth();
      if (reAuthSuccess) {
        return apiClient(error.config); // 원래 요청 재시도
      }
      clearAuthAndRedirect();
      return Promise.reject(error);
    }

    // 403: 접근 거부
    if (error.response?.status === 403) {
      showToast('접근 권한이 없습니다', 'error');
      return Promise.reject(error);
    }

    // 네트워크 오류: 자동 재시도 (최대 2회)
    if (!error.response && error.config._retryCount < 2) {
      error.config._retryCount = (error.config._retryCount || 0) + 1;
      await delay(1000 * error.config._retryCount);
      return apiClient(error.config);
    }

    // 네트워크 오류 (재시도 소진): 에러 메시지
    if (!error.response) {
      showToast('네트워크 연결을 확인해주세요', 'error');
      return Promise.reject(error);
    }

    // 5xx: 서버 오류
    if (error.response?.status >= 500) {
      showToast('잠시 후 다시 시도해주세요', 'error');
      return Promise.reject(error);
    }

    // 4xx: 비즈니스 에러 (서버 메시지 표시)
    const message = error.response?.data?.message || '오류가 발생했습니다';
    showToast(message, 'error');
    return Promise.reject(error);
  }
);
```

### Pattern: Error Boundary
**적용 NFR**: NFR-FE-REL-03, SECURITY-15
```typescript
// ErrorBoundary 컴포넌트
class ErrorBoundary extends React.Component {
  state = { hasError: false };

  static getDerivedStateFromError() {
    return { hasError: true };
  }

  componentDidCatch(error, errorInfo) {
    // 에러 로깅 (콘솔, 향후 모니터링 서비스)
    console.error('Uncaught error:', error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return <ErrorFallback onRetry={() => this.setState({ hasError: false })} />;
    }
    return this.props.children;
  }
}

// 적용 위치:
// 1. App 최상위 (전체 앱 크래시 방지)
// 2. 각 페이지 단위 (페이지별 격리)
```

### Pattern: SSE Connection Management
**적용 NFR**: NFR-FE-REL-02
```typescript
// useSSE 훅 내부 패턴
function useSSE(storeId: number) {
  const eventSourceRef = useRef<EventSource | null>(null);
  const retryCountRef = useRef(0);
  const MAX_RETRIES = 5;
  const RETRY_INTERVAL = 3000;

  const connect = useCallback(() => {
    const url = `${API_URL}/api/stores/${storeId}/orders/stream`;
    const es = new EventSource(url);

    es.onopen = () => {
      retryCountRef.current = 0;  // 연결 성공 시 카운터 리셋
      setStatus('connected');
    };

    es.onmessage = (event) => {
      const data = JSON.parse(event.data);
      onEvent(data);
    };

    es.onerror = () => {
      es.close();
      if (retryCountRef.current < MAX_RETRIES) {
        setStatus('reconnecting');
        retryCountRef.current++;
        setTimeout(connect, RETRY_INTERVAL);
      } else {
        setStatus('disconnected');
      }
    };

    eventSourceRef.current = es;
  }, [storeId]);

  // 컴포넌트 언마운트 시 연결 해제
  useEffect(() => {
    return () => eventSourceRef.current?.close();
  }, []);

  // 브라우저 탭 활성화 시 연결 상태 확인
  useEffect(() => {
    const handleVisibility = () => {
      if (document.visibilityState === 'visible') {
        if (eventSourceRef.current?.readyState === EventSource.CLOSED) {
          retryCountRef.current = 0;
          connect();
        }
      }
    };
    document.addEventListener('visibilitychange', handleVisibility);
    return () => document.removeEventListener('visibilitychange', handleVisibility);
  }, [connect]);
}
```

---

## 4. 상태 관리 패턴

### Pattern: Zustand Store with Persist (장바구니 + 인증만)
**적용 NFR**: NFR-FE-REL-04
```typescript
// CartStore - localStorage persist
const useCartStore = create<CartStore>()(
  persist(
    (set, get) => ({
      items: [],
      isPanelOpen: false,

      totalAmount: () => get().items.reduce(
        (sum, item) => sum + item.unitPrice * item.quantity, 0
      ),
      totalQuantity: () => get().items.reduce(
        (sum, item) => sum + item.quantity, 0
      ),

      addItem: (menu) => set((state) => {
        const existing = state.items.find(i => i.menuId === menu.id);
        if (existing) {
          return {
            items: state.items.map(i =>
              i.menuId === menu.id
                ? { ...i, quantity: Math.min(i.quantity + 1, 99) }
                : i
            )
          };
        }
        return {
          items: [...state.items, {
            menuId: menu.id,
            menuName: menu.name,
            unitPrice: menu.price,
            quantity: 1,
            imageUrl: menu.imageUrl,
          }]
        };
      }),
      // ... 기타 액션
    }),
    {
      name: 'cart-storage',  // localStorage key
      partialize: (state) => ({ items: state.items }),  // items만 persist
    }
  )
);

// AuthStore - localStorage persist
const useAuthStore = create<AuthStore>()(
  persist(
    (set) => ({
      isAuthenticated: false,
      authInfo: null,
      // ... 액션
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({ authInfo: state.authInfo }),
    }
  )
);

// MenuStore - persist 없음 (매번 API 조회)
const useMenuStore = create<MenuStore>()((set) => ({
  categories: [],
  menus: [],
  // ... 액션
}));

// OrderStore - persist 없음 (매번 API 조회 + SSE)
const useOrderStore = create<OrderStore>()((set) => ({
  orders: [],
  // ... 액션
}));
```

### Pattern: Toast Notification System
**적용 NFR**: NFR-FE-USE-03
```typescript
// ToastStore - 전역 토스트 관리
const useToastStore = create<ToastStore>()((set) => ({
  toasts: [],
  showToast: (message, type = 'info', duration = 3000) => {
    const id = Date.now();
    set((state) => ({
      toasts: [...state.toasts, { id, message, type, duration }]
    }));
    setTimeout(() => {
      set((state) => ({
        toasts: state.toasts.filter(t => t.id !== id)
      }));
    }, duration);
  },
}));

// Axios 인터셉터에서 사용:
// showToast('네트워크 연결을 확인해주세요', 'error');
// showToast('장바구니에 추가되었습니다', 'success');
```

---

## 5. 장바구니 슬라이드 패널 패턴

### Pattern: Auto-Close Timer
**적용 NFR**: Functional Design BR-CART-04
```typescript
function useCartAutoClose(isOpen: boolean, onClose: () => void, timeout = 3000) {
  const timerRef = useRef<NodeJS.Timeout | null>(null);

  const resetTimer = useCallback(() => {
    if (timerRef.current) clearTimeout(timerRef.current);
    if (isOpen) {
      timerRef.current = setTimeout(onClose, timeout);
    }
  }, [isOpen, onClose, timeout]);

  // 패널 열릴 때 타이머 시작
  useEffect(() => {
    if (isOpen) resetTimer();
    return () => { if (timerRef.current) clearTimeout(timerRef.current); };
  }, [isOpen, resetTimer]);

  // 패널 내 이벤트 시 타이머 리셋
  return { resetTimer };
}

// 사용:
// const { resetTimer } = useCartAutoClose(isPanelOpen, closePanel, 3000);
// <div onMouseMove={resetTimer} onTouchStart={resetTimer}>
```

---

## Security Extension Compliance

| Rule | 적용 여부 | 설계 패턴 |
|---|---|---|
| SECURITY-04 | Compliant | XSS Prevention 패턴 (CSP 준수, inline 금지) |
| SECURITY-05 | Compliant | Zod Input Validation 패턴 |
| SECURITY-08 | Compliant | Authentication Flow 패턴 (AuthGuard, 토큰 검증) |
| SECURITY-09 | Compliant | 프로덕션 소스맵 비활성화, 에러 메시지 내부 정보 미노출 |
| SECURITY-10 | Compliant | lock file, 정확한 버전 고정 |
| SECURITY-11 | Compliant | 인증 모듈 분리, 입력 검증 + 서버 인증 이중 방어 |
| SECURITY-12 | Compliant | 비밀번호 마스킹, 토큰 만료 처리, 자동 재인증 |
| SECURITY-13 | Compliant | 외부 CDN SRI, 안전한 JSON 파싱 |
| SECURITY-15 | Compliant | Error Boundary, Axios 인터셉터 글로벌 에러 처리 |
