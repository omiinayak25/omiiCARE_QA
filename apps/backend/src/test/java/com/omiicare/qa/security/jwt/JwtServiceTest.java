package com.omiicare.qa.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Claims;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link JwtService} token issuance and parsing. */
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties();
        props.setSecret("unit-test-secret-key-that-is-at-least-32-bytes-long-0123456789");
        props.setAccessTokenMinutes(15);
        props.setRefreshTokenDays(7);
        jwtService = new JwtService(props);
    }

    @Test
    void accessTokenCarriesSubjectTenantAndAuthorities() {
        String token = jwtService.generateAccessToken("alice", 42L, List.of("patient:read"));
        Claims claims = jwtService.parse(token);

        assertThat(claims.getSubject()).isEqualTo("alice");
        assertThat(claims.get(JwtService.CLAIM_TYPE)).isEqualTo(JwtService.TYPE_ACCESS);
        assertThat(claims.get(JwtService.CLAIM_TENANT, Number.class).longValue()).isEqualTo(42L);
        @SuppressWarnings("unchecked")
        List<String> authorities = claims.get(JwtService.CLAIM_AUTHORITIES, List.class);
        assertThat(authorities).contains("patient:read");
    }

    @Test
    void refreshTokenIsTypedAsRefresh() {
        String token = jwtService.generateRefreshToken("bob", 7L);
        Claims claims = jwtService.parse(token);

        assertThat(claims.getSubject()).isEqualTo("bob");
        assertThat(claims.get(JwtService.CLAIM_TYPE)).isEqualTo(JwtService.TYPE_REFRESH);
    }
}
