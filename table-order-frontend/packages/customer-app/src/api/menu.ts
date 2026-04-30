import { apiClient, API } from '@table-order/shared';
import type { ApiResponse, Category, Menu } from '../types';

export async function fetchCategories(storeId: number): Promise<Category[]> {
  const response = await apiClient.get<ApiResponse<Category[]>>(
    API.CATEGORIES(storeId)
  );
  return response.data.data;
}

export async function fetchMenus(storeId: number, categoryId?: number): Promise<Menu[]> {
  const params = categoryId ? { categoryId } : {};
  const response = await apiClient.get<ApiResponse<Menu[]>>(
    API.MENUS(storeId),
    { params }
  );
  return response.data.data;
}

export async function fetchMenu(storeId: number, menuId: number): Promise<Menu> {
  const response = await apiClient.get<ApiResponse<Menu>>(
    API.MENU(storeId, menuId)
  );
  return response.data.data;
}
