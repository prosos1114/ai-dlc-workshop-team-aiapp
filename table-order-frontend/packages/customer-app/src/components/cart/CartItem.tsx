import React from 'react';
import { Plus, Minus, Trash2 } from 'lucide-react';
import { formatPrice } from '@table-order/shared';
import type { CartItem as CartItemType } from '../../types';

interface CartItemProps {
  item: CartItemType;
  onIncrement: () => void;
  onDecrement: () => void;
  onRemove: () => void;
}

export const CartItemComponent = React.memo(function CartItemComponent({
  item,
  onIncrement,
  onDecrement,
  onRemove,
}: CartItemProps) {
  return (
    <div className="flex items-center justify-between py-3 border-b border-gray-100" data-testid={`cart-item-${item.menuId}`}>
      <div className="flex-1 min-w-0">
        <p className="text-sm font-medium text-gray-800 truncate">{item.menuName}</p>
        <p className="text-sm text-primary-700 font-semibold">{formatPrice(item.unitPrice * item.quantity)}</p>
      </div>
      <div className="flex items-center gap-2 ml-3">
        <button
          onClick={onDecrement}
          className="p-2 rounded-full bg-gray-100 hover:bg-gray-200 min-w-touch min-h-touch flex items-center justify-center"
          aria-label={`${item.menuName} 수량 감소`}
          data-testid={`cart-item-decrement-${item.menuId}`}
        >
          <Minus className="w-4 h-4" />
        </button>
        <span className="w-8 text-center text-sm font-medium" data-testid={`cart-item-quantity-${item.menuId}`}>
          {item.quantity}
        </span>
        <button
          onClick={onIncrement}
          className="p-2 rounded-full bg-gray-100 hover:bg-gray-200 min-w-touch min-h-touch flex items-center justify-center"
          aria-label={`${item.menuName} 수량 증가`}
          data-testid={`cart-item-increment-${item.menuId}`}
        >
          <Plus className="w-4 h-4" />
        </button>
        <button
          onClick={onRemove}
          className="p-2 rounded-full text-red-400 hover:bg-red-50 min-w-touch min-h-touch flex items-center justify-center"
          aria-label={`${item.menuName} 삭제`}
          data-testid={`cart-item-remove-${item.menuId}`}
        >
          <Trash2 className="w-4 h-4" />
        </button>
      </div>
    </div>
  );
});
