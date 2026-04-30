import { useEffect } from 'react';
import { CategorySidebar } from '../components/layout/CategorySidebar';
import { MenuGrid } from '../components/menu/MenuGrid';
import { MenuSkeleton } from '../components/menu/MenuSkeleton';
import { useMenuStore } from '../store/menuStore';
import { useAuthStore } from '../store/authStore';
import { useCart } from '../hooks/useCart';

export default function MenuPage() {
  const authInfo = useAuthStore((s) => s.authInfo);
  const {
    categories,
    menus,
    selectedCategoryId,
    expandedMenuId,
    isLoading,
    error,
    fetchCategories,
    fetchMenus,
    selectCategory,
    expandMenu,
  } = useMenuStore();
  const { addItem } = useCart();

  useEffect(() => {
    if (authInfo?.storeId) {
      fetchCategories(authInfo.storeId);
    }
  }, [authInfo?.storeId, fetchCategories]);

  const handleCategorySelect = (categoryId: number) => {
    selectCategory(categoryId);
    if (authInfo?.storeId) {
      fetchMenus(authInfo.storeId, categoryId);
    }
  };

  return (
    <div className="flex flex-1 overflow-hidden">
      <CategorySidebar
        categories={categories}
        selectedId={selectedCategoryId}
        onSelect={handleCategorySelect}
      />
      <main className="flex-1 overflow-y-auto bg-gray-50">
        {error && (
          <div className="p-4 text-center">
            <p className="text-red-500 mb-2">{error}</p>
            <button
              onClick={() => authInfo?.storeId && fetchCategories(authInfo.storeId)}
              className="px-4 py-2 text-primary-600 hover:underline min-h-touch"
            >
              다시 시도
            </button>
          </div>
        )}
        {isLoading ? (
          <MenuSkeleton />
        ) : (
          <MenuGrid
            menus={menus}
            expandedMenuId={expandedMenuId}
            onMenuClick={(menuId) => expandMenu(menuId)}
            onAddToCart={addItem}
          />
        )}
      </main>
    </div>
  );
}
