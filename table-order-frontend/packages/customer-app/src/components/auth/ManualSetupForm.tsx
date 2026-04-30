import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import type { QRCodeData } from '../../types';

const manualSetupSchema = z.object({
  storeCode: z.string().min(1, '매장 코드를 입력해주세요'),
  tableNumber: z.coerce.number().min(1, '테이블 번호를 입력해주세요'),
});

type ManualSetupFormData = z.infer<typeof manualSetupSchema>;

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
    onSubmit({ storeCode: data.storeCode, tableNumber: data.tableNumber });
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
          className="w-full px-4 py-3 border border-gray-300 rounded-lg text-base min-h-[44px] focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          data-testid="store-code-input"
        />
        {errors.storeCode && (
          <p className="mt-1 text-sm text-red-500">{errors.storeCode.message}</p>
        )}
      </div>
      <div>
        <label htmlFor="tableNumber" className="block text-sm font-medium text-gray-700 mb-1">
          테이블 번호
        </label>
        <input
          id="tableNumber"
          type="number"
          {...register('tableNumber')}
          placeholder="예: 1"
          className="w-full px-4 py-3 border border-gray-300 rounded-lg text-base min-h-[44px] focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          data-testid="table-number-input"
        />
        {errors.tableNumber && (
          <p className="mt-1 text-sm text-red-500">{errors.tableNumber.message}</p>
        )}
      </div>
      <button
        type="submit"
        className="w-full px-4 py-3 bg-blue-600 text-white rounded-lg font-medium min-h-[44px] hover:bg-blue-700"
        data-testid="manual-setup-submit"
      >
        다음
      </button>
    </form>
  );
}
