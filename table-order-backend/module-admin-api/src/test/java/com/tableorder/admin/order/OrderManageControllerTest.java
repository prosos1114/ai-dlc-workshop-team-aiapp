package com.tableorder.admin.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tableorder.admin.TestSecurityConfig;
import com.tableorder.admin.order.dto.OrderResponse;
import com.tableorder.admin.order.dto.OrderStatusUpdateRequest;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderManageController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
class OrderManageControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private OrderManageService orderManageService;
    @MockBean private JwtTokenProvider jwtTokenProvider;

    @Test
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
    @DisplayName("PATCH /api/stores/{storeId}/orders/{orderId}/status - 상태 변경 성공")
    void updateOrderStatus_success() throws Exception {
        OrderStatusUpdateRequest request = new OrderStatusUpdateRequest("PREPARING");
        given(orderManageService.updateOrderStatus(1L, 1L, "PREPARING"))
                .willReturn(new OrderResponse(1L, "test-001", 1L, 1,
                        "PREPARING", 10000, List.of(), LocalDateTime.now()));

        mockMvc.perform(patch("/api/stores/1/orders/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PREPARING"));
    }
}
