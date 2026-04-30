package com.tableorder.customer.menu;

import com.tableorder.core.exception.NotFoundException;
import com.tableorder.customer.menu.dto.CategoryResponse;
import com.tableorder.customer.menu.dto.MenuResponse;
import com.tableorder.domain.menu.CategoryRepository;
import com.tableorder.domain.menu.Menu;
import com.tableorder.domain.menu.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerMenuService {

    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;

    public CustomerMenuService(MenuRepository menuRepository, CategoryRepository categoryRepository) {
        this.menuRepository = menuRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<MenuResponse> getMenus(Long storeId, Long categoryId) {
        List<Menu> menus;
        if (categoryId != null) {
            menus = menuRepository.findByStoreIdAndCategoryIdOrderByDisplayOrder(storeId, categoryId);
        } else {
            menus = menuRepository.findByStoreIdOrderByDisplayOrder(storeId);
        }
        return menus.stream().map(MenuResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public MenuResponse getMenu(Long storeId, Long menuId) {
        Menu menu = menuRepository.findByIdAndStoreId(menuId, storeId)
                .orElseThrow(() -> new NotFoundException("메뉴", menuId));
        return MenuResponse.from(menu);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories(Long storeId) {
        return categoryRepository.findByStoreIdOrderByDisplayOrder(storeId)
                .stream().map(CategoryResponse::from).toList();
    }
}
