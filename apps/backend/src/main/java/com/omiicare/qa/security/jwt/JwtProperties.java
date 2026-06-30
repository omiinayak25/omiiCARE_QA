package com.omiicare.qa.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Typed binding for {@code omiicare.security.jwt.*}. Externalizes the signing
 * secret and token lifetimes so nothing is hardcoded and secrets stay in the
 * environment.
 */
@ConfigurationProperties(prefix = "omiicare.security.jwt")
public class JwtProperties {

    private String secret;
    private long accessTokenMinutes = 15;
    private long refreshTokenDays = 7;
    private String issuer = "omiicare-qa";

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getAccessTokenMinutes() {
        return accessTokenMinutes;
    }

    public void setAccessTokenMinutes(long accessTokenMinutes) {
        this.accessTokenMinutes = accessTokenMinutes;
    }

    public long getRefreshTokenDays() {
        return refreshTokenDays;
    }

    public void setRefreshTokenDays(long refreshTokenDays) {
        this.refreshTokenDays = refreshTokenDays;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
}
