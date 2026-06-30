package com.omiicare.qa.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

/**
 * Issues and validates signed JWTs. Two token kinds are produced: short-lived
 * {@code access} tokens (carrying tenant and authority claims) and longer-lived
 * {@code refresh} tokens used only to mint new access tokens (token rotation).
 */
@Service
public class JwtService {

    public static final String CLAIM_TYPE = "type";
    public static final String CLAIM_TENANT = "tenantId";
    public static final String CLAIM_AUTHORITIES = "authorities";
    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";

    private final SecretKey key;
    private final JwtProperties properties;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(
            String username, Long tenantId, List<String> authorities) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(properties.getIssuer())
                .subject(username)
                .claim(CLAIM_TYPE, TYPE_ACCESS)
                .claim(CLAIM_TENANT, tenantId)
                .claim(CLAIM_AUTHORITIES, authorities)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(properties.getAccessTokenMinutes(), ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(String username, Long tenantId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(properties.getIssuer())
                .subject(username)
                .claim(CLAIM_TYPE, TYPE_REFRESH)
                .claim(CLAIM_TENANT, tenantId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(properties.getRefreshTokenDays(), ChronoUnit.DAYS)))
                .signWith(key)
                .compact();
    }

    /** Parses and verifies the token signature/expiry, returning its claims. */
    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public long accessTokenSeconds() {
        return properties.getAccessTokenMinutes() * 60;
    }
}
