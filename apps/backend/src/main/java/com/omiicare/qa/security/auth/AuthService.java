package com.omiicare.qa.security.auth;

import com.omiicare.qa.audit.AuditService;
import com.omiicare.qa.security.AppUserPrincipal;
import com.omiicare.qa.security.auth.AuthDtos.CurrentUser;
import com.omiicare.qa.security.auth.AuthDtos.LoginRequest;
import com.omiicare.qa.security.auth.AuthDtos.TokenResponse;
import com.omiicare.qa.security.jwt.JwtService;
import com.omiicare.qa.shared.error.ApiException;
import com.omiicare.qa.shared.error.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import java.util.List;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

/**
 * Authentication use cases: password login (issuing an access + refresh token pair) and
 * refresh-token rotation. Each significant outcome is audited.
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuditService auditService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            AuditService auditService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.auditService = auditService;
    }

    public TokenResponse login(LoginRequest request) {
        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.username(), request.password()));
            AppUserPrincipal principal = (AppUserPrincipal) authentication.getPrincipal();
            List<String> authorities = authorityNames(principal);
            auditService.record(
                    "LOGIN_SUCCESS", "User", principal.getUsername(), principal.getTenantId());
            return new TokenResponse(
                    jwtService.generateAccessToken(
                            principal.getUsername(), principal.getTenantId(), authorities),
                    jwtService.generateRefreshToken(
                            principal.getUsername(), principal.getTenantId()),
                    "Bearer",
                    jwtService.accessTokenSeconds());
        } catch (BadCredentialsException ex) {
            auditService.record("LOGIN_FAILURE", "User", request.username(), null);
            throw new ApiExceptionAdapter(
                    ErrorCode.INVALID_CREDENTIALS, "Invalid username or password");
        }
    }

    public TokenResponse refresh(String refreshToken) {
        final Claims claims;
        try {
            claims = jwtService.parse(refreshToken);
        } catch (JwtException ex) {
            throw new ApiExceptionAdapter(ErrorCode.UNAUTHENTICATED, "Invalid refresh token");
        }
        if (!JwtService.TYPE_REFRESH.equals(claims.get(JwtService.CLAIM_TYPE))) {
            throw new ApiExceptionAdapter(ErrorCode.UNAUTHENTICATED, "Not a refresh token");
        }
        String username = claims.getSubject();
        Long tenantId = claims.get(JwtService.CLAIM_TENANT, Number.class).longValue();
        // Token rotation: issue a fresh access + refresh pair.
        auditService.record("TOKEN_REFRESH", "User", username, tenantId);
        return new TokenResponse(
                jwtService.generateAccessToken(username, tenantId, List.of()),
                jwtService.generateRefreshToken(username, tenantId),
                "Bearer",
                jwtService.accessTokenSeconds());
    }

    public CurrentUser currentUser(AppUserPrincipal principal) {
        return new CurrentUser(
                principal.getUsername(), principal.getTenantId(), authorityNames(principal));
    }

    private List<String> authorityNames(AppUserPrincipal principal) {
        return principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    }

    /** Small concrete {@link ApiException} so auth failures flow through the global handler. */
    private static final class ApiExceptionAdapter extends ApiException {
        ApiExceptionAdapter(ErrorCode code, String message) {
            super(code, message);
        }
    }
}
