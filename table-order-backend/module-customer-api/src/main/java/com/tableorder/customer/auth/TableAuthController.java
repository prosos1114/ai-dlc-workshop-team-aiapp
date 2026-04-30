package com.tableorder.customer.auth;

import com.tableorder.core.dto.ApiResponse;
import com.tableorder.customer.auth.dto.TableLoginRequest;
import com.tableorder.customer.auth.dto.TableLoginResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/table/auth")
public class TableAuthController {

    private final TableAuthService tableAuthService;

    public TableAuthController(TableAuthService tableAuthService) {
        this.tableAuthService = tableAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TableLoginResponse>> login(@Valid @RequestBody TableLoginRequest request) {
        TableLoginResponse response = tableAuthService.login(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
