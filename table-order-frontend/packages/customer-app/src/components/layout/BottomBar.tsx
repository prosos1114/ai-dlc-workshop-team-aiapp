import { useNavigate, useLocation } from 'react-router-dom';
import { UtensilsCrossed, ClipboardList } from 'lucide-react';
import { formatPrice } from '@table-order/shared';

interface BottomBarProps {
  totalAmount: number;
  cartQuantity: number;
  onOrderClick: () => void;
}

export function BottomBar({ totalAmount, cartQuantity, onOrderClick }: BottomBarProps) {
  const navigate = useNavigate();
  const location = useLocation();

  return (
    <div className="flex items-center justify-between px-4 py-2 bg-white border-t border-gray-200 shadow-sm" data-testid="bottom-bar">
      <div className="flex gap-2">
        <button
          onClick={() => navigate('/menu')}
          className={`flex items-center gap-2 px-4 py-3 rounded-lg min-h-touch text-sm font-medium ${
            location.pathname === '/menu'
              ? 'bg-primary-50 text-primary-700'
              : 'text-gray-600 hover:bg-gray-50'
          }`}
          data-testid="nav-menu-button"
        >
          <UtensilsCrossed className="w-5 h-5" />
          메뉴
        </button>
        <button
          onClick={() => navigate('/order-history')}
          className={`flex items-center gap-2 px-4 py-3 rounded-lg min-h-touch text-sm font-medium ${
            location.pathname === '/order-history'
              ? 'bg-primary-50 text-primary-700'
              : 'text-gray-600 hover:bg-gray-50'
          }`}
          data-testid="nav-history-button"
        >
          <ClipboardList className="w-5 h-5" />
          주문내역
        </button>
      </div>
      <button
        onClick={onOrderClick}
        disabled={cartQuantity === 0}
        className="px-6 py-3 bg-primary-600 text-white rounded-lg min-h-touch text-sm font-semibold hover:bg-primary-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
        data-testid="order-button"
      >
        주문하기 {totalAmount > 0 && formatPrice(totalAmount)}
      </button>
    </div>
  );
}
