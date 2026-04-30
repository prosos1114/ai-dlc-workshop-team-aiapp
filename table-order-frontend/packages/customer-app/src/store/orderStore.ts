import { create } from 'zustand';
import type { Order, OrderStatus, OrderResult } from '../types';
import { createOrder as createOrderApi, fetchOrders as fetchOrdersApi } from '../api/order';
import type { CartItem } from '@table-order/shared';

interface OrderState {
  orders: Order[];
  isLoading: boolean;
  isSubmitting: boolean;
  error: string | null;
  failureCount: number;
  lastOrderResult: OrderResult | null;
}

interface OrderActions {
  createOrder: (storeId: number, tableId: number, items: CartItem[]) => Promise<OrderResult>;
  fetchOrders: (storeId: number, tableId: number) => Promise<void>;
  updateOrderStatus: (orderId: number, status: OrderStatus) => void;
  removeOrder: (orderId: number) => void;
  clearLastResult: () => void;
  resetFailureCount: () => void;
}

export type OrderStore = OrderState & OrderActions;

export const useOrderStore = create<OrderStore>()((set, get) => ({
  orders: [],
  isLoading: false,
  isSubmitting: false,
  error: null,
  failureCount: 0,
  lastOrderResult: null,

  createOrder: async (storeId, tableId, items) => {
    set({ isSubmitting: true, error: null });
    try {
      const order = await createOrderApi(storeId, tableId, {
        items: items.map((item) => ({
          menuId: item.menuId,
          quantity: item.quantity,
        })),
      });
      const result: OrderResult = {
        success: true,
        orderNumber: order.orderNumber,
        totalAmount: order.totalAmount,
        createdAt: order.createdAt,
      };
      set({ isSubmitting: false, lastOrderResult: result, failureCount: 0 });
      return result;
    } catch (err) {
      const failureCount = get().failureCount + 1;
      const result: OrderResult = {
        success: false,
        error: {
          type: 'unknown',
          message: '주문 처리 중 오류가 발생했습니다',
          retryable: true,
        },
      };
      set({ isSubmitting: false, lastOrderResult: result, failureCount });
      return result;
    }
  },

  fetchOrders: async (storeId, tableId) => {
    set({ isLoading: true, error: null });
    try {
      const orders = await fetchOrdersApi(storeId, tableId);
      set({ orders, isLoading: false });
    } catch {
      set({ error: '주문 내역을 불러올 수 없습니다', isLoading: false });
    }
  },

  updateOrderStatus: (orderId, status) =>
    set((state) => ({
      orders: state.orders.map((o) =>
        o.id === orderId ? { ...o, status } : o
      ),
    })),

  removeOrder: (orderId) =>
    set((state) => ({
      orders: state.orders.filter((o) => o.id !== orderId),
    })),

  clearLastResult: () => set({ lastOrderResult: null }),
  resetFailureCount: () => set({ failureCount: 0 }),
}));
