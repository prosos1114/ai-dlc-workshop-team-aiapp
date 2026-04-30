// Store
export interface Store {
  id: number;
  name: string;
  code: string;
  createdAt: string;
}

// Admin
export interface Admin {
  id: number;
  storeId: number;
  username: string;
  createdAt: string;
}

// Table
export interface TableInfo {
  id: number;
  storeId: number;
  tableNumber: number;
  createdAt: string;
}

// Category
export interface Category {
  id: number;
  storeId: number;
  name: string;
  displayOrder: number;
}

// Menu
export interface Menu {
  id: number;
  storeId: number;
  categoryId: number;
  name: string;
  price: number;
  description: string | null;
  imageUrl: string | null;
  displayOrder: number;
}

// Order
export type OrderStatus = 'PENDING' | 'PREPARING' | 'COMPLETED';

export interface OrderItem {
  id: number;
  menuId: number;
  menuName: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
}

export interface Order {
  id: number;
  storeId: number;
  tableId: number;
  sessionId: number;
  orderNumber: string;
  status: OrderStatus;
  totalAmount: number;
  items: OrderItem[];
  createdAt: string;
}

export interface OrderHistory {
  id: number;
  storeId: number;
  tableId: number;
  sessionId: number;
  orderNumber: string;
  totalAmount: number;
  items: string;
  orderedAt: string;
  completedAt: string;
}

// Cart (client-side only)
export interface CartItem {
  menuId: number;
  menuName: string;
  unitPrice: number;
  quantity: number;
}

// API Responses
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string | null;
}

export interface ApiErrorResponse {
  code: string;
  message: string;
  timestamp: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

// Auth
export interface LoginRequest {
  storeCode: string;
  username: string;
  password: string;
}

export interface TableLoginRequest {
  storeCode: string;
  tableNumber: number;
  password: string;
}

export interface TokenResponse {
  token: string;
  expiresIn: number;
}

export interface TableTokenResponse {
  token: string;
  storeId: number;
  tableId: number;
}

export interface RegisterRequest {
  storeCode: string;
  username: string;
  password: string;
}

// Order Create
export interface OrderCreateRequest {
  items: OrderItemRequest[];
}

export interface OrderItemRequest {
  menuId: number;
  quantity: number;
}

// SSE Events
export type SSEEventType = 'ORDER_CREATED' | 'ORDER_STATUS_CHANGED' | 'ORDER_DELETED' | 'TABLE_COMPLETED';

export interface SSEEvent {
  type: SSEEventType;
  data: unknown;
}
