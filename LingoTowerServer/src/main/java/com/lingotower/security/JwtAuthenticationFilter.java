package com.lingotower.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	/**
	 * This filter intercepts incoming HTTP requests to authenticate users based on
	 * JWT tokens. It extracts the token from the "Authorization" header, validates
	 * it, and if valid, sets the authentication context for the current request. If
	 * the token is expired or invalid, it sends an unauthorized (401) response.
	 * 
	 * @param request     The incoming HttpServletRequest.
	 * @param response    The outgoing HttpServletResponse.
	 * @param filterChain The FilterChain for passing the request to the next
	 *                    filter.
	 * @throws ServletException If a servlet-specific error occurs.
	 * @throws IOException      If an I/O error occurs during the processing of the
	 *                          filter.
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String token = request.getHeader("Authorization");
		System.out.println("Token received: " + token);

		if (token != null && token.startsWith("Bearer ")) {
			token = token.substring(7);

			try {
				String username = jwtTokenProvider.extractUsername(token);
				String role = jwtTokenProvider.extractRole(token);

				System.out.println("Token received: " + token);
				System.out.println("Extracted username: " + username);
				System.out.println("Extracted role: " + role);

				if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());
					SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
							username, null, Collections.singletonList(authority)));
				}
			} catch (ExpiredJwtException e) {
				response.setStatus(401);
				response.getWriter().write("Token has expired");
				return;
			} catch (Exception e) {
				response.setStatus(401);
				response.getWriter().write("Invalid token");
				return;
			}
		}

		filterChain.doFilter(request, response);
	}
}