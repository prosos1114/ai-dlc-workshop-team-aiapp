import { describe, it, expect, beforeEach } from 'vitest';
import { useAuthStore } from '../authStore';
import type { AuthInfo } from '../../types';

const mockAuthInfo: AuthInfo = {
  token: 'test-token-123',
  storeId: 1,
  tableId: 5,
  tableNumber: 5,
  storeCode: 'cafe-abc',
};

describe('authStore', () => {
  beforeEach(() => {
    useAuthStore.setState({
      isAuthenticated: false,
      isLoading: false,
      authInfo: null,
      error: null,
    });
    localStorage.clear();
  });

  it('should set auth info and mark as authenticated', () => {
    useAuthStore.getState().setAuth(mockAuthInfo);
    const state = useAuthStore.getState();
    expect(state.isAuthenticated).toBe(true);
    expect(state.authInfo).toEqual(mockAuthInfo);
    expect(state.error).toBeNull();
  });

  it('should set loading state', () => {
    useAuthStore.getState().setLoading(true);
    expect(useAuthStore.getState().isLoading).toBe(true);
    useAuthStore.getState().setLoading(false);
    expect(useAuthStore.getState().isLoading).toBe(false);
  });

  it('should set error and clear loading', () => {
    useAuthStore.getState().setLoading(true);
    useAuthStore.getState().setError('인증 실패');
    const state = useAuthStore.getState();
    expect(state.error).toBe('인증 실패');
    expect(state.isLoading).toBe(false);
  });

  it('should logout and clear state', () => {
    useAuthStore.getState().setAuth(mockAuthInfo);
    localStorage.setItem('token', 'test-token');
    useAuthStore.getState().logout();
    const state = useAuthStore.getState();
    expect(state.isAuthenticated).toBe(false);
    expect(state.authInfo).toBeNull();
    expect(localStorage.getItem('token')).toBeNull();
  });

  it('should clear error', () => {
    useAuthStore.getState().setError('some error');
    useAuthStore.getState().clearError();
    expect(useAuthStore.getState().error).toBeNull();
  });
});
