package com.lingotower.security;

/**
 * מחלקה לשמירת הטוקן JWT בצד הלקוח
 */
public class TokenStorage {
	private static String jwtToken;

	public static void setToken(String token) {
		jwtToken = token; // Store in the static variable, not a local variable
		System.out.println("Token stored: " + jwtToken);
	}

	public static String getToken() {
		System.out.println("Token retrieved: " + jwtToken);
		return jwtToken; // Return the stored token, not null
	}

	public static boolean hasToken() {
		return jwtToken != null && !jwtToken.isEmpty();
	}

	public static void clearToken() {
		jwtToken = null;
	}
}