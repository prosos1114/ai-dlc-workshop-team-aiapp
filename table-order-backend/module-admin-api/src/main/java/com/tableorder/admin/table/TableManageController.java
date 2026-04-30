package com.tableorder.admin.table;

import com.tableorder.admin.table.dto.TableCreateRequest;
import com.tableorder.admin.table.dto.TableResponse;
import com.tableorder.admin.table.dto.TableUpdateRequest;
import com.tableorder.core.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stores/{storeId}/tables")
public class TableManageController {

    private final TableManageService tableManageService;

    public TableManageController(TableManageService tableManageService) {
        this.tableManageService = tableManageService;
    }

    @PostMapping
    public ApiResponse<TableResponse> createTable(@PathVariable Long storeId,
                                                   @Valid @RequestBody TableCreateRequest request) {
        TableResponse response = tableManageService.createTable(
                storeId, request.tableNumber(), request.password());
        return ApiResponse.ok(response, "테이블이 생성되었습니다");
    }

    @GetMapping
    public ApiResponse<List<TableResponse>> getTables(@PathVariable Long storeId) {
        List<TableResponse> response = tableManageService.getTablesByStore(storeId);
        return ApiResponse.ok(response);
    }

    @PutMapping("/{tableId}")
    public ApiResponse<TableResponse> updateTable(@PathVariable Long storeId,
                                                   @PathVariable Long tableId,
                                                   @Valid @RequestBody TableUpdateRequest request) {
        TableResponse response = tableManageService.updateTable(storeId, tableId, request.password());
        return ApiResponse.ok(response);
    }

    @PostMapping("/{tableId}/complete")
    public ApiResponse<Void> completeTable(@PathVariable Long storeId,
                                            @PathVariable Long tableId) {
        tableManageService.completeTable(storeId, tableId);
        return ApiResponse.ok();
    }
}
