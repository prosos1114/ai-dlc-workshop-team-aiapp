package com.tableorder.admin.menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tableorder.admin.menu.dto.MenuCreateRequest;
import com.tableorder.admin.menu.dto.MenuResponse;
import com.tableorder.core.security.JwtAuthenticationFilter;
import com.tableorder.core.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = MenuManageController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class}
        )
)
class MenuManageControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private MenuManageService menuManageService;
    @MockBean private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/stores/{storeId}/menus - 메뉴 등록 성공")
    void createMenu_success() throws Exception {
        MenuCreateRequest request = new MenuCreateRequest(1L, "아메리카노", 4500, "설명");
        given(menuManageService.createMenu(1L, 1L, "아메리카노", 4500, "설명"))
                .willReturn(new MenuResponse(1L, 1L, "음료", "아메리카노", 4500, "설명", null, 0));

        mockMvc.perform(post("/api/stores/1/menus")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("아메리카노"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/stores/{storeId}/menus - 메뉴 목록 조회")
    void getMenus_success() throws Exception {
        given(menuManageService.getMenusByStore(1L, null))
                .willReturn(List.of(new MenuResponse(1L, 1L, "음료", "아메리카노", 4500, null, null, 0)));

        mockMvc.perform(get("/api/stores/1/menus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("아메리카노"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/stores/{storeId}/menus - 유효성 검증 실패 (가격 음수)")
    void createMenu_invalidPrice() throws Exception {
        MenuCreateRequest request = new MenuCreateRequest(1L, "메뉴", -100, null);

        mockMvc.perform(post("/api/stores/1/menus")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
