package com.tableorder.admin.menu;

import com.tableorder.admin.menu.dto.CategoryResponse;
import com.tableorder.admin.menu.dto.MenuOrderUpdateRequest;
import com.tableorder.admin.menu.dto.MenuResponse;
import com.tableorder.admin.s3.S3Service;
import com.tableorder.core.exception.BusinessException;
import com.tableorder.core.exception.NotFoundException;
import com.tableorder.domain.menu.Category;
import com.tableorder.domain.menu.CategoryRepository;
import com.tableorder.domain.menu.Menu;
import com.tableorder.domain.menu.MenuRepository;
import com.tableorder.domain.store.Store;
import com.tableorder.domain.store.StoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MenuManageService {

    private static final Logger log = LoggerFactory.getLogger(MenuManageService.class);
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;
    private final S3Service s3Service;

    public MenuManageService(MenuRepository menuRepository,
                             CategoryRepository categoryRepository,
                             StoreRepository storeRepository,
                             S3Service s3Service) {
        this.menuRepository = menuRepository;
        this.categoryRepository = categoryRepository;
        this.storeRepository = storeRepository;
        this.s3Service = s3Service;
    }

    @Transactional(readOnly = true)
    public List<MenuResponse> getMenusByStore(Long storeId, Long categoryId) {
        Map<Long, String> categoryNames = getCategoryNameMap(storeId);

        List<Menu> menus;
        if (categoryId != null) {
            menus = menuRepository.findByStoreIdAndCategoryIdOrderByDisplayOrder(storeId, categoryId);
        } else {
            menus = menuRepository.findByStoreIdOrderByDisplayOrder(storeId);
        }

        return menus.stream()
                .map(menu -> MenuResponse.from(menu, categoryNames.getOrDefault(menu.getCategoryId(), "")))
                .toList();
    }

    @Transactional
    public MenuResponse createMenu(Long storeId, Long categoryId, String name,
                                    int price, String description) {
        Category category = findCategoryByIdAndStore(categoryId, storeId);
        int displayOrder = menuRepository.countByStoreIdAndCategoryId(storeId, categoryId);

        Menu menu = Menu.builder()
                .storeId(storeId)
                .categoryId(categoryId)
                .name(name)
                .price(price)
                .description(description)
                .displayOrder(displayOrder)
                .build();

        menuRepository.save(menu);
        log.info("Menu created: storeId={}, name={}", storeId, name);
        return MenuResponse.from(menu, category.getName());
    }

    @Transactional
    public MenuResponse updateMenu(Long storeId, Long menuId, Long categoryId,
                                    String name, int price, String description) {
        Menu menu = findMenuByIdAndStore(menuId, storeId);
        Category category = findCategoryByIdAndStore(categoryId, storeId);

        menu.update(name, price, description, categoryId);
        menuRepository.save(menu);
        log.info("Menu updated: menuId={}", menuId);
        return MenuResponse.from(menu, category.getName());
    }

    @Transactional
    public void deleteMenu(Long storeId, Long menuId) {
        Menu menu = findMenuByIdAndStore(menuId, storeId);
        menuRepository.delete(menu);
        log.info("Menu deleted: menuId={}", menuId);
    }

    @Transactional
    public void updateMenuOrder(Long storeId, List<MenuOrderUpdateRequest.MenuOrderItem> menuOrders) {
        for (var item : menuOrders) {
            Menu menu = findMenuByIdAndStore(item.menuId(), storeId);
            menu.updateDisplayOrder(item.displayOrder());
        }
        log.info("Menu order updated: storeId={}, count={}", storeId, menuOrders.size());
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories(Long storeId) {
        List<Category> categories = categoryRepository.findByStoreIdOrderByDisplayOrder(storeId);
        return categories.stream()
                .map(cat -> CategoryResponse.from(cat,
                        menuRepository.countByStoreIdAndCategoryId(storeId, cat.getId())))
                .toList();
    }

    @Transactional
    public CategoryResponse createCategory(Long storeId, String name) {
        int displayOrder = categoryRepository.findByStoreIdOrderByDisplayOrder(storeId).size();

        Category category = Category.builder()
                .storeId(storeId)
                .name(name)
                .displayOrder(displayOrder)
                .build();

        categoryRepository.save(category);
        log.info("Category created: storeId={}, name={}", storeId, name);
        return CategoryResponse.from(category, 0);
    }

    @Transactional
    public String uploadImage(Long storeId, Long menuId, MultipartFile file) {
        Menu menu = findMenuByIdAndStore(menuId, storeId);
        validateImageFile(file);

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException("Store", storeId));

        String keyPrefix = store.getCode() + "/menus/" + menuId;
        String imageUrl = s3Service.upload(keyPrefix, file);

        menu.updateImageUrl(imageUrl);
        menuRepository.save(menu);
        log.info("Menu image uploaded: menuId={}", menuId);
        return imageUrl;
    }

    private Menu findMenuByIdAndStore(Long menuId, Long storeId) {
        return menuRepository.findByIdAndStoreId(menuId, storeId)
                .orElseThrow(() -> new NotFoundException("Menu", menuId));
    }

    private Category findCategoryByIdAndStore(Long categoryId, Long storeId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category", categoryId));
        if (!category.getStoreId().equals(storeId)) {
            throw new NotFoundException("Category", categoryId);
        }
        return category;
    }

    private Map<Long, String> getCategoryNameMap(Long storeId) {
        return categoryRepository.findByStoreIdOrderByDisplayOrder(storeId).stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileException("파일이 비어있습니다");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new InvalidFileException("허용되지 않는 파일 형식입니다. JPEG, PNG, WebP만 허용됩니다");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException("파일 크기가 5MB를 초과합니다");
        }
    }

    private static class InvalidFileException extends BusinessException {
        InvalidFileException(String message) {
            super("INVALID_FILE", message, HttpStatus.BAD_REQUEST);
        }
    }
}
