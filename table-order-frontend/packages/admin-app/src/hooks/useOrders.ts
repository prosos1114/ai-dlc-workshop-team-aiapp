import { useState, useCallback } from 'react';
import { apiClient, API } from '@table-order/shared';
import type { Order, OrderStatus, ApiResponse, PageResponse, OrderHistory } from '@table-order/shared';

export function useOrders(storeId: number | null) {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchOrders = useCallback(async (status?: OrderStatus) => {
    if (!storeId) return;
    setLoading(true);
    setError(null);
    try {
      const params = status ? { status } : {};
      const { data } = await apiClient.get<ApiResponse<Order[]>>(
        API.STORE_ORDERS(storeId), { params }
      );
      setOrders(data.data);
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })
        ?.response?.data?.message ?? '주문 목록을 불러오는데 실패했습니다';
      setError(message);
    } finally {
      setLoading(false);
    }
  }, [storeId]);

  const updateOrderStatus = useCallback(async (orderId: number, status: OrderStatus) => {
    if (!storeId) return;
    try {
      const { data } = await apiClient.patch<ApiResponse<Order>>(
        API.ORDER_STATUS(storeId, orderId), { status }
      );
      setOrders(prev => prev.map(o => o.id === orderId ? data.data : o));
      return data.data;
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })
        ?.response?.data?.message ?? '주문 상태 변경에 실패했습니다';
      setError(message);
      throw err;
    }
  }, [storeId]);

  const deleteOrder = useCallback(async (orderId: number) => {
    if (!storeId) return;
    try {
      await apiClient.delete(API.ORDER(storeId, orderId));
      setOrders(prev => prev.filter(o => o.id !== orderId));
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })
        ?.response?.data?.message ?? '주문 삭제에 실패했습니다';
      setError(message);
      throw err;
    }
  }, [storeId]);

  const fetchOrderHistory = useCallback(async (
    tableId: number, startDate?: string, endDate?: string, page = 0, size = 20
  ) => {
    if (!storeId) return null;
    try {
      const params: Record<string, string | number> = { page, size };
      if (startDate) params.startDate = startDate;
      if (endDate) params.endDate = endDate;
      const { data } = await apiClient.get<ApiResponse<PageResponse<OrderHistory>>>(
        API.TABLE_HISTORY(storeId, tableId), { params }
      );
      return data.data;
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })
        ?.response?.data?.message ?? '과거 내역을 불러오는데 실패했습니다';
      setError(message);
      return null;
    }
  }, [storeId]);

  const addOrder = useCallback((order: Order) => {
    setOrders(prev => [order, ...prev]);
  }, []);

  const removeOrder = useCallback((orderId: number) => {
    setOrders(prev => prev.filter(o => o.id !== orderId));
  }, []);

  const updateOrder = useCallback((order: Order) => {
    setOrders(prev => prev.map(o => o.id === order.id ? order : o));
  }, []);

  return {
    orders, loading, error,
    fetchOrders, updateOrderStatus, deleteOrder, fetchOrderHistory,
    addOrder, removeOrder, updateOrder
  };
}
