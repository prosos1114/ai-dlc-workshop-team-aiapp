# Component Methods

## Controllers

### AuthController
| Method | HTTP | Path | Input | Output | Purpose |
|---|---|---|---|---|---|
| login | POST | /api/admin/auth/login | LoginRequest(storeCode, username, password) | TokenResponse(token, expiresIn) | 관리자 로그인 |
| register | POST | /api/admin/auth/register | RegisterRequest(storeCode, username, password) | AdminResponse | 관리자 회원가입 |
| tableLogin | POST | /api/table/auth/login | TableLoginRequest(storeCode, tableNumber, password) | TableTokenResponse(token, storeId, tableId) | 테이블 태블릿 인증 |

### StoreController
| Method | HTTP | Path | Input | Output | Purpose |
|---|---|---|---|---|---|
| createStore | POST | /api/stores | StoreCreateRequest(name, code) | StoreResponse | 매장 등록 |
| getStore | GET | /api/stores/{storeCode} | storeCode | StoreResponse | 매장 정보 조회 |

### TableController
| Method | HTTP | Path | Input | Output | Purpose |
|---|---|---|---|---|---|
| createTable | POST | /api/stores/{storeId}/tables | TableCreateRequest(number, password) | TableResponse | 테이블 생성 |
| getTables | GET | /api/stores/{storeId}/tables | storeId | List<TableResponse> | 테이블 목록 조회 |
| updateTable | PUT | /api/stores/{storeId}/tables/{tableId} | TableUpdateRequest | TableResponse | 테이블 수정 |
| completeTable | POST | /api/stores/{storeId}/tables/{tableId}/complete | tableId | void | 이용 완료 처리 |

### MenuController
| Method | HTTP | Path | Input | Output | Purpose |
|---|---|---|---|---|---|
| getMenus | GET | /api/stores/{storeId}/menus | storeId, categoryId? | List<MenuResponse> | 메뉴 목록 조회 |
| getMenu | GET | /api/stores/{storeId}/menus/{menuId} | menuId | MenuResponse | 메뉴 상세 조회 |
| createMenu | POST | /api/stores/{storeId}/menus | MenuCreateRequest | MenuResponse | 메뉴 등록 |
| updateMenu | PUT | /api/stores/{storeId}/menus/{menuId} | MenuUpdateRequest | MenuResponse | 메뉴 수정 |
| deleteMenu | DELETE | /api/stores/{storeId}/menus/{menuId} | menuId | void | 메뉴 삭제 |
| updateMenuOrder | PUT | /api/stores/{storeId}/menus/order | List<MenuOrderRequest> | void | 메뉴 순서 변경 |
| getCategories | GET | /api/stores/{storeId}/categories | storeId | List<CategoryResponse> | 카테고리 목록 |
| createCategory | POST | /api/stores/{storeId}/categories | CategoryCreateRequest | CategoryResponse | 카테고리 생성 |
| uploadImage | POST | /api/stores/{storeId}/menus/{menuId}/image | MultipartFile | ImageResponse(url) | 이미지 업로드 |

### OrderController
| Method | HTTP | Path | Input | Output | Purpose |
|---|---|---|---|---|---|
| createOrder | POST | /api/stores/{storeId}/tables/{tableId}/orders | OrderCreateRequest | OrderResponse | 주문 생성 |
| getOrders | GET | /api/stores/{storeId}/tables/{tableId}/orders | tableId, sessionId | List<OrderResponse> | 현재 세션 주문 조회 |
| getAllOrders | GET | /api/stores/{storeId}/orders | storeId, status? | List<OrderResponse> | 매장 전체 주문 조회 |
| updateOrderStatus | PATCH | /api/stores/{storeId}/orders/{orderId}/status | StatusUpdateRequest(status) | OrderResponse | 주문 상태 변경 |
| deleteOrder | DELETE | /api/stores/{storeId}/orders/{orderId} | orderId | void | 주문 삭제 |
| getOrderHistory | GET | /api/stores/{storeId}/tables/{tableId}/history | tableId, startDate?, endDate? | List<OrderHistoryResponse> | 과거 주문 내역 |

### SSEController
| Method | HTTP | Path | Input | Output | Purpose |
|---|---|---|---|---|---|
| subscribe | GET | /api/stores/{storeId}/orders/stream | storeId | SseEmitter | SSE 구독 |

---

## Services

### AuthService
| Method | Input | Output | Purpose |
|---|---|---|---|
| authenticateAdmin | storeCode, username, password | TokenResponse | 관리자 인증 |
| registerAdmin | storeCode, username, password | Admin | 관리자 등록 |
| authenticateTable | storeCode, tableNumber, password | TableTokenResponse | 테이블 인증 |
| validateToken | token | Claims | 토큰 검증 |
| checkLoginAttempts | username, storeCode | boolean | 로그인 시도 제한 확인 |

### StoreService
| Method | Input | Output | Purpose |
|---|---|---|---|
| createStore | name, code | Store | 매장 생성 |
| getStoreByCode | code | Store | 매장 조회 |
| validateStoreCode | code | boolean | 매장 코드 유효성 |

### TableService
| Method | Input | Output | Purpose |
|---|---|---|---|
| createTable | storeId, number, password | Table | 테이블 생성 |
| getTablesByStore | storeId | List<Table> | 테이블 목록 |
| updateTable | tableId, request | Table | 테이블 수정 |
| completeTableSession | tableId | void | 이용 완료 (세션 종료, 이력 이동) |
| getOrCreateSession | tableId | TableSession | 현재 세션 조회/생성 |

### MenuService
| Method | Input | Output | Purpose |
|---|---|---|---|
| getMenusByStore | storeId, categoryId? | List<Menu> | 메뉴 목록 |
| getMenu | menuId | Menu | 메뉴 상세 |
| createMenu | storeId, request | Menu | 메뉴 생성 |
| updateMenu | menuId, request | Menu | 메뉴 수정 |
| deleteMenu | menuId | void | 메뉴 삭제 |
| updateMenuOrder | List<MenuOrderRequest> | void | 순서 변경 |
| uploadImage | menuId, file | String(url) | 이미지 업로드 |

### OrderService
| Method | Input | Output | Purpose |
|---|---|---|---|
| createOrder | storeId, tableId, request | Order | 주문 생성 |
| getOrdersBySession | tableId, sessionId | List<Order> | 세션별 주문 |
| getAllOrdersByStore | storeId, status? | List<Order> | 매장 전체 주문 |
| updateOrderStatus | orderId, status | Order | 상태 변경 |
| deleteOrder | orderId | void | 주문 삭제 |
| getOrderHistory | tableId, startDate?, endDate? | List<OrderHistory> | 과거 이력 |

### SSEService
| Method | Input | Output | Purpose |
|---|---|---|---|
| subscribe | storeId | SseEmitter | SSE 구독 등록 |
| publishOrderEvent | storeId, event | void | 주문 이벤트 발행 |
| removeEmitter | storeId, emitter | void | 연결 해제 |
