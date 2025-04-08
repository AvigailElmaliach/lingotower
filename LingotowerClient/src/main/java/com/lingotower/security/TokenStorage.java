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
		System.out.println("Token retrieved from storage: " + jwtToken);
		return jwtToken;
	}

	public static boolean hasToken() {
		return jwtToken != null && !jwtToken.isEmpty();
	}

	public static void clearToken() {
		jwtToken = null;
	}

	// Debug method to help troubleshoot
	public static void logTokenStatus(String location) {
		if (hasToken()) {
			System.out.println("[" + location + "] Token is present: "
					+ getToken().substring(0, Math.min(10, getToken().length())) + "...");
		} else {
			System.out.println("[" + location + "] No token stored!");
		}
	}

}