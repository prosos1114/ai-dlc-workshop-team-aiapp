import { useState, useCallback } from 'react';
import { apiClient, API } from '@table-order/shared';
import type { Menu, Category, ApiResponse } from '@table-order/shared';

export function useMenus(storeId: number | null) {
  const [menus, setMenus] = useState<Menu[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchMenus = useCallback(async (categoryId?: number) => {
    if (!storeId) return;
    setLoading(true);
    setError(null);
    try {
      const params = categoryId ? { categoryId } : {};
      const { data } = await apiClient.get<ApiResponse<Menu[]>>(
        API.MENUS(storeId), { params }
      );
      setMenus(data.data);
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })
        ?.response?.data?.message ?? '메뉴를 불러오는데 실패했습니다';
      setError(message);
    } finally {
      setLoading(false);
    }
  }, [storeId]);

  const fetchCategories = useCallback(async () => {
    if (!storeId) return;
    try {
      const { data } = await apiClient.get<ApiResponse<Category[]>>(API.CATEGORIES(storeId));
      setCategories(data.data);
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })
        ?.response?.data?.message ?? '카테고리를 불러오는데 실패했습니다';
      setError(message);
    }
  }, [storeId]);

  const createMenu = useCallback(async (menuData: Partial<Menu>) => {
    if (!storeId) return;
    try {
      const { data } = await apiClient.post<ApiResponse<Menu>>(API.MENUS(storeId), menuData);
      setMenus(prev => [...prev, data.data]);
      return data.data;
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })
        ?.response?.data?.message ?? '메뉴 등록에 실패했습니다';
      setError(message);
      throw err;
    }
  }, [storeId]);

  const updateMenu = useCallback(async (menuId: number, menuData: Partial<Menu>) => {
    if (!storeId) return;
    try {
      const { data } = await apiClient.put<ApiResponse<Menu>>(API.MENU(storeId, menuId), menuData);
      setMenus(prev => prev.map(m => m.id === menuId ? data.data : m));
      return data.data;
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })
        ?.response?.data?.message ?? '메뉴 수정에 실패했습니다';
      setError(message);
      throw err;
    }
  }, [storeId]);

  const deleteMenu = useCallback(async (menuId: number) => {
    if (!storeId) return;
    try {
      await apiClient.delete(API.MENU(storeId, menuId));
      setMenus(prev => prev.filter(m => m.id !== menuId));
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })
        ?.response?.data?.message ?? '메뉴 삭제에 실패했습니다';
      setError(message);
      throw err;
    }
  }, [storeId]);

  const updateMenuOrder = useCallback(async (orderData: { menuId: number; displayOrder: number }[]) => {
    if (!storeId) return;
    try {
      await apiClient.put(API.MENU_ORDER(storeId), orderData);
      await fetchMenus();
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })
        ?.response?.data?.message ?? '메뉴 순서 변경에 실패했습니다';
      setError(message);
      throw err;
    }
  }, [storeId, fetchMenus]);

  const uploadImage = useCallback(async (menuId: number, file: File) => {
    if (!storeId) return;
    try {
      const formData = new FormData();
      formData.append('file', file);
      const { data } = await apiClient.post<ApiResponse<{ url: string }>>(
        API.MENU_IMAGE(storeId, menuId), formData,
        { headers: { 'Content-Type': 'multipart/form-data' } }
      );
      setMenus(prev => prev.map(m => m.id === menuId ? { ...m, imageUrl: data.data.url } : m));
      return data.data.url;
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })
        ?.response?.data?.message ?? '이미지 업로드에 실패했습니다';
      setError(message);
      throw err;
    }
  }, [storeId]);

  const createCategory = useCallback(async (name: string) => {
    if (!storeId) return;
    try {
      const { data } = await apiClient.post<ApiResponse<Category>>(
        API.CATEGORIES(storeId), { name }
      );
      setCategories(prev => [...prev, data.data]);
      return data.data;
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })
        ?.response?.data?.message ?? '카테고리 생성에 실패했습니다';
      setError(message);
      throw err;
    }
  }, [storeId]);

  return {
    menus, categories, loading, error,
    fetchMenus, fetchCategories, createMenu, updateMenu, deleteMenu,
    updateMenuOrder, uploadImage, createCategory
  };
}
