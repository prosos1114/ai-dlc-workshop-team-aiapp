import { create } from 'zustand';
import type { Category, Menu } from '../types';
import { fetchCategories as fetchCategoriesApi, fetchMenus as fetchMenusApi } from '../api/menu';

interface MenuState {
  categories: Category[];
  menus: Menu[];
  selectedCategoryId: number | null;
  expandedMenuId: number | null;
  isLoading: boolean;
  error: string | null;
  menuCache: Record<number, Menu[]>;
}

interface MenuActions {
  fetchCategories: (storeId: number) => Promise<void>;
  fetchMenus: (storeId: number, categoryId: number) => Promise<void>;
  selectCategory: (categoryId: number) => void;
  expandMenu: (menuId: number | null) => void;
}

export type MenuStore = MenuState & MenuActions;

export const useMenuStore = create<MenuStore>()((set, get) => ({
  categories: [],
  menus: [],
  selectedCategoryId: null,
  expandedMenuId: null,
  isLoading: false,
  error: null,
  menuCache: {},

  fetchCategories: async (storeId) => {
    set({ isLoading: true, error: null });
    try {
      const categories = await fetchCategoriesApi(storeId);
      set({ categories, isLoading: false });
      if (categories.length > 0 && !get().selectedCategoryId) {
        const firstCategoryId = categories[0].id;
        set({ selectedCategoryId: firstCategoryId });
        await get().fetchMenus(storeId, firstCategoryId);
      }
    } catch {
      set({ error: '카테고리를 불러올 수 없습니다', isLoading: false });
    }
  },

  fetchMenus: async (storeId, categoryId) => {
    const cached = get().menuCache[categoryId];
    if (cached) {
      set({ menus: cached, isLoading: false });
      return;
    }
    set({ isLoading: true, error: null });
    try {
      const menus = await fetchMenusApi(storeId, categoryId);
      set((state) => ({
        menus,
        menuCache: { ...state.menuCache, [categoryId]: menus },
        isLoading: false,
      }));
    } catch {
      set({ error: '메뉴를 불러올 수 없습니다', isLoading: false });
    }
  },

  selectCategory: (categoryId) =>
    set({ selectedCategoryId: categoryId, expandedMenuId: null }),

  expandMenu: (menuId) =>
    set((state) => ({
      expandedMenuId: state.expandedMenuId === menuId ? null : menuId,
    })),
}));
