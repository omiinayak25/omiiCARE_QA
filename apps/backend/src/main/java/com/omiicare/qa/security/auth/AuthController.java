package com.omiicare.qa.security.auth;

import com.omiicare.qa.security.AppUserPrincipal;
import com.omiicare.qa.security.auth.AuthDtos.CurrentUser;
import com.omiicare.qa.security.auth.AuthDtos.LoginRequest;
import com.omiicare.qa.security.auth.AuthDtos.RefreshRequest;
import com.omiicare.qa.security.auth.AuthDtos.TokenResponse;
import com.omiicare.qa.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Authentication endpoints: password login, refresh-token rotation, and whoami. */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Login, token refresh, and current-user lookup")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate with username and password")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Exchange a refresh token for a new access/refresh pair")
    public ApiResponse<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ApiResponse.ok(authService.refresh(request.refreshToken()));
    }

    @GetMapping("/me")
    @Operation(summary = "Return the currently authenticated user")
    public ApiResponse<CurrentUser> me(@AuthenticationPrincipal AppUserPrincipal principal) {
        return ApiResponse.ok(authService.currentUser(principal));
    }
}
