// Re-export shared types
export type {
  Category,
  Menu,
  Order,
  OrderItem,
  OrderStatus,
  CartItem,
  ApiResponse,
  ApiErrorResponse,
  TableLoginRequest,
  TableTokenResponse,
  OrderCreateRequest,
  OrderItemRequest,
  SSEEventType,
  SSEEvent,
} from '@table-order/shared';

// Customer-app specific types

export interface QRCodeData {
  storeCode: string;
  totalTables: number;
}

export interface AuthInfo {
  token: string;
  storeId: number;
  tableId: number;
  tableNumber: number;
  storeCode: string;
}

export interface OrderResult {
  success: boolean;
  orderNumber?: string;
  totalAmount?: number;
  createdAt?: string;
  error?: AppError;
}

export interface AppError {
  type: 'network' | 'server' | 'auth' | 'unknown';
  message: string;
  retryable: boolean;
}

export interface ToastItem {
  id: number;
  message: string;
  type: 'success' | 'error' | 'info';
  duration: number;
}

export type SSEConnectionStatus = 'connected' | 'reconnecting' | 'disconnected';
