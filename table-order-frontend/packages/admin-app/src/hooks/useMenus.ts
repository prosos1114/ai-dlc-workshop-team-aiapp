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
        `/api/admin/stores/${storeId}/menus`, { params }
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
      const { data } = await apiClient.get<ApiResponse<Category[]>>(`/api/admin/stores/${storeId}/categories`);
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
      const { data } = await apiClient.post<ApiResponse<Menu>>(`/api/admin/stores/${storeId}/menus`, menuData);
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
      const { data } = await apiClient.put<ApiResponse<Menu>>(`/api/admin/stores/${storeId}/menus/${menuId}`, menuData);
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
      await apiClient.delete(`/api/admin/stores/${storeId}/menus/${menuId}`);
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
      await apiClient.put(`/api/admin/stores/${storeId}/menus/order`, orderData);
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
        `/api/admin/stores/${storeId}/menus/${menuId}/image`, formData,
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
        `/api/admin/stores/${storeId}/categories`, { name }
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
