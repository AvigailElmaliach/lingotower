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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = request.getHeader("Authorization");
        System.out.println("Token received: " + token);

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // חותך את המילה "Bearer "

            try {
                String username = jwtTokenProvider.extractUsername(token);
                String role = jwtTokenProvider.extractRole(token); // הוספת שליפת תפקיד

                // >>> פה תכניסי את ההדפסות:
                System.out.println("Token received: " + token);
                System.out.println("Extracted username: " + username);
                System.out.println("Extracted role: " + role);

                
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // יצירת GrantedAuthority על בסיס ה-role מהטוקן
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(authority))
                    );
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