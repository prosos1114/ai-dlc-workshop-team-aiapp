import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { CheckCircle } from 'lucide-react';
import { formatPrice, formatDate, ORDER_SUCCESS_REDIRECT_DELAY } from '@table-order/shared';

interface OrderSuccessState {
  orderNumber: string;
  totalAmount: number;
  createdAt: string;
}

export default function OrderSuccessPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const state = location.state as OrderSuccessState | null;
  const [countdown, setCountdown] = useState(ORDER_SUCCESS_REDIRECT_DELAY / 1000);

  useEffect(() => {
    if (!state) {
      navigate('/menu', { replace: true });
      return;
    }

    const timer = setInterval(() => {
      setCountdown((prev) => {
        if (prev <= 1) {
          clearInterval(timer);
          navigate('/menu', { replace: true });
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [state, navigate]);

  if (!state) return null;

  return (
    <div className="flex-1 flex flex-col items-center justify-center p-8 bg-gray-50" data-testid="order-success-page">
      <CheckCircle className="w-20 h-20 text-green-500 mb-4" />
      <h2 className="text-2xl font-bold text-gray-800 mb-2">주문이 완료되었습니다!</h2>

      <div className="bg-white rounded-xl shadow-sm p-6 mt-4 w-full max-w-sm text-center">
        <div className="mb-3">
          <p className="text-sm text-gray-500">주문 번호</p>
          <p className="text-lg font-bold text-primary-700" data-testid="order-success-number">
            {state.orderNumber}
          </p>
        </div>
        <div className="mb-3">
          <p className="text-sm text-gray-500">주문 시각</p>
          <p className="text-sm font-medium text-gray-700">{formatDate(state.createdAt)}</p>
        </div>
        <div>
          <p className="text-sm text-gray-500">총 금액</p>
          <p className="text-xl font-bold text-primary-700">{formatPrice(state.totalAmount)}</p>
        </div>
      </div>

      <p className="text-sm text-gray-400 mt-6">{countdown}초 후 메뉴 화면으로 이동합니다</p>

      <button
        onClick={() => navigate('/menu', { replace: true })}
        className="mt-4 px-6 py-3 bg-primary-600 text-white rounded-lg font-medium min-h-touch hover:bg-primary-700"
        data-testid="order-success-back-button"
      >
        메뉴로 돌아가기
      </button>
    </div>
  );
}
