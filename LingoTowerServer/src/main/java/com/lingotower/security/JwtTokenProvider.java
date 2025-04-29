package com.lingotower.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.lingotower.model.BaseUser;
import com.lingotower.model.User;

@Component
public class JwtTokenProvider {
	private static final String SECRET_KEY = "mySecretKeymySecretKeymySecretKey";

	/**
	 * Generates a JWT token for the given user. The token includes the username and
	 * role of the user as claims, and has an expiration time of 10 hours. It is
	 * signed using HMAC-SHA-256 with the SECRET_KEY.
	 * 
	 * @param user The BaseUser object for whom to generate the token.
	 * @return The generated JWT token as a String.
	 */
	public String generateToken(BaseUser user) {
		Instant now = Instant.now();
		return Jwts.builder().subject(user.getUsername()).claim("role", user.getRole()).issuedAt(Date.from(now))
				.expiration(Date.from(now.plus(10, ChronoUnit.HOURS)))
				.signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), Jwts.SIG.HS256).compact();
	}

	/**
	 * Extracts the username from the given JWT token. It parses the token, verifies
	 * the signature using the SECRET_KEY, and retrieves the subject claim, which
	 * contains the username.
	 * 
	 * @param token The JWT token from which to extract the username.
	 * @return The username extracted from the token.
	 */
	public String extractUsername(String token) {
		return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes())).build().parseSignedClaims(token)
				.getPayload().getSubject();
	}

	/**
	 * Retrieves all claims from the given JWT token's payload. It parses the token
	 * and verifies the signature using the SECRET_KEY to ensure the token's
	 * integrity.
	 * 
	 * @param token The JWT token from which to retrieve the claims.
	 * @return A Claims object containing all the claims in the token's payload.
	 */
	public Claims getClaims(String token) {
		return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes())).build().parseSignedClaims(token)
				.getPayload();
	}

	/**
	 * Extracts the role of the user from the given JWT token. It retrieves all
	 * claims using the getClaims method and then extracts the value of the "role"
	 * claim as a String.
	 * 
	 * @param token The JWT token from which to extract the role.
	 * @return The role of the user extracted from the token.
	 */
	public String extractRole(String token) {
		return getClaims(token).get("role", String.class);
	}
}