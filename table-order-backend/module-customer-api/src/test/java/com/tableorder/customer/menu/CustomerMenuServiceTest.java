package com.tableorder.customer.menu;

import com.tableorder.core.exception.NotFoundException;
import com.tableorder.customer.menu.dto.CategoryResponse;
import com.tableorder.customer.menu.dto.MenuResponse;
import com.tableorder.domain.menu.Category;
import com.tableorder.domain.menu.CategoryRepository;
import com.tableorder.domain.menu.Menu;
import com.tableorder.domain.menu.MenuRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerMenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private CustomerMenuService customerMenuService;

    @BeforeEach
    void setUp() {
        customerMenuService = new CustomerMenuService(menuRepository, categoryRepository);
    }

    @Test
    @DisplayName("매장의 전체 메뉴를 조회한다")
    void getMenus_allMenus_success() {
        Menu menu1 = Menu.builder().storeId(1L).categoryId(1L).name("아메리카노").price(4500).displayOrder(1).build();
        Menu menu2 = Menu.builder().storeId(1L).categoryId(1L).name("카페라떼").price(5500).displayOrder(2).build();
        ReflectionTestUtils.setField(menu1, "id", 1L);
        ReflectionTestUtils.setField(menu2, "id", 2L);

        when(menuRepository.findByStoreIdOrderByDisplayOrder(1L)).thenReturn(List.of(menu1, menu2));

        List<MenuResponse> result = customerMenuService.getMenus(1L, null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("아메리카노");
        assertThat(result.get(1).name()).isEqualTo("카페라떼");
    }

    @Test
    @DisplayName("카테고리별 메뉴를 조회한다")
    void getMenus_byCategory_success() {
        Menu menu = Menu.builder().storeId(1L).categoryId(2L).name("치즈케이크").price(7000).displayOrder(1).build();
        ReflectionTestUtils.setField(menu, "id", 10L);

        when(menuRepository.findByStoreIdAndCategoryIdOrderByDisplayOrder(1L, 2L)).thenReturn(List.of(menu));

        List<MenuResponse> result = customerMenuService.getMenus(1L, 2L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("치즈케이크");
        assertThat(result.get(0).categoryId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("메뉴가 없으면 빈 리스트를 반환한다")
    void getMenus_empty_returnsEmptyList() {
        when(menuRepository.findByStoreIdOrderByDisplayOrder(1L)).thenReturn(List.of());

        List<MenuResponse> result = customerMenuService.getMenus(1L, null);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("메뉴 상세를 조회한다")
    void getMenu_success() {
        Menu menu = Menu.builder()
                .storeId(1L).categoryId(1L).name("아메리카노")
                .price(4500).description("깊은 풍미").imageUrl("http://img.jpg").displayOrder(1)
                .build();
        ReflectionTestUtils.setField(menu, "id", 5L);

        when(menuRepository.findByIdAndStoreId(5L, 1L)).thenReturn(Optional.of(menu));

        MenuResponse result = customerMenuService.getMenu(1L, 5L);

        assertThat(result.id()).isEqualTo(5L);
        assertThat(result.name()).isEqualTo("아메리카노");
        assertThat(result.price()).isEqualTo(4500);
        assertThat(result.description()).isEqualTo("깊은 풍미");
        assertThat(result.imageUrl()).isEqualTo("http://img.jpg");
    }

    @Test
    @DisplayName("존재하지 않는 메뉴 조회 시 예외 발생")
    void getMenu_notFound_throwsException() {
        when(menuRepository.findByIdAndStoreId(999L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerMenuService.getMenu(1L, 999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("매장의 카테고리 목록을 조회한다")
    void getCategories_success() {
        Category cat1 = Category.builder().storeId(1L).name("커피").displayOrder(1).build();
        Category cat2 = Category.builder().storeId(1L).name("디저트").displayOrder(2).build();
        ReflectionTestUtils.setField(cat1, "id", 1L);
        ReflectionTestUtils.setField(cat2, "id", 2L);

        when(categoryRepository.findByStoreIdOrderByDisplayOrder(1L)).thenReturn(List.of(cat1, cat2));

        List<CategoryResponse> result = customerMenuService.getCategories(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("커피");
        assertThat(result.get(1).name()).isEqualTo("디저트");
    }
}
