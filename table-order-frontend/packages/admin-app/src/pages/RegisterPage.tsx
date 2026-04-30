import { RegisterForm } from '../components/auth/RegisterForm';
import { useAdminAuth } from '../hooks/useAdminAuth';

export function RegisterPage() {
  const { register, loading, error } = useAdminAuth();

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900">관리자 회원가입</h1>
          <p className="mt-2 text-gray-600">매장 관리자 계정을 생성하세요</p>
        </div>
        <div className="bg-white rounded-lg shadow-md p-6">
          <RegisterForm onSubmit={register} loading={loading} error={error} />
        </div>
      </div>
    </div>
  );
}
