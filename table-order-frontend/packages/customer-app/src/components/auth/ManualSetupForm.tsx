import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { manualSetupSchema, type ManualSetupFormData } from '../../utils/schemas';
import type { QRCodeData } from '../../types';

interface ManualSetupFormProps {
  onSubmit: (data: QRCodeData) => void;
}

export function ManualSetupForm({ onSubmit }: ManualSetupFormProps) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ManualSetupFormData>({
    resolver: zodResolver(manualSetupSchema),
  });

  const onFormSubmit = (data: ManualSetupFormData) => {
    onSubmit({ storeCode: data.storeCode, totalTables: data.totalTables });
  };

  return (
    <form onSubmit={handleSubmit(onFormSubmit)} className="w-full max-w-sm space-y-4" data-testid="manual-setup-form">
      <div>
        <label htmlFor="storeCode" className="block text-sm font-medium text-gray-700 mb-1">
          매장 코드
        </label>
        <input
          id="storeCode"
          type="text"
          {...register('storeCode')}
          placeholder="예: cafe-abc"
          className="w-full px-4 py-3 border border-gray-300 rounded-lg text-base min-h-touch focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
          data-testid="store-code-input"
        />
        {errors.storeCode && (
          <p className="mt-1 text-sm text-red-500">{errors.storeCode.message}</p>
        )}
      </div>
      <div>
        <label htmlFor="totalTables" className="block text-sm font-medium text-gray-700 mb-1">
          총 테이블 수
        </label>
        <input
          id="totalTables"
          type="number"
          {...register('totalTables', { valueAsNumber: true })}
          placeholder="예: 10"
          className="w-full px-4 py-3 border border-gray-300 rounded-lg text-base min-h-touch focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
          data-testid="total-tables-input"
        />
        {errors.totalTables && (
          <p className="mt-1 text-sm text-red-500">{errors.totalTables.message}</p>
        )}
      </div>
      <button
        type="submit"
        className="w-full px-4 py-3 bg-primary-600 text-white rounded-lg font-medium min-h-touch hover:bg-primary-700"
        data-testid="manual-setup-submit"
      >
        다음
      </button>
    </form>
  );
}
