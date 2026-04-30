import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { MAX_QUANTITY, MIN_QUANTITY } from '@table-order/shared';
import type { CartItem, Menu } from '../types';

interface CartState {
  items: CartItem[];
  isPanelOpen: boolean;
}

interface CartActions {
  addItem: (menu: Menu) => void;
  removeItem: (menuId: number) => void;
  incrementQuantity: (menuId: number) => void;
  decrementQuantity: (menuId: number) => void;
  clearCart: () => void;
  openPanel: () => void;
  closePanel: () => void;
  getTotalAmount: () => number;
  getTotalQuantity: () => number;
}

export type CartStore = CartState & CartActions;

export const useCartStore = create<CartStore>()(
  persist(
    (set, get) => ({
      items: [],
      isPanelOpen: false,

      addItem: (menu) =>
        set((state) => {
          const existing = state.items.find((i) => i.menuId === menu.id);
          if (existing) {
            return {
              items: state.items.map((i) =>
                i.menuId === menu.id
                  ? { ...i, quantity: Math.min(i.quantity + 1, MAX_QUANTITY) }
                  : i
              ),
              isPanelOpen: true,
            };
          }
          return {
            items: [
              ...state.items,
              {
                menuId: menu.id,
                menuName: menu.name,
                unitPrice: menu.price,
                quantity: 1,
              },
            ],
            isPanelOpen: true,
          };
        }),

      removeItem: (menuId) =>
        set((state) => ({
          items: state.items.filter((i) => i.menuId !== menuId),
        })),

      incrementQuantity: (menuId) =>
        set((state) => ({
          items: state.items.map((i) =>
            i.menuId === menuId
              ? { ...i, quantity: Math.min(i.quantity + 1, MAX_QUANTITY) }
              : i
          ),
        })),

      decrementQuantity: (menuId) =>
        set((state) => {
          const item = state.items.find((i) => i.menuId === menuId);
          if (item && item.quantity <= MIN_QUANTITY) {
            return { items: state.items.filter((i) => i.menuId !== menuId) };
          }
          return {
            items: state.items.map((i) =>
              i.menuId === menuId ? { ...i, quantity: i.quantity - 1 } : i
            ),
          };
        }),

      clearCart: () => set({ items: [] }),

      openPanel: () => set({ isPanelOpen: true }),
      closePanel: () => set({ isPanelOpen: false }),

      getTotalAmount: () =>
        get().items.reduce((sum, item) => sum + item.unitPrice * item.quantity, 0),

      getTotalQuantity: () =>
        get().items.reduce((sum, item) => sum + item.quantity, 0),
    }),
    {
      name: 'cart-storage',
      partialize: (state) => ({ items: state.items }),
    }
  )
);
