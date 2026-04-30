import { LoginForm } from '../components/auth/LoginForm';
import { useAdminAuth } from '../hooks/useAdminAuth';

export function LoginPage() {
  const { login, loading, error } = useAdminAuth();

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900">테이블오더 관리자</h1>
          <p className="mt-2 text-gray-600">매장 관리 시스템에 로그인하세요</p>
        </div>
        <div className="bg-white rounded-lg shadow-md p-6">
          <LoginForm onSubmit={login} loading={loading} error={error} />
        </div>
      </div>
    </div>
  );
}
