import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../../store/authStore';
import { TOKEN_KEY } from '@table-order/shared';
import { LoadingSpinner } from '../common/LoadingSpinner';

interface AuthGuardProps {
  children: React.ReactNode;
}

export function AuthGuard({ children }: AuthGuardProps) {
  const { isAuthenticated, isLoading, authInfo } = useAuthStore();
  const navigate = useNavigate();

  useEffect(() => {
    if (!isAuthenticated || !authInfo?.token) {
      localStorage.removeItem(TOKEN_KEY);
      navigate('/setup', { replace: true });
    }
  }, [isAuthenticated, authInfo, navigate]);

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <LoadingSpinner message="로그인 중..." size="lg" />
      </div>
    );
  }

  if (!isAuthenticated) return null;

  return <>{children}</>;
}
