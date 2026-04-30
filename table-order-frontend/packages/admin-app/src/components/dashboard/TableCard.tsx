import { formatPrice, formatDate } from '@table-order/shared';
import type { Order, TableInfo } from '@table-order/shared';
import { StatusBadge } from './StatusBadge';

interface TableCardProps {
  table: TableInfo;
  orders: Order[];
  isNew?: boolean;
  onTableClick: (tableId: number) => void;
  onCompleteTable: (tableId: number) => void;
}

export function TableCard({ table, orders, isNew, onTableClick, onCompleteTable }: TableCardProps) {
  const totalAmount = orders.reduce((sum, o) => sum + o.totalAmount, 0);
  const recentOrders = orders.slice(0, 3);
  const hasActiveOrders = orders.length > 0;

  return (
    <div
      className={`rounded-lg border p-4 cursor-pointer transition-all hover:shadow-md ${
        isNew ? 'border-orange-400 bg-orange-50 animate-pulse' :
        hasActiveOrders ? 'border-blue-200 bg-white' : 'border-gray-200 bg-gray-50'
      }`}
      onClick={() => onTableClick(table.id)}
      data-testid={`table-card-${table.tableNumber}`}
    >
      <div className="flex items-center justify-between mb-3">
        <h3 className="text-lg font-bold" data-testid={`table-card-number-${table.tableNumber}`}>
          테이블 {table.tableNumber}
        </h3>
        {hasActiveOrders && (
          <button
            onClick={(e) => { e.stopPropagation(); onCompleteTable(table.id); }}
            className="rounded bg-gray-500 px-2 py-1 text-xs text-white hover:bg-gray-600 min-h-[32px]"
            data-testid={`table-card-complete-button-${table.tableNumber}`}
          >
            이용 완료
          </button>
        )}
      </div>

      <div className="text-xl font-bold text-blue-600 mb-3" data-testid={`table-card-total-${table.tableNumber}`}>
        {formatPrice(totalAmount)}
      </div>

      {recentOrders.length > 0 ? (
        <div className="space-y-2">
          {recentOrders.map(order => (
            <div key={order.id} className="flex items-center justify-between text-sm border-t pt-1">
              <div className="flex items-center gap-2">
                <span className="text-gray-500">{order.orderNumber}</span>
                <StatusBadge status={order.status} />
              </div>
              <span className="text-gray-600">{formatPrice(order.totalAmount)}</span>
            </div>
          ))}
          {orders.length > 3 && (
            <p className="text-xs text-gray-400 text-center">+{orders.length - 3}건 더보기</p>
          )}
        </div>
      ) : (
        <p className="text-sm text-gray-400">주문 없음</p>
      )}

      <div className="mt-2 text-xs text-gray-400">
        {orders.length > 0 && `최근: ${formatDate(orders[0].createdAt)}`}
      </div>
    </div>
  );
}
