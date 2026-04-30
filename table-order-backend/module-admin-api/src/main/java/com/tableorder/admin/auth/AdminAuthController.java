package com.tableorder.admin.auth;

import com.tableorder.admin.auth.dto.AdminResponse;
import com.tableorder.admin.auth.dto.LoginRequest;
import com.tableorder.admin.auth.dto.RegisterRequest;
import com.tableorder.admin.auth.dto.TokenResponse;
import com.tableorder.core.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    public AdminAuthController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = adminAuthService.login(
                request.storeCode(), request.username(), request.password());
        return ApiResponse.ok(response);
    }

    @PostMapping("/register")
    public ApiResponse<AdminResponse> register(@Valid @RequestBody RegisterRequest request) {
        AdminResponse response = adminAuthService.register(
                request.storeCode(), request.username(), request.password());
        return ApiResponse.ok(response, "관리자 등록이 완료되었습니다");
    }
}
