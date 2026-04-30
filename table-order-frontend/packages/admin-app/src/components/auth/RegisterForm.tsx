import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Link } from 'react-router-dom';

const registerSchema = z.object({
  storeCode: z.string().min(1, '매장 식별자를 입력해주세요'),
  username: z.string().min(1, '사용자명을 입력해주세요').max(50, '사용자명은 50자 이하여야 합니다'),
  password: z.string().min(8, '비밀번호는 8자 이상이어야 합니다')
    .regex(/[a-zA-Z]/, '영문을 포함해야 합니다')
    .regex(/[0-9]/, '숫자를 포함해야 합니다'),
  confirmPassword: z.string().min(1, '비밀번호 확인을 입력해주세요'),
}).refine(data => data.password === data.confirmPassword, {
  message: '비밀번호가 일치하지 않습니다',
  path: ['confirmPassword'],
});

type RegisterFormData = z.infer<typeof registerSchema>;

interface RegisterFormProps {
  onSubmit: (data: { storeCode: string; username: string; password: string }) => Promise<void>;
  loading: boolean;
  error: string | null;
}

export function RegisterForm({ onSubmit, loading, error }: RegisterFormProps) {
  const { register, handleSubmit, formState: { errors } } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
  });

  const handleFormSubmit = (data: RegisterFormData) => {
    const { confirmPassword: _, ...submitData } = data;
    return onSubmit(submitData);
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4" data-testid="register-form">
      <div>
        <label htmlFor="storeCode" className="block text-sm font-medium text-gray-700">매장 식별자</label>
        <input id="storeCode" type="text" {...register('storeCode')}
          className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          data-testid="register-form-store-code" />
        {errors.storeCode && <p className="mt-1 text-sm text-red-600">{errors.storeCode.message}</p>}
      </div>

      <div>
        <label htmlFor="username" className="block text-sm font-medium text-gray-700">사용자명</label>
        <input id="username" type="text" {...register('username')}
          className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          data-testid="register-form-username" />
        {errors.username && <p className="mt-1 text-sm text-red-600">{errors.username.message}</p>}
      </div>

      <div>
        <label htmlFor="password" className="block text-sm font-medium text-gray-700">비밀번호</label>
        <input id="password" type="password" {...register('password')}
          className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          placeholder="영문 + 숫자 포함 8자 이상"
          data-testid="register-form-password" />
        {errors.password && <p className="mt-1 text-sm text-red-600">{errors.password.message}</p>}
      </div>

      <div>
        <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700">비밀번호 확인</label>
        <input id="confirmPassword" type="password" {...register('confirmPassword')}
          className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
          data-testid="register-form-confirm-password" />
        {errors.confirmPassword && <p className="mt-1 text-sm text-red-600">{errors.confirmPassword.message}</p>}
      </div>

      {error && (
        <div className="rounded-md bg-red-50 p-3" data-testid="register-form-error">
          <p className="text-sm text-red-700">{error}</p>
        </div>
      )}

      <button type="submit" disabled={loading}
        className="w-full rounded-md bg-blue-600 px-4 py-2 text-white font-medium hover:bg-blue-700 disabled:bg-blue-300 disabled:cursor-not-allowed min-h-[44px]"
        data-testid="register-form-submit-button">
        {loading ? '가입 중...' : '회원가입'}
      </button>

      <div className="text-center text-sm text-gray-600">
        이미 계정이 있으신가요?{' '}
        <Link to="/login" className="text-blue-600 hover:underline" data-testid="register-form-login-link">로그인</Link>
      </div>
    </form>
  );
}
