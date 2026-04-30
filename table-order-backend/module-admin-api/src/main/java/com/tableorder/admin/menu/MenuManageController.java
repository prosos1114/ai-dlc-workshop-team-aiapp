package com.tableorder.admin.menu;

import com.tableorder.admin.menu.dto.CategoryCreateRequest;
import com.tableorder.admin.menu.dto.CategoryResponse;
import com.tableorder.admin.menu.dto.MenuCreateRequest;
import com.tableorder.admin.menu.dto.MenuOrderUpdateRequest;
import com.tableorder.admin.menu.dto.MenuResponse;
import com.tableorder.admin.menu.dto.MenuUpdateRequest;
import com.tableorder.core.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/stores/{storeId}")
public class MenuManageController {

    private final MenuManageService menuManageService;

    public MenuManageController(MenuManageService menuManageService) {
        this.menuManageService = menuManageService;
    }

    @GetMapping("/menus")
    public ApiResponse<List<MenuResponse>> getMenus(@PathVariable Long storeId,
                                                     @RequestParam(required = false) Long categoryId) {
        List<MenuResponse> response = menuManageService.getMenusByStore(storeId, categoryId);
        return ApiResponse.ok(response);
    }

    @PostMapping("/menus")
    public ApiResponse<MenuResponse> createMenu(@PathVariable Long storeId,
                                                 @Valid @RequestBody MenuCreateRequest request) {
        MenuResponse response = menuManageService.createMenu(
                storeId, request.categoryId(), request.name(),
                request.price(), request.description());
        return ApiResponse.ok(response, "메뉴가 등록되었습니다");
    }

    @PutMapping("/menus/{menuId}")
    public ApiResponse<MenuResponse> updateMenu(@PathVariable Long storeId,
                                                 @PathVariable Long menuId,
                                                 @Valid @RequestBody MenuUpdateRequest request) {
        MenuResponse response = menuManageService.updateMenu(
                storeId, menuId, request.categoryId(), request.name(),
                request.price(), request.description());
        return ApiResponse.ok(response);
    }

    @DeleteMapping("/menus/{menuId}")
    public ApiResponse<Void> deleteMenu(@PathVariable Long storeId,
                                         @PathVariable Long menuId) {
        menuManageService.deleteMenu(storeId, menuId);
        return ApiResponse.ok();
    }

    @PutMapping("/menus/order")
    public ApiResponse<Void> updateMenuOrder(@PathVariable Long storeId,
                                              @Valid @RequestBody MenuOrderUpdateRequest request) {
        menuManageService.updateMenuOrder(storeId, request.menuOrders());
        return ApiResponse.ok();
    }

    @PostMapping("/menus/{menuId}/image")
    public ApiResponse<String> uploadImage(@PathVariable Long storeId,
                                            @PathVariable Long menuId,
                                            @RequestParam("file") MultipartFile file) {
        String imageUrl = menuManageService.uploadImage(storeId, menuId, file);
        return ApiResponse.ok(imageUrl);
    }

    @GetMapping("/categories")
    public ApiResponse<List<CategoryResponse>> getCategories(@PathVariable Long storeId) {
        List<CategoryResponse> response = menuManageService.getCategories(storeId);
        return ApiResponse.ok(response);
    }

    @PostMapping("/categories")
    public ApiResponse<CategoryResponse> createCategory(@PathVariable Long storeId,
                                                         @Valid @RequestBody CategoryCreateRequest request) {
        CategoryResponse response = menuManageService.createCategory(storeId, request.name());
        return ApiResponse.ok(response, "카테고리가 생성되었습니다");
    }
}
