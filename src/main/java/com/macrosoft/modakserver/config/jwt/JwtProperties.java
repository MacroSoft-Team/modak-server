package com.macrosoft.modakserver.config.jwt;

import io.jsonwebtoken.io.Decoders;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {
    private String secret;
    private Long access_token_expiration;
    private Long refresh_token_expiration;
}