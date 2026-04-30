export const ORDER_STATUS = {
  PENDING: 'PENDING',
  PREPARING: 'PREPARING',
  COMPLETED: 'COMPLETED',
} as const;

export const SESSION_STATUS = {
  ACTIVE: 'ACTIVE',
  COMPLETED: 'COMPLETED',
} as const;

export const TOKEN_KEY = 'token';
export const STORE_ID_KEY = 'storeId';
export const TABLE_ID_KEY = 'tableId';
export const TABLE_NUMBER_KEY = 'tableNumber';
export const CART_KEY = 'cart';

export const MAX_QUANTITY = 99;
export const MIN_QUANTITY = 1;

export const ORDER_SUCCESS_REDIRECT_DELAY = 5000; // 5 seconds

export const SSE_RECONNECT_DELAY = 3000; // 3 seconds
export const SSE_HEARTBEAT_INTERVAL = 15000; // 15 seconds
