import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { apiClient, API } from '@table-order/shared';
import type { TableInfo, ApiResponse, PageResponse, OrderHistory } from '@table-order/shared';
import { formatPrice, formatDate } from '@table-order/shared';
import { useAdminAuth } from '../hooks/useAdminAuth';
import { ArrowLeft, Plus, History, X } from 'lucide-react';

const tableSchema = z.object({
  tableNumber: z.coerce.number().min(1, '테이블 번호는 1 이상이어야 합니다'),
  password: z.string().min(4, '비밀번호는 4자 이상이어야 합니다'),
});

type TableFormData = z.infer<typeof tableSchema>;

export function TableManagePage() {
  const navigate = useNavigate();
  const { getStoreId } = useAdminAuth();
  const storeId = getStoreId();

  const [tables, setTables] = useState<TableInfo[]>([]);
  const [showForm, setShowForm] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [historyTableId, setHistoryTableId] = useState<number | null>(null);
  const [historyData, setHistoryData] = useState<OrderHistory[]>([]);
  const [historyTableNumber, setHistoryTableNumber] = useState<number>(0);

  const { register, handleSubmit, reset, formState: { errors } } = useForm<TableFormData>({
    resolver: zodResolver(tableSchema),
  });

  useEffect(() => {
    if (!storeId) return;
    const load = async () => {
      const { data } = await apiClient.get<ApiResponse<TableInfo[]>>(API.TABLES(storeId));
      setTables(data.data);
    };
    load();
  }, [storeId]);

  const onSubmit = async (formData: TableFormData) => {
    if (!storeId) return;
    setError(null);
    try {
      const { data } = await apiClient.post<ApiResponse<TableInfo>>(API.TABLES(storeId), formData);
      setTables(prev => [...prev, data.data]);
      setShowForm(false);
      reset();
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })
        ?.response?.data?.message ?? '테이블 생성에 실패했습니다';
      setError(message);
    }
  };

  const openHistory = async (tableId: number, tableNumber: number) => {
    if (!storeId) return;
    setHistoryTableId(tableId);
    setHistoryTableNumber(tableNumber);
    try {
      const { data } = await apiClient.get<ApiResponse<PageResponse<OrderHistory>>>(
        API.TABLE_HISTORY(storeId, tableId), { params: { page: 0, size: 50 } }
      );
      setHistoryData(data.data.content);
    } catch {
      setHistoryData([]);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-4xl mx-auto px-4 py-3 flex items-center gap-3">
          <button onClick={() => navigate('/dashboard')} className="p-2 hover:bg-gray-100 rounded min-w-[44px] min-h-[44px] flex items-center justify-center"
            data-testid="table-manage-back-button"><ArrowLeft size={20} /></button>
          <h1 className="text-xl font-bold">테이블 관리</h1>
        </div>
      </header>

      <main className="max-w-4xl mx-auto px-4 py-6">
        <div className="flex justify-end mb-4">
          <button onClick={() => setShowForm(true)}
            className="flex items-center gap-1 rounded bg-blue-600 px-4 py-2 text-white hover:bg-blue-700 min-h-[44px]"
            data-testid="table-manage-add-button"><Plus size={18} /> 테이블 추가</button>
        </div>

        {showForm && (
          <div className="bg-white rounded-lg shadow-md p-4 mb-4" data-testid="table-manage-form">
            <form onSubmit={handleSubmit(onSubmit)} className="flex items-end gap-3">
              <div className="flex-1">
                <label className="block text-sm font-medium text-gray-700">테이블 번호</label>
                <input type="number" {...register('tableNumber')}
                  className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2"
                  data-testid="table-manage-form-number" />
                {errors.tableNumber && <p className="text-sm text-red-600">{errors.tableNumber.message}</p>}
              </div>
              <div className="flex-1">
                <label className="block text-sm font-medium text-gray-700">비밀번호</label>
                <input type="password" {...register('password')}
                  className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2"
                  data-testid="table-manage-form-password" />
                {errors.password && <p className="text-sm text-red-600">{errors.password.message}</p>}
              </div>
              <button type="submit" className="rounded bg-blue-600 px-4 py-2 text-white hover:bg-blue-700 min-h-[44px]"
                data-testid="table-manage-form-submit">저장</button>
              <button type="button" onClick={() => { setShowForm(false); reset(); }}
                className="rounded bg-gray-200 px-4 py-2 hover:bg-gray-300 min-h-[44px]">취소</button>
            </form>
            {error && <p className="mt-2 text-sm text-red-600">{error}</p>}
          </div>
        )}

        <div className="bg-white rounded-lg shadow-md overflow-hidden">
          <table className="w-full" data-testid="table-manage-list">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-4 py-3 text-left text-sm font-medium text-gray-700">테이블 번호</th>
                <th className="px-4 py-3 text-left text-sm font-medium text-gray-700">생성일</th>
                <th className="px-4 py-3 text-right text-sm font-medium text-gray-700">작업</th>
              </tr>
            </thead>
            <tbody className="divide-y">
              {tables.map(table => (
                <tr key={table.id} data-testid={`table-manage-row-${table.tableNumber}`}>
                  <td className="px-4 py-3 font-medium">테이블 {table.tableNumber}</td>
                  <td className="px-4 py-3 text-sm text-gray-500">{formatDate(table.createdAt)}</td>
                  <td className="px-4 py-3 text-right">
                    <button onClick={() => openHistory(table.id, table.tableNumber)}
                      className="inline-flex items-center gap-1 rounded bg-gray-100 px-3 py-1 text-sm hover:bg-gray-200 min-h-[36px]"
                      data-testid={`table-manage-history-${table.tableNumber}`}>
                      <History size={14} /> 과거 내역
                    </button>
                  </td>
                </tr>
              ))}
              {tables.length === 0 && (
                <tr><td colSpan={3} className="px-4 py-8 text-center text-gray-400">등록된 테이블이 없습니다</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </main>

      {historyTableId && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50" data-testid="history-modal">
          <div className="bg-white rounded-lg shadow-xl w-full max-w-2xl max-h-[80vh] overflow-hidden mx-4">
            <div className="flex items-center justify-between p-4 border-b">
              <h2 className="text-lg font-bold">테이블 {historyTableNumber} - 과거 내역</h2>
              <button onClick={() => setHistoryTableId(null)} className="p-1 hover:bg-gray-100 rounded min-w-[44px] min-h-[44px] flex items-center justify-center"
                data-testid="history-modal-close"><X size={20} /></button>
            </div>
            <div className="overflow-y-auto p-4 space-y-3" style={{ maxHeight: 'calc(80vh - 60px)' }}>
              {historyData.length === 0 ? (
                <p className="text-center text-gray-400 py-8">과거 주문 내역이 없습니다</p>
              ) : (
                historyData.map(h => (
                  <div key={h.id} className="border rounded-lg p-3">
                    <div className="flex justify-between items-center mb-1">
                      <span className="font-medium">{h.orderNumber}</span>
                      <span className="font-bold text-blue-600">{formatPrice(h.totalAmount)}</span>
                    </div>
                    <div className="text-xs text-gray-500">
                      주문: {formatDate(h.orderedAt)} | 완료: {formatDate(h.completedAt)}
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
