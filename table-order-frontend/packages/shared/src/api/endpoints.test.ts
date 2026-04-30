import { describe, it, expect } from 'vitest';
import { API } from './endpoints';

describe('API endpoints', () => {
  describe('Auth endpoints', () => {
    it('관리자 로그인 경로가 올바르다', () => {
      expect(API.ADMIN_LOGIN).toBe('/api/admin/auth/login');
    });

    it('관리자 회원가입 경로가 올바르다', () => {
      expect(API.ADMIN_REGISTER).toBe('/api/admin/auth/register');
    });

    it('테이블 로그인 경로가 올바르다', () => {
      expect(API.TABLE_LOGIN).toBe('/api/table/auth/login');
    });
  });

  describe('Store endpoints', () => {
    it('매장 목록 경로가 올바르다', () => {
      expect(API.STORES).toBe('/api/stores');
    });

    it('매장 코드로 조회 경로가 올바르다', () => {
      expect(API.STORE_BY_CODE('CAFE-001')).toBe('/api/stores/CAFE-001');
    });
  });

  describe('Table endpoints', () => {
    it('테이블 목록 경로가 올바르다', () => {
      expect(API.TABLES(1)).toBe('/api/stores/1/tables');
    });

    it('테이블 상세 경로가 올바르다', () => {
      expect(API.TABLE(1, 5)).toBe('/api/stores/1/tables/5');
    });

    it('테이블 이용 완료 경로가 올바르다', () => {
      expect(API.TABLE_COMPLETE(1, 5)).toBe('/api/stores/1/tables/5/complete');
    });
  });

  describe('Category endpoints', () => {
    it('카테고리 목록 경로가 올바르다', () => {
      expect(API.CATEGORIES(1)).toBe('/api/stores/1/categories');
    });
  });

  describe('Menu endpoints', () => {
    it('메뉴 목록 경로가 올바르다', () => {
      expect(API.MENUS(1)).toBe('/api/stores/1/menus');
    });

    it('메뉴 상세 경로가 올바르다', () => {
      expect(API.MENU(1, 10)).toBe('/api/stores/1/menus/10');
    });

    it('메뉴 순서 변경 경로가 올바르다', () => {
      expect(API.MENU_ORDER(1)).toBe('/api/stores/1/menus/order');
    });

    it('메뉴 이미지 업로드 경로가 올바르다', () => {
      expect(API.MENU_IMAGE(1, 10)).toBe('/api/stores/1/menus/10/image');
    });
  });

  describe('Order endpoints', () => {
    it('테이블 주문 목록 경로가 올바르다', () => {
      expect(API.TABLE_ORDERS(1, 5)).toBe('/api/stores/1/tables/5/orders');
    });

    it('매장 전체 주문 경로가 올바르다', () => {
      expect(API.STORE_ORDERS(1)).toBe('/api/stores/1/orders');
    });

    it('주문 상태 변경 경로가 올바르다', () => {
      expect(API.ORDER_STATUS(1, 100)).toBe('/api/stores/1/orders/100/status');
    });

    it('주문 삭제 경로가 올바르다', () => {
      expect(API.ORDER(1, 100)).toBe('/api/stores/1/orders/100');
    });
  });

  describe('History endpoints', () => {
    it('테이블 주문 내역 경로가 올바르다', () => {
      expect(API.TABLE_HISTORY(1, 5)).toBe('/api/stores/1/tables/5/history');
    });
  });

  describe('SSE endpoints', () => {
    it('주문 스트림 경로가 올바르다', () => {
      expect(API.ORDER_STREAM(1)).toBe('/api/stores/1/orders/stream');
    });
  });
});
