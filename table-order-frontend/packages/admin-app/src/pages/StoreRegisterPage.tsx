import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { apiClient, API } from '@table-order/shared';

const storeSchema = z.object({
  name: z.string().min(1, '매장명을 입력해주세요').max(100),
  code: z.string().min(3, '매장 식별자는 3자 이상이어야 합니다').max(50)
    .regex(/^[a-z0-9-]+$/, '영문 소문자, 숫자, 하이픈만 허용됩니다'),
});

type StoreFormData = z.infer<typeof storeSchema>;

export function StoreRegisterPage() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const { register, handleSubmit, formState: { errors } } = useForm<StoreFormData>({
    resolver: zodResolver(storeSchema),
  });

  const onSubmit = async (data: StoreFormData) => {
    setLoading(true);
    setError(null);
    try {
      await apiClient.post(API.STORES, data);
      navigate('/register');
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })
        ?.response?.data?.message ?? '매장 등록에 실패했습니다';
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900">매장 등록</h1>
          <p className="mt-2 text-gray-600">새로운 매장을 시스템에 등록하세요</p>
        </div>
        <div className="bg-white rounded-lg shadow-md p-6">
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4" data-testid="store-register-form">
            <div>
              <label htmlFor="name" className="block text-sm font-medium text-gray-700">매장명</label>
              <input id="name" type="text" {...register('name')}
                className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
                data-testid="store-register-name" />
              {errors.name && <p className="mt-1 text-sm text-red-600">{errors.name.message}</p>}
            </div>

            <div>
              <label htmlFor="code" className="block text-sm font-medium text-gray-700">매장 식별자</label>
              <input id="code" type="text" {...register('code')} placeholder="예: my-cafe-01"
                className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 shadow-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500"
                data-testid="store-register-code" />
              {errors.code && <p className="mt-1 text-sm text-red-600">{errors.code.message}</p>}
              <p className="mt-1 text-xs text-gray-500">영문 소문자, 숫자, 하이픈만 사용 가능</p>
            </div>

            {error && (
              <div className="rounded-md bg-red-50 p-3" data-testid="store-register-error">
                <p className="text-sm text-red-700">{error}</p>
              </div>
            )}

            <button type="submit" disabled={loading}
              className="w-full rounded-md bg-blue-600 px-4 py-2 text-white font-medium hover:bg-blue-700 disabled:bg-blue-300 disabled:cursor-not-allowed min-h-[44px]"
              data-testid="store-register-submit-button">
              {loading ? '등록 중...' : '매장 등록'}
            </button>

            <div className="text-center text-sm text-gray-600">
              <Link to="/login" className="text-blue-600 hover:underline">로그인으로 돌아가기</Link>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
