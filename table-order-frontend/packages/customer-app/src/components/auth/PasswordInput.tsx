import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Lock } from 'lucide-react';
import { passwordSchema, type PasswordFormData } from '../../utils/schemas';

interface PasswordInputProps {
  storeCode: string;
  onSubmit: (password: string) => void;
  isLoading: boolean;
  error: string | null;
}

export function PasswordInput({ storeCode, onSubmit, isLoading, error }: PasswordInputProps) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<PasswordFormData>({
    resolver: zodResolver(passwordSchema),
  });

  return (
    <form
      onSubmit={handleSubmit((data) => onSubmit(data.password))}
      className="w-full max-w-sm space-y-4"
      data-testid="password-form"
    >
      <div className="text-center mb-4">
        <Lock className="w-10 h-10 text-primary-600 mx-auto mb-2" />
        <p className="text-sm text-gray-500">매장: <span className="font-medium text-gray-700">{storeCode}</span></p>
      </div>
      <div>
        <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-1">
          테이블 비밀번호
        </label>
        <input
          id="password"
          type="password"
          {...register('password')}
          placeholder="비밀번호 입력"
          autoFocus
          className="w-full px-4 py-3 border border-gray-300 rounded-lg text-base text-center tracking-widest min-h-touch focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
          data-testid="password-input"
        />
        {errors.password && (
          <p className="mt-1 text-sm text-red-500">{errors.password.message}</p>
        )}
        {error && (
          <p className="mt-1 text-sm text-red-500" role="alert">{error}</p>
        )}
      </div>
      <button
        type="submit"
        disabled={isLoading}
        className="w-full px-4 py-3 bg-primary-600 text-white rounded-lg font-medium min-h-touch hover:bg-primary-700 disabled:bg-gray-300"
        data-testid="password-submit-button"
      >
        {isLoading ? '인증 중...' : '설정 완료'}
      </button>
    </form>
  );
}
