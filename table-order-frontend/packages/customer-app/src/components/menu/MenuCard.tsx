import React from 'react';
import { Plus } from 'lucide-react';
import { formatPrice } from '@table-order/shared';
import { OptimizedImage } from '../common/OptimizedImage';
import type { Menu } from '../../types';

interface MenuCardProps {
  menu: Menu;
  isExpanded: boolean;
  onClick: () => void;
  onAddToCart: () => void;
}

export const MenuCard = React.memo(function MenuCard({
  menu,
  isExpanded,
  onClick,
  onAddToCart,
}: MenuCardProps) {
  const handleAddToCart = (e: React.MouseEvent) => {
    e.stopPropagation();
    onAddToCart();
  };

  return (
    <div
      className={`bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden cursor-pointer transition-all duration-300 ${
        isExpanded ? 'col-span-3 row-span-2' : ''
      }`}
      onClick={onClick}
      data-testid={`menu-card-${menu.id}`}
      role="button"
      tabIndex={0}
      onKeyDown={(e) => e.key === 'Enter' && onClick()}
    >
      <OptimizedImage
        src={menu.imageUrl}
        alt={menu.name}
        className={isExpanded ? 'h-48' : 'h-32'}
      />
      <div className="p-3">
        <h3 className="font-medium text-gray-800 text-sm truncate">{menu.name}</h3>
        <p className="text-primary-700 font-bold text-sm mt-1">{formatPrice(menu.price)}</p>

        {isExpanded && (
          <div className="mt-3 animate-fade-in">
            {menu.description && (
              <p className="text-gray-500 text-sm mb-3 line-clamp-3">{menu.description}</p>
            )}
            <button
              onClick={handleAddToCart}
              className="w-full flex items-center justify-center gap-2 px-4 py-3 bg-primary-600 text-white rounded-lg font-medium min-h-touch hover:bg-primary-700 transition-colors"
              data-testid={`menu-card-add-button-${menu.id}`}
            >
              <Plus className="w-5 h-5" />
              장바구니 담기
            </button>
          </div>
        )}
      </div>
    </div>
  );
});
