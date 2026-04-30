import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { formatPrice } from '@table-order/shared';
import type { Menu, Category } from '@table-order/shared';
import { useAdminAuth } from '../hooks/useAdminAuth';
import { useMenus } from '../hooks/useMenus';
import { ArrowLeft, Plus, Pencil, Trash2, X } from 'lucide-react';

const menuSchema = z.object({
  name: z.string().min(1, '메뉴명을 입력해주세요').max(100),
  price: z.coerce.number().min(0, '가격은 0 이상이어야 합니다').max(10000000),
  description: z.string().max(500).optional(),
  categoryId: z.coerce.number().min(1, '카테고리를 선택해주세요'),
});

type MenuFormData = z.infer<typeof menuSchema>;

export function MenuManagePage() {
  const navigate = useNavigate();
  const { getStoreId } = useAdminAuth();
  const storeId = getStoreId();
  const { menus, categories, fetchMenus, fetchCategories, createMenu, updateMenu, deleteMenu, createCategory, error } = useMenus(storeId);

  const [editingMenu, setEditingMenu] = useState<Menu | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [showCategoryForm, setShowCategoryForm] = useState(false);
  const [newCategoryName, setNewCategoryName] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<number | undefined>();
  const [confirmDelete, setConfirmDelete] = useState<number | null>(null);

  const { register, handleSubmit, reset, setValue, formState: { errors } } = useForm<MenuFormData>({
    resolver: zodResolver(menuSchema),
  });

  useEffect(() => {
    fetchMenus();
    fetchCategories();
  }, [fetchMenus, fetchCategories]);

  const onSubmit = async (data: MenuFormData) => {
    if (editingMenu) {
      await updateMenu(editingMenu.id, data);
    } else {
      await createMenu(data);
    }
    setShowForm(false);
    setEditingMenu(null);
    reset();
  };

  const startEdit = (menu: Menu) => {
    setEditingMenu(menu);
    setValue('name', menu.name);
    setValue('price', menu.price);
    setValue('description', menu.description ?? '');
    setValue('categoryId', menu.categoryId);
    setShowForm(true);
  };

  const handleDelete = async () => {
    if (confirmDelete) {
      await deleteMenu(confirmDelete);
      setConfirmDelete(null);
    }
  };

  const handleCreateCategory = async () => {
    if (newCategoryName.trim()) {
      await createCategory(newCategoryName.trim());
      setNewCategoryName('');
      setShowCategoryForm(false);
    }
  };

  const filteredMenus = selectedCategory
    ? menus.filter(m => m.categoryId === selectedCategory)
    : menus;

  const getCategoryName = (categoryId: number) =>
    categories.find(c => c.id === categoryId)?.name ?? '미분류';

  return (
    <div className="min-h-screen bg-gray-100">
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-4xl mx-auto px-4 py-3 flex items-center gap-3">
          <button onClick={() => navigate('/dashboard')} className="p-2 hover:bg-gray-100 rounded min-w-[44px] min-h-[44px] flex items-center justify-center"
            data-testid="menu-manage-back-button"><ArrowLeft size={20} /></button>
          <h1 className="text-xl font-bold">메뉴 관리</h1>
        </div>
      </header>

      <main className="max-w-4xl mx-auto px-4 py-6">
        {/* 카테고리 필터 */}
        <div className="flex items-center gap-2 mb-4 flex-wrap">
          <button onClick={() => setSelectedCategory(undefined)}
            className={`rounded-full px-3 py-1 text-sm min-h-[36px] ${!selectedCategory ? 'bg-blue-600 text-white' : 'bg-gray-200 hover:bg-gray-300'}`}
            data-testid="menu-manage-filter-all">전체</button>
          {categories.map(cat => (
            <button key={cat.id} onClick={() => setSelectedCategory(cat.id)}
              className={`rounded-full px-3 py-1 text-sm min-h-[36px] ${selectedCategory === cat.id ? 'bg-blue-600 text-white' : 'bg-gray-200 hover:bg-gray-300'}`}
              data-testid={`menu-manage-filter-${cat.id}`}>{cat.name}</button>
          ))}
          <button onClick={() => setShowCategoryForm(true)}
            className="rounded-full px-3 py-1 text-sm bg-gray-100 hover:bg-gray-200 min-h-[36px]"
            data-testid="menu-manage-add-category">+ 카테고리</button>
        </div>

        {showCategoryForm && (
          <div className="bg-white rounded-lg shadow-md p-4 mb-4 flex items-center gap-2" data-testid="category-form">
            <input value={newCategoryName} onChange={e => setNewCategoryName(e.target.value)}
              placeholder="카테고리명" className="flex-1 rounded-md border border-gray-300 px-3 py-2"
              data-testid="category-form-name" />
            <button onClick={handleCreateCategory} className="rounded bg-blue-600 px-4 py-2 text-white hover:bg-blue-700 min-h-[44px]"
              data-testid="category-form-submit">추가</button>
            <button onClick={() => setShowCategoryForm(false)} className="rounded bg-gray-200 px-4 py-2 hover:bg-gray-300 min-h-[44px]">취소</button>
          </div>
        )}

        {/* 메뉴 추가 버튼 */}
        <div className="flex justify-end mb-4">
          <button onClick={() => { setShowForm(true); setEditingMenu(null); reset(); }}
            className="flex items-center gap-1 rounded bg-blue-600 px-4 py-2 text-white hover:bg-blue-700 min-h-[44px]"
            data-testid="menu-manage-add-button"><Plus size={18} /> 메뉴 추가</button>
        </div>

        {/* 메뉴 폼 */}
        {showForm && (
          <div className="bg-white rounded-lg shadow-md p-4 mb-4" data-testid="menu-form">
            <h3 className="font-bold mb-3">{editingMenu ? '메뉴 수정' : '메뉴 등록'}</h3>
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-sm font-medium text-gray-700">메뉴명</label>
                  <input {...register('name')} className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2"
                    data-testid="menu-form-name" />
                  {errors.name && <p className="text-sm text-red-600">{errors.name.message}</p>}
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">가격 (원)</label>
                  <input type="number" {...register('price')} className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2"
                    data-testid="menu-form-price" />
                  {errors.price && <p className="text-sm text-red-600">{errors.price.message}</p>}
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">카테고리</label>
                <select {...register('categoryId')} className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2"
                  data-testid="menu-form-category">
                  <option value="">선택해주세요</option>
                  {categories.map(cat => <option key={cat.id} value={cat.id}>{cat.name}</option>)}
                </select>
                {errors.categoryId && <p className="text-sm text-red-600">{errors.categoryId.message}</p>}
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">설명</label>
                <textarea {...register('description')} rows={2} className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2"
                  data-testid="menu-form-description" />
              </div>
              {error && <p className="text-sm text-red-600">{error}</p>}
              <div className="flex gap-2 justify-end">
                <button type="button" onClick={() => { setShowForm(false); setEditingMenu(null); reset(); }}
                  className="rounded bg-gray-200 px-4 py-2 hover:bg-gray-300 min-h-[44px]">취소</button>
                <button type="submit" className="rounded bg-blue-600 px-4 py-2 text-white hover:bg-blue-700 min-h-[44px]"
                  data-testid="menu-form-submit">{editingMenu ? '수정' : '등록'}</button>
              </div>
            </form>
          </div>
        )}

        {/* 메뉴 목록 */}
        <div className="bg-white rounded-lg shadow-md overflow-hidden" data-testid="menu-list">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-4 py-3 text-left text-sm font-medium text-gray-700">메뉴명</th>
                <th className="px-4 py-3 text-left text-sm font-medium text-gray-700">카테고리</th>
                <th className="px-4 py-3 text-right text-sm font-medium text-gray-700">가격</th>
                <th className="px-4 py-3 text-right text-sm font-medium text-gray-700">작업</th>
              </tr>
            </thead>
            <tbody className="divide-y">
              {filteredMenus.map(menu => (
                <tr key={menu.id} data-testid={`menu-list-row-${menu.id}`}>
                  <td className="px-4 py-3">
                    <div className="flex items-center gap-2">
                      {menu.imageUrl && <img src={menu.imageUrl} alt={menu.name} className="w-10 h-10 rounded object-cover" />}
                      <span className="font-medium">{menu.name}</span>
                    </div>
                  </td>
                  <td className="px-4 py-3 text-sm text-gray-500">{getCategoryName(menu.categoryId)}</td>
                  <td className="px-4 py-3 text-right font-medium">{formatPrice(menu.price)}</td>
                  <td className="px-4 py-3 text-right">
                    <div className="flex items-center justify-end gap-1">
                      <button onClick={() => startEdit(menu)}
                        className="rounded p-1.5 hover:bg-gray-100 min-w-[36px] min-h-[36px] flex items-center justify-center"
                        data-testid={`menu-edit-${menu.id}`}><Pencil size={16} /></button>
                      <button onClick={() => setConfirmDelete(menu.id)}
                        className="rounded p-1.5 text-red-500 hover:bg-red-50 min-w-[36px] min-h-[36px] flex items-center justify-center"
                        data-testid={`menu-delete-${menu.id}`}><Trash2 size={16} /></button>
                    </div>
                  </td>
                </tr>
              ))}
              {filteredMenus.length === 0 && (
                <tr><td colSpan={4} className="px-4 py-8 text-center text-gray-400">등록된 메뉴가 없습니다</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </main>

      {confirmDelete && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50" data-testid="menu-delete-modal">
          <div className="bg-white rounded-lg p-6 max-w-sm w-full mx-4">
            <h3 className="text-lg font-bold mb-2">메뉴 삭제</h3>
            <p className="text-gray-600 mb-4">이 메뉴를 삭제하시겠습니까?</p>
            <div className="flex gap-2 justify-end">
              <button onClick={() => setConfirmDelete(null)} className="rounded bg-gray-200 px-4 py-2 hover:bg-gray-300 min-h-[44px]"
                data-testid="menu-delete-modal-cancel">취소</button>
              <button onClick={handleDelete} className="rounded bg-red-600 px-4 py-2 text-white hover:bg-red-700 min-h-[44px]"
                data-testid="menu-delete-modal-confirm">삭제</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
