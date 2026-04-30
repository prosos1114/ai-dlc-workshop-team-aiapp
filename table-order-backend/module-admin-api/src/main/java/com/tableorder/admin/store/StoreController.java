package com.tableorder.admin.store;

import com.tableorder.admin.store.dto.StoreCreateRequest;
import com.tableorder.admin.store.dto.StoreResponse;
import com.tableorder.core.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping
    public ApiResponse<StoreResponse> createStore(@Valid @RequestBody StoreCreateRequest request) {
        StoreResponse response = storeService.createStore(request.name(), request.code());
        return ApiResponse.ok(response, "매장이 등록되었습니다");
    }

    @GetMapping("/{storeCode}")
    public ApiResponse<StoreResponse> getStore(@PathVariable String storeCode) {
        StoreResponse response = storeService.getStoreByCode(storeCode);
        return ApiResponse.ok(response);
    }
}
