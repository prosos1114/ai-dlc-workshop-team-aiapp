import { useState, useCallback, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiClient, API, TOKEN_KEY, STORE_ID_KEY } from '@table-order/shared';
import type { LoginRequest, RegisterRequest, TokenResponse, ApiResponse } from '@table-order/shared';

export function useAdminAuth() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const isAuthenticated = useCallback(() => {
    return !!localStorage.getItem(TOKEN_KEY);
  }, []);

  const getStoreId = useCallback(() => {
    const id = localStorage.getItem(STORE_ID_KEY);
    return id ? Number(id) : null;
  }, []);

  const login = useCallback(async (request: LoginRequest) => {
    setLoading(true);
    setError(null);
    try {
      const { data } = await apiClient.post<ApiResponse<TokenResponse & { storeId: number }>>(
        API.ADMIN_LOGIN, request
      );
      localStorage.setItem(TOKEN_KEY, data.data.token);
      localStorage.setItem(STORE_ID_KEY, String(data.data.storeId));
      navigate('/dashboard');
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })
        ?.response?.data?.message ?? '로그인에 실패했습니다';
      setError(message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, [navigate]);

  const register = useCallback(async (request: RegisterRequest) => {
    setLoading(true);
    setError(null);
    try {
      await apiClient.post(API.ADMIN_REGISTER, request);
      navigate('/login');
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })
        ?.response?.data?.message ?? '회원가입에 실패했습니다';
      setError(message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, [navigate]);

  const logout = useCallback(() => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(STORE_ID_KEY);
    navigate('/login');
  }, [navigate]);

  useEffect(() => {
    // 토큰 만료 체크 (16시간)
    const token = localStorage.getItem(TOKEN_KEY);
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        if (payload.exp * 1000 < Date.now()) {
          logout();
        }
      } catch {
        // 토큰 파싱 실패 시 무시
      }
    }
  }, [logout]);

  return { login, register, logout, isAuthenticated, getStoreId, loading, error };
}
