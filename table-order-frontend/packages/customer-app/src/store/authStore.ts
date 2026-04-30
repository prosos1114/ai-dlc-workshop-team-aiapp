import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { AuthInfo } from '../types';

interface AuthState {
  isAuthenticated: boolean;
  isLoading: boolean;
  authInfo: AuthInfo | null;
  error: string | null;
}

interface AuthActions {
  setAuth: (authInfo: AuthInfo) => void;
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  logout: () => void;
  clearError: () => void;
}

export type AuthStore = AuthState & AuthActions;

export const useAuthStore = create<AuthStore>()(
  persist(
    (set) => ({
      isAuthenticated: false,
      isLoading: false,
      authInfo: null,
      error: null,

      setAuth: (authInfo) =>
        set({ isAuthenticated: true, authInfo, error: null, isLoading: false }),

      setLoading: (isLoading) => set({ isLoading }),

      setError: (error) => set({ error, isLoading: false }),

      logout: () => {
        localStorage.removeItem('token');
        set({ isAuthenticated: false, authInfo: null, error: null });
      },

      clearError: () => set({ error: null }),
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({ authInfo: state.authInfo, isAuthenticated: state.isAuthenticated }),
    }
  )
);
