package com.omiicare.qa.security.auth;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/** Request/response payloads for the authentication endpoints. */
public final class AuthDtos {

    private AuthDtos() {}

    public record LoginRequest(
            @NotBlank(message = "username is required") String username,
            @NotBlank(message = "password is required") String password) {}

    public record RefreshRequest(@NotBlank(message = "refreshToken is required") String refreshToken) {}

    public record TokenResponse(
            String accessToken, String refreshToken, String tokenType, long expiresInSeconds) {}

    public record CurrentUser(
            String username, Long tenantId, List<String> authorities) {}
}
