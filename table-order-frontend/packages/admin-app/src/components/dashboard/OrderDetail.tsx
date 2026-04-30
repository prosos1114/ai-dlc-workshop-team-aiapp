import { formatPrice, formatDate } from '@table-order/shared';
import type { Order, OrderStatus } from '@table-order/shared';
import { StatusBadge } from './StatusBadge';
import { X, Trash2 } from 'lucide-react';

interface OrderDetailProps {
  orders: Order[];
  tableNumber: number;
  onClose: () => void;
  onStatusChange: (orderId: number, status: OrderStatus) => void;
  onDeleteOrder: (orderId: number) => void;
}

export function OrderDetail({ orders, tableNumber, onClose, onStatusChange, onDeleteOrder }: OrderDetailProps) {
  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50" data-testid="order-detail-modal">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-2xl max-h-[80vh] overflow-hidden">
        <div className="flex items-center justify-between p-4 border-b">
          <h2 className="text-lg font-bold">테이블 {tableNumber} - 주문 상세</h2>
          <button onClick={onClose} className="p-1 hover:bg-gray-100 rounded min-w-[44px] min-h-[44px] flex items-center justify-center"
            data-testid="order-detail-close-button">
            <X size={20} />
          </button>
        </div>

        <div className="overflow-y-auto p-4 space-y-4" style={{ maxHeight: 'calc(80vh - 60px)' }}>
          {orders.length === 0 ? (
            <p className="text-center text-gray-400 py-8">주문이 없습니다</p>
          ) : (
            orders.map(order => (
              <div key={order.id} className="border rounded-lg p-4" data-testid={`order-detail-item-${order.id}`}>
                <div className="flex items-center justify-between mb-2">
                  <div className="flex items-center gap-2">
                    <span className="font-medium">{order.orderNumber}</span>
                    <StatusBadge status={order.status} />
                  </div>
                  <div className="flex items-center gap-2">
                    {order.status === 'PENDING' && (
                      <button
                        onClick={() => onStatusChange(order.id, 'PREPARING')}
                        className="rounded bg-blue-500 px-3 py-1 text-sm text-white hover:bg-blue-600 min-h-[36px]"
                        data-testid={`order-status-preparing-${order.id}`}
                      >
                        준비중
                      </button>
                    )}
                    {order.status === 'PREPARING' && (
                      <button
                        onClick={() => onStatusChange(order.id, 'COMPLETED')}
                        className="rounded bg-green-500 px-3 py-1 text-sm text-white hover:bg-green-600 min-h-[36px]"
                        data-testid={`order-status-completed-${order.id}`}
                      >
                        완료
                      </button>
                    )}
                    <button
                      onClick={() => onDeleteOrder(order.id)}
                      className="rounded bg-red-500 p-1.5 text-white hover:bg-red-600 min-w-[36px] min-h-[36px] flex items-center justify-center"
                      data-testid={`order-delete-${order.id}`}
                    >
                      <Trash2 size={16} />
                    </button>
                  </div>
                </div>

                <div className="text-xs text-gray-500 mb-2">{formatDate(order.createdAt)}</div>

                <div className="space-y-1">
                  {order.items.map(item => (
                    <div key={item.id} className="flex justify-between text-sm">
                      <span>{item.menuName} x {item.quantity}</span>
                      <span className="text-gray-600">{formatPrice(item.subtotal)}</span>
                    </div>
                  ))}
                </div>

                <div className="mt-2 pt-2 border-t flex justify-end">
                  <span className="font-bold">{formatPrice(order.totalAmount)}</span>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
