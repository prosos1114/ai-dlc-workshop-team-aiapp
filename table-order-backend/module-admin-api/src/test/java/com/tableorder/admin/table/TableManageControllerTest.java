package com.tableorder.admin.table;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tableorder.admin.TestSecurityConfig;
import com.tableorder.admin.table.dto.TableCreateRequest;
import com.tableorder.admin.table.dto.TableResponse;
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

@WebMvcTest(TableManageController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
class TableManageControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private TableManageService tableManageService;
    @MockBean private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("POST /api/stores/{storeId}/tables - 테이블 생성 성공")
    void createTable_success() throws Exception {
        TableCreateRequest request = new TableCreateRequest(1, "1234");
        given(tableManageService.createTable(1L, 1, "1234"))
                .willReturn(new TableResponse(1L, 1L, 1, false, 0, 0));

        mockMvc.perform(post("/api/stores/1/tables")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tableNumber").value(1));
    }

    @Test
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
