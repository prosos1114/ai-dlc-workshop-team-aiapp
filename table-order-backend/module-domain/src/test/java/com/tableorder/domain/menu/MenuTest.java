package com.tableorder.domain.menu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MenuTest {

    @Test
    @DisplayName("Menu 생성 시 필드가 올바르게 설정된다")
    void create_fieldsSetCorrectly() {
        Menu menu = Menu.builder()
                .storeId(1L)
                .categoryId(2L)
                .name("아메리카노")
                .price(4500)
                .description("깊은 풍미의 에스프레소")
                .imageUrl("https://s3.amazonaws.com/images/americano.jpg")
                .displayOrder(1)
                .build();

        assertThat(menu.getStoreId()).isEqualTo(1L);
        assertThat(menu.getCategoryId()).isEqualTo(2L);
        assertThat(menu.getName()).isEqualTo("아메리카노");
        assertThat(menu.getPrice()).isEqualTo(4500);
        assertThat(menu.getDescription()).isEqualTo("깊은 풍미의 에스프레소");
        assertThat(menu.getImageUrl()).isEqualTo("https://s3.amazonaws.com/images/americano.jpg");
        assertThat(menu.getDisplayOrder()).isEqualTo(1);
    }

    @Test
    @DisplayName("메뉴 정보를 수정할 수 있다")
    void update_changesFields() {
        Menu menu = Menu.builder()
                .storeId(1L)
                .categoryId(2L)
                .name("아메리카노")
                .price(4500)
                .description("원래 설명")
                .displayOrder(1)
                .build();

        menu.update("카페라떼", 5500, "우유가 들어간 커피", 3L);

        assertThat(menu.getName()).isEqualTo("카페라떼");
        assertThat(menu.getPrice()).isEqualTo(5500);
        assertThat(menu.getDescription()).isEqualTo("우유가 들어간 커피");
        assertThat(menu.getCategoryId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("이미지 URL을 변경할 수 있다")
    void updateImageUrl_changesUrl() {
        Menu menu = Menu.builder()
                .storeId(1L)
                .categoryId(2L)
                .name("아메리카노")
                .price(4500)
                .displayOrder(1)
                .build();

        menu.updateImageUrl("https://s3.amazonaws.com/images/new-image.jpg");

        assertThat(menu.getImageUrl()).isEqualTo("https://s3.amazonaws.com/images/new-image.jpg");
    }

    @Test
    @DisplayName("표시 순서를 변경할 수 있다")
    void updateDisplayOrder_changesOrder() {
        Menu menu = Menu.builder()
                .storeId(1L)
                .categoryId(2L)
                .name("아메리카노")
                .price(4500)
                .displayOrder(1)
                .build();

        menu.updateDisplayOrder(5);

        assertThat(menu.getDisplayOrder()).isEqualTo(5);
    }

    @Test
    @DisplayName("description과 imageUrl은 null일 수 있다")
    void create_nullableFields() {
        Menu menu = Menu.builder()
                .storeId(1L)
                .categoryId(2L)
                .name("물")
                .price(0)
                .displayOrder(99)
                .build();

        assertThat(menu.getDescription()).isNull();
        assertThat(menu.getImageUrl()).isNull();
    }
}
