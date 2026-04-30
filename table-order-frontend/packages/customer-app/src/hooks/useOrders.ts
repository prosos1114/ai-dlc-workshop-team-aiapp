import { useCallback } from 'react';
import { useOrderStore } from '../store/orderStore';
import { useAuthStore } from '../store/authStore';
import { useCartStore } from '../store/cartStore';
import type { OrderResult } from '../types';

export function useOrders() {
  const authInfo = useAuthStore((s) => s.authInfo);
  const clearCart = useCartStore((s) => s.clearCart);
  const store = useOrderStore();

  const createOrder = useCallback(async (): Promise<OrderResult> => {
    if (!authInfo) {
      return { success: false, error: { type: 'auth', message: '인증 정보가 없습니다', retryable: false } };
    }
    const items = useCartStore.getState().items;
    if (items.length === 0) {
      return { success: false, error: { type: 'unknown', message: '장바구니가 비어있습니다', retryable: false } };
    }
    const result = await store.createOrder(authInfo.storeId, authInfo.tableId, items);
    if (result.success) {
      clearCart();
    }
    return result;
  }, [authInfo, clearCart, store]);

  const fetchOrders = useCallback(async () => {
    if (!authInfo) return;
    await store.fetchOrders(authInfo.storeId, authInfo.tableId);
  }, [authInfo, store]);

  return {
    orders: store.orders,
    isLoading: store.isLoading,
    isSubmitting: store.isSubmitting,
    error: store.error,
    failureCount: store.failureCount,
    lastOrderResult: store.lastOrderResult,
    createOrder,
    fetchOrders,
    updateOrderStatus: store.updateOrderStatus,
    removeOrder: store.removeOrder,
    clearLastResult: store.clearLastResult,
  };
}
