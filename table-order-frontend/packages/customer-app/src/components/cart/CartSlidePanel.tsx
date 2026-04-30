import { useState } from 'react';
import { X, Trash2, ShoppingBag } from 'lucide-react';
import { formatPrice } from '@table-order/shared';
import { CartItemComponent } from './CartItem';
import { ConfirmDialog } from '../common/ConfirmDialog';
import { useCartAutoClose } from '../../hooks/useCartAutoClose';
import type { CartItem, Menu } from '../../types';

interface CartSlidePanelProps {
  isOpen: boolean;
  onClose: () => void;
  items: CartItem[];
  totalAmount: number;
  onRemoveItem: (menuId: number) => void;
  onIncrement: (menuId: number) => void;
  onDecrement: (menuId: number) => void;
  onClearAll: () => void;
  onOrder: () => void;
  onAddItem: (menu: Menu) => void;
}

export function CartSlidePanel({
  isOpen,
  onClose,
  items,
  totalAmount,
  onRemoveItem,
  onIncrement,
  onDecrement,
  onClearAll,
  onOrder,
}: CartSlidePanelProps) {
  const [showClearConfirm, setShowClearConfirm] = useState(false);
  const { resetTimer } = useCartAutoClose(isOpen, onClose, 3000);

  const handleClearAll = () => {
    onClearAll();
    setShowClearConfirm(false);
  };

  return (
    <>
      <div
        className={`absolute top-0 right-0 h-full w-80 bg-white shadow-xl border-l border-gray-200 z-40 transition-transform duration-300 flex flex-col ${
          isOpen ? 'translate-x-0' : 'translate-x-full'
        }`}
        onMouseMove={resetTimer}
        onTouchStart={resetTimer}
        data-testid="cart-slide-panel"
      >
        {/* Header */}
        <div className="flex items-center justify-between px-4 py-3 border-b border-gray-200">
          <h2 className="font-semibold text-gray-800 flex items-center gap-2">
            <ShoppingBag className="w-5 h-5" />
            장바구니
          </h2>
          <div className="flex items-center gap-1">
            {items.length > 0 && (
              <button
                onClick={() => setShowClearConfirm(true)}
                className="p-2 text-gray-400 hover:text-red-500 min-w-touch min-h-touch flex items-center justify-center"
                aria-label="전체 비우기"
                data-testid="cart-clear-all-button"
              >
                <Trash2 className="w-4 h-4" />
              </button>
            )}
            <button
              onClick={onClose}
              className="p-2 text-gray-400 hover:text-gray-600 min-w-touch min-h-touch flex items-center justify-center"
              aria-label="장바구니 닫기"
              data-testid="cart-close-button"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
        </div>

        {/* Items */}
        <div className="flex-1 overflow-y-auto px-4">
          {items.length === 0 ? (
            <div className="flex flex-col items-center justify-center h-full text-gray-400">
              <ShoppingBag className="w-12 h-12 mb-2" />
              <p className="text-sm">장바구니가 비어있습니다</p>
            </div>
          ) : (
            items.map((item) => (
              <CartItemComponent
                key={item.menuId}
                item={item}
                onIncrement={() => onIncrement(item.menuId)}
                onDecrement={() => onDecrement(item.menuId)}
                onRemove={() => onRemoveItem(item.menuId)}
              />
            ))
          )}
        </div>

        {/* Footer */}
        {items.length > 0 && (
          <div className="px-4 py-3 border-t border-gray-200">
            <div className="flex justify-between items-center mb-3">
              <span className="text-sm font-medium text-gray-600">총 금액</span>
              <span className="text-lg font-bold text-primary-700">{formatPrice(totalAmount)}</span>
            </div>
            <button
              onClick={() => { onClose(); onOrder(); }}
              className="w-full px-4 py-3 bg-primary-600 text-white rounded-lg font-medium min-h-touch hover:bg-primary-700"
              data-testid="cart-order-button"
            >
              주문하기
            </button>
          </div>
        )}
      </div>

      <ConfirmDialog
        isOpen={showClearConfirm}
        title="장바구니 비우기"
        message="장바구니의 모든 메뉴를 삭제하시겠습니까?"
        onConfirm={handleClearAll}
        onCancel={() => setShowClearConfirm(false)}
      />
    </>
  );
}
