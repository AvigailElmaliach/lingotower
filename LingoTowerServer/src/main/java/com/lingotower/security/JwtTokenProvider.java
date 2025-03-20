package com.lingotower.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.lingotower.model.User;

@Component
public class JwtTokenProvider {
    private static final String SECRET_KEY = "mySecretKeymySecretKeymySecretKey"; // חייב להיות לפחות 32 תווים

    public String generateToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getUsername()) // Use the username (or other string field) here
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(10, ChronoUnit.HOURS)))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), Jwts.SIG.HS256) // Signing the token
                .compact();
    }


    public String extractUsername(String token) {
        return Jwts.parser() // שינוי ל-parser() במקום parserBuilder()
                .verifyWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes())) // במקום setSigningKey
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
