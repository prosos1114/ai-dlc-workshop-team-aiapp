package com.tableorder.customer.menu;

import com.tableorder.core.dto.ApiResponse;
import com.tableorder.customer.menu.dto.CategoryResponse;
import com.tableorder.customer.menu.dto.MenuResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stores/{storeId}")
public class CustomerMenuController {

    private final CustomerMenuService customerMenuService;

    public CustomerMenuController(CustomerMenuService customerMenuService) {
        this.customerMenuService = customerMenuService;
    }

    @GetMapping("/menus")
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getMenus(
            @PathVariable Long storeId,
            @RequestParam(required = false) Long categoryId) {
        List<MenuResponse> menus = customerMenuService.getMenus(storeId, categoryId);
        return ResponseEntity.ok(ApiResponse.ok(menus));
    }

    @GetMapping("/menus/{menuId}")
    public ResponseEntity<ApiResponse<MenuResponse>> getMenu(
            @PathVariable Long storeId,
            @PathVariable Long menuId) {
        MenuResponse menu = customerMenuService.getMenu(storeId, menuId);
        return ResponseEntity.ok(ApiResponse.ok(menu));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories(
            @PathVariable Long storeId) {
        List<CategoryResponse> categories = customerMenuService.getCategories(storeId);
        return ResponseEntity.ok(ApiResponse.ok(categories));
    }
}
