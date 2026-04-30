import { useCartStore } from '../store/cartStore';
import { useToastStore } from '../store/toastStore';
import type { Menu } from '../types';
import { MAX_QUANTITY } from '@table-order/shared';

export function useCart() {
  const store = useCartStore();
  const showToast = useToastStore((s) => s.showToast);

  const addItem = (menu: Menu) => {
    const existing = store.items.find((i) => i.menuId === menu.id);
    if (existing && existing.quantity >= MAX_QUANTITY) {
      showToast('최대 수량입니다', 'info');
      return;
    }
    store.addItem(menu);
    showToast('장바구니에 추가되었습니다', 'success');
  };

  return {
    items: store.items,
    isPanelOpen: store.isPanelOpen,
    totalAmount: store.getTotalAmount(),
    totalQuantity: store.getTotalQuantity(),
    addItem,
    removeItem: store.removeItem,
    incrementQuantity: store.incrementQuantity,
    decrementQuantity: store.decrementQuantity,
    clearCart: store.clearCart,
    openPanel: store.openPanel,
    closePanel: store.closePanel,
  };
}
