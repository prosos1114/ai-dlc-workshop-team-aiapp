import React, { useState } from 'react';
import { ChevronDown, ChevronUp } from 'lucide-react';
import { formatPrice, formatDate, formatOrderStatus, getStatusColor } from '@table-order/shared';
import type { Order } from '../../types';

interface OrderCardProps {
  order: Order;
}

export const OrderCard = React.memo(function OrderCard({ order }: OrderCardProps) {
  const [isExpanded, setIsExpanded] = useState(false);

  return (
    <div
      className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden"
      data-testid={`order-card-${order.id}`}
    >
      <button
        onClick={() => setIsExpanded(!isExpanded)}
        className="w-full flex items-center justify-between p-4 min-h-touch text-left"
      >
        <div className="flex-1">
          <div className="flex items-center gap-2 mb-1">
            <span className="text-sm font-semibold text-gray-800">#{order.orderNumber}</span>
            <span
              className={`px-2 py-0.5 rounded-full text-xs font-medium ${getStatusColor(order.status)}`}
              data-testid={`order-status-badge-${order.id}`}
            >
              {formatOrderStatus(order.status)}
            </span>
          </div>
          <div className="flex items-center gap-3 text-xs text-gray-500">
            <span>{formatDate(order.createdAt)}</span>
            <span className="font-semibold text-gray-700">{formatPrice(order.totalAmount)}</span>
          </div>
        </div>
        {isExpanded ? (
          <ChevronUp className="w-5 h-5 text-gray-400" />
        ) : (
          <ChevronDown className="w-5 h-5 text-gray-400" />
        )}
      </button>

      {isExpanded && (
        <div className="px-4 pb-4 border-t border-gray-100 animate-fade-in">
          <div className="divide-y divide-gray-50 mt-2">
            {order.items.map((item) => (
              <div key={item.id} className="flex justify-between py-2 text-sm">
                <div>
                  <span className="text-gray-700">{item.menuName}</span>
                  <span className="text-gray-400 ml-2">×{item.quantity}</span>
                </div>
                <span className="text-gray-600">{formatPrice(item.subtotal)}</span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
});
