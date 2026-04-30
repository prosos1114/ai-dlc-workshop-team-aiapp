import { useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { TOKEN_KEY } from '@table-order/shared';
import { useAuthStore } from '../store/authStore';
import { tableLogin } from '../api/auth';
import type { AuthInfo } from '../types';

export function useTableAuth() {
  const { isAuthenticated, isLoading, authInfo, error, setAuth, setLoading, setError, logout, clearError } =
    useAuthStore();
  const navigate = useNavigate();

  const login = useCallback(
    async (storeCode: string, password: string) => {
      setLoading(true);
      clearError();
      try {
        const response = await tableLogin(storeCode, password);
        localStorage.setItem(TOKEN_KEY, response.token);
        const info: AuthInfo = {
          token: response.token,
          storeId: response.storeId,
          tableId: response.tableId,
          tableNumber: (response as AuthInfo & { tableNumber: number }).tableNumber ?? 0,
          storeCode,
        };
        setAuth(info);
        navigate('/menu', { replace: true });
      } catch {
        setError('인증에 실패했습니다. 다시 시도해주세요.');
      }
    },
    [setAuth, setLoading, setError, clearError, navigate]
  );

  const autoLogin = useCallback(async () => {
    if (!authInfo?.token) return false;
    setLoading(true);
    localStorage.setItem(TOKEN_KEY, authInfo.token);
    setLoading(false);
    return true;
  }, [authInfo, setLoading]);

  const handleLogout = useCallback(() => {
    logout();
    navigate('/setup', { replace: true });
  }, [logout, navigate]);

  return {
    isAuthenticated,
    isLoading,
    authInfo,
    error,
    login,
    autoLogin,
    logout: handleLogout,
    clearError,
  };
}
