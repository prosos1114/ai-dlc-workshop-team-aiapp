import { ShoppingCart } from 'lucide-react';

interface HeaderProps {
  tableNumber: number;
  cartQuantity: number;
  onCartClick: () => void;
}

export function Header({ tableNumber, cartQuantity, onCartClick }: HeaderProps) {
  return (
    <header className="flex items-center justify-between px-4 py-3 bg-white border-b border-gray-200 shadow-sm" data-testid="app-header">
      <div className="text-lg font-bold text-primary-700">테이블오더</div>
      <div className="text-sm font-medium text-gray-600" data-testid="table-info">
        테이블 {tableNumber}번
      </div>
      <button
        onClick={onCartClick}
        className="relative p-2 min-w-touch min-h-touch flex items-center justify-center rounded-lg hover:bg-gray-100"
        aria-label={`장바구니 ${cartQuantity}개`}
        data-testid="cart-badge"
      >
        <ShoppingCart className="w-6 h-6 text-gray-700" />
        {cartQuantity > 0 && (
          <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs font-bold rounded-full w-5 h-5 flex items-center justify-center">
            {cartQuantity > 99 ? '99+' : cartQuantity}
          </span>
        )}
      </button>
    </header>
  );
}
