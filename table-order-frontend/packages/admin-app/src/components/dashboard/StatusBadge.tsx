import { formatOrderStatus, getStatusColor } from '@table-order/shared';
import type { OrderStatus } from '@table-order/shared';

interface StatusBadgeProps {
  status: OrderStatus;
}

export function StatusBadge({ status }: StatusBadgeProps) {
  return (
    <span
      className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${getStatusColor(status)}`}
      data-testid={`status-badge-${status.toLowerCase()}`}
    >
      {formatOrderStatus(status)}
    </span>
  );
}
