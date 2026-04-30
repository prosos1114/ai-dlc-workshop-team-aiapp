import { useEffect, useCallback } from 'react';
import { ClipboardList } from 'lucide-react';
import { OrderCard } from '../components/order/OrderCard';
import { SSEIndicator } from '../components/order/SSEIndicator';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { useOrders } from '../hooks/useOrders';
import { useSSE } from '../hooks/useSSE';
import { useAuthStore } from '../store/authStore';
import type { SSEEvent, OrderStatus } from '../types';

export default function OrderHistoryPage() {
  const authInfo = useAuthStore((s) => s.authInfo);
  const { orders, isLoading, fetchOrders, updateOrderStatus, removeOrder } = useOrders();

  useEffect(() => {
    fetchOrders();
  }, [fetchOrders]);

  const handleSSEEvent = useCallback(
    (event: SSEEvent) => {
      const data = event.data as Record<string, unknown>;
      switch (event.type) {
        case 'ORDER_STATUS_CHANGED':
          updateOrderStatus(data.orderId as number, data.status as OrderStatus);
          break;
        case 'ORDER_DELETED':
          removeOrder(data.orderId as number);
          break;
        case 'TABLE_COMPLETED':
          fetchOrders();
          break;
        default:
          break;
      }
    },
    [updateOrderStatus, removeOrder, fetchOrders]
  );

  const { status: sseStatus } = useSSE(authInfo?.storeId ?? null, handleSSEEvent);

  if (isLoading) {
    return (
      <div className="flex-1 flex items-center justify-center">
        <LoadingSpinner message="주문 내역을 불러오는 중..." />
      </div>
    );
  }

  return (
    <div className="flex-1 overflow-y-auto bg-gray-50 p-4">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-lg font-bold text-gray-800">주문 내역</h2>
        <SSEIndicator status={sseStatus} />
      </div>

      {orders.length === 0 ? (
        <div className="flex flex-col items-center justify-center h-64 text-gray-400" data-testid="empty-orders">
          <ClipboardList className="w-12 h-12 mb-2" />
          <p>주문 내역이 없습니다</p>
        </div>
      ) : (
        <div className="space-y-3">
          {orders.map((order) => (
            <OrderCard key={order.id} order={order} />
          ))}
        </div>
      )}
    </div>
  );
}
