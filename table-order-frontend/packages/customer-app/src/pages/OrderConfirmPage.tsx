import { useNavigate } from 'react-router-dom';
import { formatPrice } from '@table-order/shared';
import { useCart } from '../hooks/useCart';
import { useOrders } from '../hooks/useOrders';
import { useAuthStore } from '../store/authStore';
import { LoadingSpinner } from '../components/common/LoadingSpinner';

export default function OrderConfirmPage() {
  const navigate = useNavigate();
  const authInfo = useAuthStore((s) => s.authInfo);
  const { items, totalAmount } = useCart();
  const { isSubmitting, failureCount, createOrder } = useOrders();

  const handleConfirm = async () => {
    const result = await createOrder();
    if (result.success) {
      navigate('/order-success', {
        state: {
          orderNumber: result.orderNumber,
          totalAmount: result.totalAmount,
          createdAt: result.createdAt,
        },
        replace: true,
      });
    }
  };

  if (items.length === 0) {
    navigate('/menu', { replace: true });
    return null;
  }

  return (
    <div className="flex-1 flex flex-col overflow-y-auto bg-gray-50 p-6">
      <h2 className="text-xl font-bold text-gray-800 mb-4">주문 확인</h2>

      <div className="bg-white rounded-xl shadow-sm p-4 mb-4">
        <p className="text-sm text-gray-500 mb-3">
          테이블 <span className="font-semibold text-gray-700">{authInfo?.tableNumber}번</span>
        </p>
        <div className="divide-y divide-gray-100" data-testid="order-summary">
          {items.map((item) => (
            <div key={item.menuId} className="flex justify-between py-3" data-testid={`order-summary-item-${item.menuId}`}>
              <div>
                <p className="text-sm font-medium text-gray-800">{item.menuName}</p>
                <p className="text-xs text-gray-500">{formatPrice(item.unitPrice)} × {item.quantity}</p>
              </div>
              <p className="text-sm font-semibold text-gray-800">
                {formatPrice(item.unitPrice * item.quantity)}
              </p>
            </div>
          ))}
        </div>
      </div>

      <div className="bg-white rounded-xl shadow-sm p-4 mb-6">
        <div className="flex justify-between items-center">
          <span className="text-base font-medium text-gray-700">총 금액</span>
          <span className="text-xl font-bold text-primary-700">{formatPrice(totalAmount)}</span>
        </div>
      </div>

      {failureCount >= 3 && (
        <p className="text-center text-red-500 text-sm mb-4">
          주문이 반복적으로 실패하고 있습니다. 직원에게 문의해주세요.
        </p>
      )}

      <div className="flex gap-3 mt-auto">
        <button
          onClick={() => navigate(-1)}
          className="flex-1 px-4 py-3 border border-gray-300 rounded-lg text-gray-700 font-medium min-h-touch hover:bg-gray-50"
          data-testid="order-back-button"
        >
          뒤로가기
        </button>
        <button
          onClick={handleConfirm}
          disabled={isSubmitting}
          className="flex-1 px-4 py-3 bg-primary-600 text-white rounded-lg font-semibold min-h-touch hover:bg-primary-700 disabled:bg-gray-300"
          data-testid="order-confirm-button"
        >
          {isSubmitting ? <LoadingSpinner size="sm" message="주문 처리 중..." /> : '주문 확정'}
        </button>
      </div>
    </div>
  );
}
