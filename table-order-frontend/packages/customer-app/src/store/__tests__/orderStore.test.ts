import { describe, it, expect, beforeEach } from 'vitest';
import { useOrderStore } from '../orderStore';
import type { Order } from '../../types';

const mockOrder: Order = {
  id: 1,
  storeId: 1,
  tableId: 5,
  sessionId: 10,
  orderNumber: 'ORD-001',
  status: 'PENDING',
  totalAmount: 14000,
  items: [
    { id: 1, menuId: 1, menuName: '아메리카노', quantity: 2, unitPrice: 4500, subtotal: 9000 },
    { id: 2, menuId: 2, menuName: '카페라떼', quantity: 1, unitPrice: 5000, subtotal: 5000 },
  ],
  createdAt: '2026-04-30T12:00:00Z',
};

describe('orderStore', () => {
  beforeEach(() => {
    useOrderStore.setState({
      orders: [],
      isLoading: false,
      isSubmitting: false,
      error: null,
      failureCount: 0,
      lastOrderResult: null,
    });
  });

  it('should update order status', () => {
    useOrderStore.setState({ orders: [mockOrder] });
    useOrderStore.getState().updateOrderStatus(1, 'PREPARING');
    expect(useOrderStore.getState().orders[0].status).toBe('PREPARING');
  });

  it('should remove order', () => {
    useOrderStore.setState({ orders: [mockOrder] });
    useOrderStore.getState().removeOrder(1);
    expect(useOrderStore.getState().orders).toHaveLength(0);
  });

  it('should clear last result', () => {
    useOrderStore.setState({ lastOrderResult: { success: true, orderNumber: 'ORD-001' } });
    useOrderStore.getState().clearLastResult();
    expect(useOrderStore.getState().lastOrderResult).toBeNull();
  });

  it('should reset failure count', () => {
    useOrderStore.setState({ failureCount: 3 });
    useOrderStore.getState().resetFailureCount();
    expect(useOrderStore.getState().failureCount).toBe(0);
  });

  it('should not modify other orders when updating status', () => {
    const order2: Order = { ...mockOrder, id: 2, orderNumber: 'ORD-002' };
    useOrderStore.setState({ orders: [mockOrder, order2] });
    useOrderStore.getState().updateOrderStatus(1, 'COMPLETED');
    expect(useOrderStore.getState().orders[0].status).toBe('COMPLETED');
    expect(useOrderStore.getState().orders[1].status).toBe('PENDING');
  });
});
