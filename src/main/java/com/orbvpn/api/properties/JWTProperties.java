package com.orbvpn.api.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JWTProperties {
    private String secret;
    private String issuer;
    private Long expirationMillis;
    private Long refreshMillis;
    private String resetPasswordSecret;
    private Long resetPasswordExpirationMillis;
}