import { Outlet, useNavigate } from 'react-router-dom';
import { Header } from './Header';
import { BottomBar } from './BottomBar';
import { CartSlidePanel } from '../cart/CartSlidePanel';
import { useCart } from '../../hooks/useCart';
import { useAuthStore } from '../../store/authStore';

export function AppLayout() {
  const authInfo = useAuthStore((s) => s.authInfo);
  const { totalAmount, totalQuantity, isPanelOpen, openPanel, closePanel, items, addItem, removeItem, incrementQuantity, decrementQuantity, clearCart } = useCart();
  const navigate = useNavigate();

  const handleOrderClick = () => {
    if (totalQuantity > 0) {
      navigate('/order-confirm');
    }
  };

  return (
    <div className="flex flex-col h-screen" data-testid="app-layout">
      <Header
        tableNumber={authInfo?.tableNumber ?? 0}
        cartQuantity={totalQuantity}
        onCartClick={openPanel}
      />
      <div className="flex flex-1 overflow-hidden relative">
        <Outlet />
        <CartSlidePanel
          isOpen={isPanelOpen}
          onClose={closePanel}
          items={items}
          totalAmount={totalAmount}
          onRemoveItem={removeItem}
          onIncrement={incrementQuantity}
          onDecrement={decrementQuantity}
          onClearAll={clearCart}
          onOrder={handleOrderClick}
          onAddItem={addItem}
        />
      </div>
      <BottomBar
        totalAmount={totalAmount}
        cartQuantity={totalQuantity}
        onOrderClick={handleOrderClick}
      />
    </div>
  );
}
