import type { Category } from '../../types';

interface CategorySidebarProps {
  categories: Category[];
  selectedId: number | null;
  onSelect: (categoryId: number) => void;
}

export function CategorySidebar({ categories, selectedId, onSelect }: CategorySidebarProps) {
  return (
    <nav
      className="w-32 bg-white border-r border-gray-200 overflow-y-auto flex-shrink-0"
      aria-label="카테고리"
      data-testid="category-sidebar"
    >
      <ul className="py-2">
        {categories.map((category) => (
          <li key={category.id}>
            <button
              onClick={() => onSelect(category.id)}
              className={`w-full px-3 py-4 text-left text-sm font-medium min-h-touch transition-colors ${
                selectedId === category.id
                  ? 'bg-primary-50 text-primary-700 border-r-2 border-primary-600'
                  : 'text-gray-600 hover:bg-gray-50'
              }`}
              data-testid={`category-tab-${category.id}`}
            >
              {category.name}
            </button>
          </li>
        ))}
      </ul>
    </nav>
  );
}
