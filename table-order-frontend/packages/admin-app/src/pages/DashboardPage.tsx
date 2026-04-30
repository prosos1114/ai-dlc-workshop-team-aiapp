import { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiClient, API } from '@table-order/shared';
import type { TableInfo, Order, OrderStatus, ApiResponse, SSEEventType } from '@table-order/shared';
import { useAdminAuth } from '../hooks/useAdminAuth';
import { useOrders } from '../hooks/useOrders';
import { useSSE } from '../hooks/useSSE';
import { TableCard } from '../components/dashboard/TableCard';
import { OrderDetail } from '../components/dashboard/OrderDetail';
import { LogOut, RefreshCw } from 'lucide-react';

export function DashboardPage() {
  const navigate = useNavigate();
  const { logout, getStoreId, isAuthenticated } = useAdminAuth();
  const storeId = getStoreId();
  const { orders, fetchOrders, updateOrderStatus, deleteOrder, addOrder, removeOrder, updateOrder } = useOrders(storeId);

  const [tables, setTables] = useState<TableInfo[]>([]);
  const [selectedTableId, setSelectedTableId] = useState<number | null>(null);
  const [newOrderTableIds, setNewOrderTableIds] = useState<Set<number>>(new Set());
  const [confirmModal, setConfirmModal] = useState<{ type: string; id: number } | null>(null);

  useEffect(() => {
    if (!isAuthenticated()) { navigate('/login'); return; }
    if (!storeId) return;

    const loadData = async () => {
      const { data } = await apiClient.get<ApiResponse<TableInfo[]>>(API.TABLES(storeId));
      setTables(data.data);
      await fetchOrders();
    };
    loadData();
  }, [storeId, isAuthenticated, navigate, fetchOrders]);

  const handleSSEEvent = useCallback((type: SSEEventType, data: unknown) => {
    const eventData = data as Order;
    switch (type) {
      case 'ORDER_CREATED':
        addOrder(eventData);
        setNewOrderTableIds(prev => new Set(prev).add(eventData.tableId));
        setTimeout(() => {
          setNewOrderTableIds(prev => { const next = new Set(prev); next.delete(eventData.tableId); return next; });
        }, 5000);
        break;
      case 'ORDER_STATUS_CHANGED':
        updateOrder(eventData);
        break;
      case 'ORDER_DELETED':
        removeOrder(eventData.id);
        break;
      case 'TABLE_COMPLETED':
        fetchOrders();
        break;
    }
  }, [addOrder, updateOrder, removeOrder, fetchOrders]);

  useSSE({ storeId: storeId ?? 0, onEvent: handleSSEEvent, enabled: !!storeId });

  const getOrdersForTable = (tableId: number) =>
    orders.filter(o => o.tableId === tableId).sort((a, b) =>
      new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());

  const handleCompleteTable = async (tableId: number) => {
    setConfirmModal({ type: 'complete', id: tableId });
  };

  const handleConfirm = async () => {
    if (!confirmModal || !storeId) return;
    if (confirmModal.type === 'complete') {
      await apiClient.post(API.TABLE_COMPLETE(storeId, confirmModal.id));
      await fetchOrders();
    } else if (confirmModal.type === 'deleteOrder') {
      await deleteOrder(confirmModal.id);
    }
    setConfirmModal(null);
  };

  const handleStatusChange = async (orderId: number, status: OrderStatus) => {
    await updateOrderStatus(orderId, status);
  };

  const handleDeleteOrder = (orderId: number) => {
    setConfirmModal({ type: 'deleteOrder', id: orderId });
  };

  const selectedTable = tables.find(t => t.id === selectedTableId);

  return (
    <div className="min-h-screen bg-gray-100">
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 py-3 flex items-center justify-between">
          <h1 className="text-xl font-bold text-gray-900">주문 대시보드</h1>
          <div className="flex items-center gap-3">
            <button onClick={() => navigate('/tables')}
              className="rounded bg-gray-100 px-3 py-2 text-sm hover:bg-gray-200 min-h-[44px]"
              data-testid="dashboard-table-manage-button">테이블 관리</button>
            <button onClick={() => navigate('/menus')}
              className="rounded bg-gray-100 px-3 py-2 text-sm hover:bg-gray-200 min-h-[44px]"
              data-testid="dashboard-menu-manage-button">메뉴 관리</button>
            <button onClick={() => fetchOrders()}
              className="rounded bg-gray-100 p-2 hover:bg-gray-200 min-w-[44px] min-h-[44px] flex items-center justify-center"
              data-testid="dashboard-refresh-button"><RefreshCw size={18} /></button>
            <button onClick={logout}
              className="rounded bg-red-50 p-2 text-red-600 hover:bg-red-100 min-w-[44px] min-h-[44px] flex items-center justify-center"
              data-testid="dashboard-logout-button"><LogOut size={18} /></button>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 py-6">
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4" data-testid="dashboard-grid">
          {tables.map(table => (
            <TableCard
              key={table.id}
              table={table}
              orders={getOrdersForTable(table.id)}
              isNew={newOrderTableIds.has(table.id)}
              onTableClick={setSelectedTableId}
              onCompleteTable={handleCompleteTable}
            />
          ))}
          {tables.length === 0 && (
            <div className="col-span-full text-center py-12 text-gray-400">
              등록된 테이블이 없습니다. 테이블 관리에서 테이블을 추가해주세요.
            </div>
          )}
        </div>
      </main>

      {selectedTableId && selectedTable && (
        <OrderDetail
          orders={getOrdersForTable(selectedTableId)}
          tableNumber={selectedTable.tableNumber}
          onClose={() => setSelectedTableId(null)}
          onStatusChange={handleStatusChange}
          onDeleteOrder={handleDeleteOrder}
        />
      )}

      {confirmModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50" data-testid="confirm-modal">
          <div className="bg-white rounded-lg p-6 max-w-sm w-full mx-4">
            <h3 className="text-lg font-bold mb-2">
              {confirmModal.type === 'complete' ? '이용 완료 처리' : '주문 삭제'}
            </h3>
            <p className="text-gray-600 mb-4">
              {confirmModal.type === 'complete'
                ? '이 테이블의 이용을 완료 처리하시겠습니까? 현재 주문이 과거 내역으로 이동됩니다.'
                : '이 주문을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.'}
            </p>
            <div className="flex gap-2 justify-end">
              <button onClick={() => setConfirmModal(null)}
                className="rounded bg-gray-200 px-4 py-2 text-sm hover:bg-gray-300 min-h-[44px]"
                data-testid="confirm-modal-cancel">취소</button>
              <button onClick={handleConfirm}
                className="rounded bg-red-600 px-4 py-2 text-sm text-white hover:bg-red-700 min-h-[44px]"
                data-testid="confirm-modal-confirm">확인</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
