package com.example.message_store.util;

import io.jsonwebtoken.Jwts;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

public class JwtGenerator {
    private static final String SECRET = "secret-signature-verification-key";

    public static String generateToken(String subject, String role, String issuer, long expirationMillis) {
        Key key = new SecretKeySpec(SECRET.getBytes(), "HmacSHA256");
        long nowMillis = System.currentTimeMillis();
        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(subject)
                .claim("role", role)
                .setIssuedAt(new Date(nowMillis))
                .setExpiration(new Date(nowMillis + expirationMillis))
                .signWith(key)
                .compact();
    }


    public static void main(String[] args) {
        long oneYearMillis = 365L * 24 * 60 * 60 * 1000;
        System.out.println("client1: \n");
        String jwt = generateToken("client1", "admin", "example.com", oneYearMillis);
        System.out.println("Generated JWT Token:");
        System.out.println(jwt);

        System.out.println("client2: \n");
        jwt = generateToken("client2", "admin", "example.com", oneYearMillis);
        System.out.println("Generated JWT Token:");

        System.out.println(jwt);
    }
}