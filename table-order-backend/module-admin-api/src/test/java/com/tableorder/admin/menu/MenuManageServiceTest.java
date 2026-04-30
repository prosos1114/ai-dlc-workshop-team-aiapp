package com.tableorder.admin.menu;

import com.tableorder.admin.menu.dto.CategoryResponse;
import com.tableorder.admin.menu.dto.MenuResponse;
import com.tableorder.admin.s3.S3Service;
import com.tableorder.core.exception.NotFoundException;
import com.tableorder.domain.menu.Category;
import com.tableorder.domain.menu.CategoryRepository;
import com.tableorder.domain.menu.Menu;
import com.tableorder.domain.menu.MenuRepository;
import com.tableorder.domain.store.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MenuManageServiceTest {

    @Mock private MenuRepository menuRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private StoreRepository storeRepository;
    @Mock private S3Service s3Service;

    @InjectMocks private MenuManageService menuManageService;

    @Test
    @DisplayName("메뉴 생성 성공")
    void createMenu_success() {
        Category category = Category.builder().storeId(1L).name("음료").displayOrder(0).build();
        ReflectionTestUtils.setField(category, "id", 1L);

        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
        given(menuRepository.countByStoreIdAndCategoryId(1L, 1L)).willReturn(0);

        MenuResponse result = menuManageService.createMenu(1L, 1L, "아메리카노", 4500, "설명");

        assertThat(result.name()).isEqualTo("아메리카노");
        assertThat(result.price()).isEqualTo(4500);
        assertThat(result.categoryName()).isEqualTo("음료");
        verify(menuRepository).save(any(Menu.class));
    }

    @Test
    @DisplayName("존재하지 않는 카테고리로 메뉴 생성 시 NotFoundException")
    void createMenu_categoryNotFound() {
        given(categoryRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> menuManageService.createMenu(1L, 99L, "메뉴", 1000, null))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("메뉴 삭제 성공")
    void deleteMenu_success() {
        Menu menu = Menu.builder().storeId(1L).categoryId(1L).name("메뉴").price(1000).build();
        ReflectionTestUtils.setField(menu, "id", 1L);
        given(menuRepository.findByIdAndStoreId(1L, 1L)).willReturn(Optional.of(menu));

        menuManageService.deleteMenu(1L, 1L);

        verify(menuRepository).delete(menu);
    }

    @Test
    @DisplayName("카테고리 생성 성공")
    void createCategory_success() {
        given(categoryRepository.findByStoreIdOrderByDisplayOrder(1L)).willReturn(List.of());

        CategoryResponse result = menuManageService.createCategory(1L, "음료");

        assertThat(result.name()).isEqualTo("음료");
        assertThat(result.displayOrder()).isEqualTo(0);
        verify(categoryRepository).save(any(Category.class));
    }
}
