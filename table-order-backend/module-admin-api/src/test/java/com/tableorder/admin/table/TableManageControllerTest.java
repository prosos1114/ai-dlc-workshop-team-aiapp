package com.tableorder.admin.table;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tableorder.admin.table.dto.TableCreateRequest;
import com.tableorder.admin.table.dto.TableResponse;
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
        controllers = TableManageController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class}
        )
)
class TableManageControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private TableManageService tableManageService;
    @MockBean private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/stores/{storeId}/tables - 테이블 생성 성공")
    void createTable_success() throws Exception {
        TableCreateRequest request = new TableCreateRequest(1, "1234");
        given(tableManageService.createTable(1L, 1, "1234"))
                .willReturn(new TableResponse(1L, 1L, 1, false, 0, 0));

        mockMvc.perform(post("/api/stores/1/tables")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tableNumber").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/stores/{storeId}/tables - 테이블 목록 조회")
    void getTables_success() throws Exception {
        given(tableManageService.getTablesByStore(1L))
                .willReturn(List.of(new TableResponse(1L, 1L, 1, true, 3, 25000)));

        mockMvc.perform(get("/api/stores/1/tables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].tableNumber").value(1))
                .andExpect(jsonPath("$.data[0].hasActiveSession").value(true));
    }
}
