package com.tableorder.admin.order;

import com.tableorder.admin.order.dto.OrderHistoryResponse;
import com.tableorder.admin.order.dto.OrderResponse;
import com.tableorder.admin.order.dto.OrderStatusUpdateRequest;
import com.tableorder.core.dto.ApiResponse;
import com.tableorder.core.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/stores/{storeId}")
public class OrderManageController {

    private final OrderManageService orderManageService;

    public OrderManageController(OrderManageService orderManageService) {
        this.orderManageService = orderManageService;
    }

    @GetMapping("/orders")
    public ApiResponse<List<OrderResponse>> getAllOrders(@PathVariable Long storeId,
                                                         @RequestParam(required = false) String status) {
        List<OrderResponse> response = orderManageService.getAllOrders(storeId, status);
        return ApiResponse.ok(response);
    }

    @PatchMapping("/orders/{orderId}/status")
    public ApiResponse<OrderResponse> updateOrderStatus(@PathVariable Long storeId,
                                                         @PathVariable Long orderId,
                                                         @Valid @RequestBody OrderStatusUpdateRequest request) {
        OrderResponse response = orderManageService.updateOrderStatus(
                storeId, orderId, request.status());
        return ApiResponse.ok(response);
    }

    @DeleteMapping("/orders/{orderId}")
    public ApiResponse<Void> deleteOrder(@PathVariable Long storeId,
                                          @PathVariable Long orderId) {
        orderManageService.deleteOrder(storeId, orderId);
        return ApiResponse.ok();
    }

    @GetMapping("/tables/{tableId}/history")
    public ApiResponse<PageResponse<OrderHistoryResponse>> getOrderHistory(
            @PathVariable Long storeId,
            @PathVariable Long tableId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<OrderHistoryResponse> response = orderManageService.getOrderHistory(
                storeId, tableId, startDate, endDate, page, size);
        return ApiResponse.ok(response);
    }
}
