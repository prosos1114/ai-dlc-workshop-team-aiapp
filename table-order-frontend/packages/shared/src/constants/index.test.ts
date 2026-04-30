import { describe, it, expect } from 'vitest';
import {
  ORDER_STATUS,
  SESSION_STATUS,
  TOKEN_KEY,
  STORE_ID_KEY,
  TABLE_ID_KEY,
  TABLE_NUMBER_KEY,
  CART_KEY,
  MAX_QUANTITY,
  MIN_QUANTITY,
  ORDER_SUCCESS_REDIRECT_DELAY,
  SSE_RECONNECT_DELAY,
  SSE_HEARTBEAT_INTERVAL,
} from './index';

describe('constants', () => {
  describe('ORDER_STATUS', () => {
    it('주문 상태 상수가 올바르게 정의되어 있다', () => {
      expect(ORDER_STATUS.PENDING).toBe('PENDING');
      expect(ORDER_STATUS.PREPARING).toBe('PREPARING');
      expect(ORDER_STATUS.COMPLETED).toBe('COMPLETED');
    });
  });

  describe('SESSION_STATUS', () => {
    it('세션 상태 상수가 올바르게 정의되어 있다', () => {
      expect(SESSION_STATUS.ACTIVE).toBe('ACTIVE');
      expect(SESSION_STATUS.COMPLETED).toBe('COMPLETED');
    });
  });

  describe('Storage Keys', () => {
    it('localStorage 키가 올바르게 정의되어 있다', () => {
      expect(TOKEN_KEY).toBe('token');
      expect(STORE_ID_KEY).toBe('storeId');
      expect(TABLE_ID_KEY).toBe('tableId');
      expect(TABLE_NUMBER_KEY).toBe('tableNumber');
      expect(CART_KEY).toBe('cart');
    });
  });

  describe('Quantity Limits', () => {
    it('수량 제한이 올바르게 정의되어 있다', () => {
      expect(MAX_QUANTITY).toBe(99);
      expect(MIN_QUANTITY).toBe(1);
    });
  });

  describe('Timing Constants', () => {
    it('타이밍 상수가 올바르게 정의되어 있다', () => {
      expect(ORDER_SUCCESS_REDIRECT_DELAY).toBe(5000);
      expect(SSE_RECONNECT_DELAY).toBe(3000);
      expect(SSE_HEARTBEAT_INTERVAL).toBe(15000);
    });
  });
});
