package com.tableorder.admin.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tableorder.admin.order.dto.OrderResponse;
import com.tableorder.admin.order.dto.OrderStatusUpdateRequest;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = OrderManageController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class}
        )
)
class OrderManageControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private OrderManageService orderManageService;
    @MockBean private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/stores/{storeId}/orders - 주문 목록 조회")
    void getAllOrders_success() throws Exception {
        given(orderManageService.getAllOrders(1L, null))
                .willReturn(List.of(new OrderResponse(1L, "test-001", 1L, 1,
                        "PENDING", 10000, List.of(), LocalDateTime.now())));

        mockMvc.perform(get("/api/stores/1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].orderNumber").value("test-001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PATCH /api/stores/{storeId}/orders/{orderId}/status - 상태 변경 성공")
    void updateOrderStatus_success() throws Exception {
        OrderStatusUpdateRequest request = new OrderStatusUpdateRequest("PREPARING");
        given(orderManageService.updateOrderStatus(1L, 1L, "PREPARING"))
                .willReturn(new OrderResponse(1L, "test-001", 1L, 1,
                        "PREPARING", 10000, List.of(), LocalDateTime.now()));

        mockMvc.perform(patch("/api/stores/1/orders/1/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PREPARING"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PATCH - 유효성 검증 실패 (잘못된 상태값)")
    void updateOrderStatus_invalidStatus() throws Exception {
        OrderStatusUpdateRequest request = new OrderStatusUpdateRequest("INVALID");

        mockMvc.perform(patch("/api/stores/1/orders/1/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
