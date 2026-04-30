import { MenuCard } from './MenuCard';
import type { Menu } from '../../types';

interface MenuGridProps {
  menus: Menu[];
  expandedMenuId: number | null;
  onMenuClick: (menuId: number) => void;
  onAddToCart: (menu: Menu) => void;
}

export function MenuGrid({ menus, expandedMenuId, onMenuClick, onAddToCart }: MenuGridProps) {
  if (menus.length === 0) {
    return (
      <div className="flex items-center justify-center h-full text-gray-400" data-testid="empty-menu">
        <p>등록된 메뉴가 없습니다</p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-3 gap-4 p-4 overflow-y-auto" data-testid="menu-grid">
      {menus.map((menu) => (
        <MenuCard
          key={menu.id}
          menu={menu}
          isExpanded={expandedMenuId === menu.id}
          onClick={() => onMenuClick(menu.id)}
          onAddToCart={() => onAddToCart(menu)}
        />
      ))}
    </div>
  );
}
