import { describe, it, expect, beforeEach } from 'vitest';
import { useCartStore } from '../cartStore';
import type { Menu } from '../../types';

const mockMenu: Menu = {
  id: 1,
  storeId: 1,
  categoryId: 1,
  name: '아메리카노',
  price: 4500,
  description: null,
  imageUrl: null,
  displayOrder: 0,
};

const mockMenu2: Menu = {
  id: 2,
  storeId: 1,
  categoryId: 1,
  name: '카페라떼',
  price: 5000,
  description: null,
  imageUrl: null,
  displayOrder: 1,
};

describe('cartStore', () => {
  beforeEach(() => {
    useCartStore.setState({ items: [], isPanelOpen: false });
  });

  it('should add item to cart', () => {
    useCartStore.getState().addItem(mockMenu);
    const { items } = useCartStore.getState();
    expect(items).toHaveLength(1);
    expect(items[0].menuId).toBe(1);
    expect(items[0].quantity).toBe(1);
  });

  it('should increment quantity when adding existing item', () => {
    useCartStore.getState().addItem(mockMenu);
    useCartStore.getState().addItem(mockMenu);
    const { items } = useCartStore.getState();
    expect(items).toHaveLength(1);
    expect(items[0].quantity).toBe(2);
  });

  it('should not exceed max quantity (99)', () => {
    useCartStore.setState({
      items: [{ menuId: 1, menuName: '아메리카노', unitPrice: 4500, quantity: 99 }],
    });
    useCartStore.getState().addItem(mockMenu);
    expect(useCartStore.getState().items[0].quantity).toBe(99);
  });

  it('should remove item from cart', () => {
    useCartStore.getState().addItem(mockMenu);
    useCartStore.getState().removeItem(1);
    expect(useCartStore.getState().items).toHaveLength(0);
  });

  it('should increment quantity', () => {
    useCartStore.getState().addItem(mockMenu);
    useCartStore.getState().incrementQuantity(1);
    expect(useCartStore.getState().items[0].quantity).toBe(2);
  });

  it('should decrement quantity', () => {
    useCartStore.setState({
      items: [{ menuId: 1, menuName: '아메리카노', unitPrice: 4500, quantity: 3 }],
    });
    useCartStore.getState().decrementQuantity(1);
    expect(useCartStore.getState().items[0].quantity).toBe(2);
  });

  it('should remove item when decrementing from quantity 1', () => {
    useCartStore.getState().addItem(mockMenu);
    useCartStore.getState().decrementQuantity(1);
    expect(useCartStore.getState().items).toHaveLength(0);
  });

  it('should clear all items', () => {
    useCartStore.getState().addItem(mockMenu);
    useCartStore.getState().addItem(mockMenu2);
    useCartStore.getState().clearCart();
    expect(useCartStore.getState().items).toHaveLength(0);
  });

  it('should calculate total amount', () => {
    useCartStore.getState().addItem(mockMenu);
    useCartStore.getState().addItem(mockMenu2);
    useCartStore.getState().incrementQuantity(1);
    // 아메리카노 4500 * 2 + 카페라떼 5000 * 1 = 14000
    expect(useCartStore.getState().getTotalAmount()).toBe(14000);
  });

  it('should calculate total quantity', () => {
    useCartStore.getState().addItem(mockMenu);
    useCartStore.getState().addItem(mockMenu2);
    useCartStore.getState().incrementQuantity(1);
    expect(useCartStore.getState().getTotalQuantity()).toBe(3);
  });

  it('should open and close panel', () => {
    useCartStore.getState().openPanel();
    expect(useCartStore.getState().isPanelOpen).toBe(true);
    useCartStore.getState().closePanel();
    expect(useCartStore.getState().isPanelOpen).toBe(false);
  });

  it('should auto-open panel when adding item', () => {
    useCartStore.getState().addItem(mockMenu);
    expect(useCartStore.getState().isPanelOpen).toBe(true);
  });
});
