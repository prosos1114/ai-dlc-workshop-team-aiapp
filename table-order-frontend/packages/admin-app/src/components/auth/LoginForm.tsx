import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Link } from 'react-router-dom';
import type { LoginRequest } from '@table-order/shared';

const loginSchema = z.object({
  storeCode: z.string().min(1, '매장 식별자를 입력해주세요'),
  username: z.string().min(1, '사용자명을 입력해주세요'),
  password: z.string().min(1, '비밀번호를 입력해주세요'),
});

interface LoginFormProps {
  onSubmit: (data: LoginRequest) => Promise<void>;
  loading: boolean;
  error: string | null;
}

export function LoginForm({ onSubmit, loading, error }: LoginFormProps) {
  const { register, handleSubmit, formState: { errors } } = useForm<LoginRequest>({
    resolver: zodResolver(loginSchema),
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4" data-testid="login-form">
      <div>
        <label htmlFor="storeCode" className="block text-sm font-medium text-gray-700">
          매장 식별자
        </label>
        <input
          id="storeCode"
          type="text"
          {...register('storeCode')}
          className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          data-testid="login-form-store-code"
        />
        {errors.storeCode && (
          <p className="mt-1 text-sm text-red-600">{errors.storeCode.message}</p>
        )}
      </div>

      <div>
        <label htmlFor="username" className="block text-sm font-medium text-gray-700">
          사용자명
        </label>
        <input
          id="username"
          type="text"
          {...register('username')}
          className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          data-testid="login-form-username"
        />
        {errors.username && (
          <p className="mt-1 text-sm text-red-600">{errors.username.message}</p>
        )}
      </div>

      <div>
        <label htmlFor="password" className="block text-sm font-medium text-gray-700">
          비밀번호
        </label>
        <input
          id="password"
          type="password"
          {...register('password')}
          className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          data-testid="login-form-password"
        />
        {errors.password && (
          <p className="mt-1 text-sm text-red-600">{errors.password.message}</p>
        )}
      </div>

      {error && (
        <div className="rounded-md bg-red-50 p-3" data-testid="login-form-error">
          <p className="text-sm text-red-700">{error}</p>
        </div>
      )}

      <button
        type="submit"
        disabled={loading}
        className="w-full rounded-md bg-blue-600 px-4 py-2 text-white font-medium hover:bg-blue-700 disabled:bg-blue-300 disabled:cursor-not-allowed min-h-[44px]"
        data-testid="login-form-submit-button"
      >
        {loading ? '로그인 중...' : '로그인'}
      </button>

      <div className="text-center text-sm text-gray-600">
        <Link to="/register" className="text-blue-600 hover:underline" data-testid="login-form-register-link">
          회원가입
        </Link>
        <span className="mx-2">|</span>
        <Link to="/store/register" className="text-blue-600 hover:underline" data-testid="login-form-store-register-link">
          매장 등록
        </Link>
      </div>
    </form>
  );
}
