package com.tableorder.admin.menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tableorder.admin.TestSecurityConfig;
import com.tableorder.admin.menu.dto.MenuCreateRequest;
import com.tableorder.admin.menu.dto.MenuResponse;
import com.tableorder.core.exception.GlobalExceptionHandler;
import com.tableorder.core.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuManageController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
class MenuManageControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private MenuManageService menuManageService;
    @MockBean private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("POST /api/stores/{storeId}/menus - 메뉴 등록 성공")
    void createMenu_success() throws Exception {
        MenuCreateRequest request = new MenuCreateRequest(1L, "아메리카노", 4500, "설명");
        given(menuManageService.createMenu(1L, 1L, "아메리카노", 4500, "설명"))
                .willReturn(new MenuResponse(1L, 1L, "음료", "아메리카노", 4500, "설명", null, 0));

        mockMvc.perform(post("/api/stores/1/menus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("아메리카노"));
    }

    @Test
    @DisplayName("GET /api/stores/{storeId}/menus - 메뉴 목록 조회")
    void getMenus_success() throws Exception {
        given(menuManageService.getMenusByStore(1L, null))
                .willReturn(List.of(new MenuResponse(1L, 1L, "음료", "아메리카노", 4500, null, null, 0)));

        mockMvc.perform(get("/api/stores/1/menus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("아메리카노"));
    }
}
