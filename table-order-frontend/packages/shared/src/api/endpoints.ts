export const API = {
  // Admin Auth
  ADMIN_LOGIN: '/api/admin/auth/login',
  ADMIN_REGISTER: '/api/admin/auth/register',

  // Table Auth
  TABLE_LOGIN: '/api/table/auth/login',

  // Store
  STORES: '/api/stores',
  STORE_BY_CODE: (code: string) => `/api/stores/${code}` as const,

  // Tables
  TABLES: (storeId: number) => `/api/stores/${storeId}/tables` as const,
  TABLE: (storeId: number, tableId: number) => `/api/stores/${storeId}/tables/${tableId}` as const,
  TABLE_COMPLETE: (storeId: number, tableId: number) =>
    `/api/stores/${storeId}/tables/${tableId}/complete` as const,

  // Categories
  CATEGORIES: (storeId: number) => `/api/stores/${storeId}/categories` as const,

  // Menus
  MENUS: (storeId: number) => `/api/stores/${storeId}/menus` as const,
  MENU: (storeId: number, menuId: number) => `/api/stores/${storeId}/menus/${menuId}` as const,
  MENU_ORDER: (storeId: number) => `/api/stores/${storeId}/menus/order` as const,
  MENU_IMAGE: (storeId: number, menuId: number) =>
    `/api/stores/${storeId}/menus/${menuId}/image` as const,

  // Orders (Customer)
  TABLE_ORDERS: (storeId: number, tableId: number) =>
    `/api/stores/${storeId}/tables/${tableId}/orders` as const,

  // Orders (Admin)
  STORE_ORDERS: (storeId: number) => `/api/stores/${storeId}/orders` as const,
  ORDER_STATUS: (storeId: number, orderId: number) =>
    `/api/stores/${storeId}/orders/${orderId}/status` as const,
  ORDER: (storeId: number, orderId: number) =>
    `/api/stores/${storeId}/orders/${orderId}` as const,

  // Order History
  TABLE_HISTORY: (storeId: number, tableId: number) =>
    `/api/stores/${storeId}/tables/${tableId}/history` as const,

  // SSE
  ORDER_STREAM: (storeId: number) => `/api/stores/${storeId}/orders/stream` as const,
} as const;
