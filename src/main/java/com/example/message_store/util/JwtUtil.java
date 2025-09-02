package com.example.message_store.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Log4j2
@Component
public class JwtUtil {

    @Value("${jwt.secret:secret-signature-verification-key}")
    private String secret;

    private SecretKey getSecretKey() {
        log.error("secret: ".concat(secret));
        System.out.println("secret: ".concat(secret));

        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String extractUsername(String token) {
        Claims userClaims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return userClaims.getSubject();
    }

}
