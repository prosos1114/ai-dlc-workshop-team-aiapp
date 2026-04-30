package com.tableorder.admin.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tableorder.admin.store.dto.StoreCreateRequest;
import com.tableorder.admin.store.dto.StoreResponse;
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
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = StoreController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class}
        )
)
class StoreControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private StoreService storeService;
    @MockBean private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("POST /api/stores - 매장 등록 성공")
    void createStore_success() throws Exception {
        StoreCreateRequest request = new StoreCreateRequest("테스트매장", "test-store");
        given(storeService.createStore("테스트매장", "test-store"))
                .willReturn(new StoreResponse(1L, "테스트매장", "test-store", LocalDateTime.now()));

        mockMvc.perform(post("/api/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.code").value("test-store"));
    }

    @Test
    @DisplayName("GET /api/stores/{storeCode} - 매장 조회 성공")
    void getStore_success() throws Exception {
        given(storeService.getStoreByCode("test-store"))
                .willReturn(new StoreResponse(1L, "테스트매장", "test-store", LocalDateTime.now()));

        mockMvc.perform(get("/api/stores/test-store"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("테스트매장"));
    }

    @Test
    @DisplayName("POST /api/stores - 유효성 검증 실패 (매장 코드 형식)")
    void createStore_invalidCode() throws Exception {
        StoreCreateRequest request = new StoreCreateRequest("매장", "INVALID CODE!");

        mockMvc.perform(post("/api/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
