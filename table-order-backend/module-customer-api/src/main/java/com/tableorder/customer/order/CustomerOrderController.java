package com.tableorder.customer.order;

import com.tableorder.core.dto.ApiResponse;
import com.tableorder.customer.order.dto.OrderCreateRequest;
import com.tableorder.customer.order.dto.OrderResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stores/{storeId}/tables/{tableId}")
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;

    public CustomerOrderController(CustomerOrderService customerOrderService) {
        this.customerOrderService = customerOrderService;
    }

    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @PathVariable Long storeId,
            @PathVariable Long tableId,
            @Valid @RequestBody OrderCreateRequest request) {
        OrderResponse response = customerOrderService.createOrder(storeId, tableId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response, "주문이 접수되었습니다"));
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getSessionOrders(
            @PathVariable Long storeId,
            @PathVariable Long tableId) {
        List<OrderResponse> orders = customerOrderService.getSessionOrders(storeId, tableId);
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }
}
