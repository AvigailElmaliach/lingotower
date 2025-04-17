package com.lingotower.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * מחלקה לשמירת הטוקן JWT בצד הלקוח
 */
public class TokenStorage {
	private static final Logger logger = LoggerFactory.getLogger(TokenStorage.class);
	private static String jwtToken;

	public static void setToken(String token) {
		jwtToken = token; // Store in the static variable, not a local variable
		logger.debug("JWT token stored, length: {}", token.length());

		// For extra security, only log a small portion of the token
		if (token != null && token.length() > 10) {
			logger.trace("Token starts with: {}...", token.substring(0, 8));
		}
	}

	public static String getToken() {
		logger.trace("JWT token retrieved from storage");
		return jwtToken;
	}

	public static boolean hasToken() {
		boolean hasValidToken = jwtToken != null && !jwtToken.isEmpty();
		logger.trace("Token validation check: {}", hasValidToken ? "token present" : "no token");
		return hasValidToken;
	}

	public static void clearToken() {
		logger.debug("JWT token cleared from storage");
		jwtToken = null;
	}

	// Debug method to help troubleshoot
	public static void logTokenStatus(String location) {
		if (hasToken()) {
			String tokenPreview = getToken().substring(0, Math.min(10, getToken().length())) + "...";
			logger.debug("[{}] Token is present: {}", location, tokenPreview);
		} else {
			logger.warn("[{}] No token stored!", location);
		}
	}
}