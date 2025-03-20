package com.lingotower.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.lingotower.security.JwtTokenProvider;
import java.io.IOException;
import java.util.ArrayList;



public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // שלוף את הטוקן מהכותרת Authorization
    	
    	

        String token = request.getHeader("Authorization");
        System.out.println("Token received: " + token);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // חותך את "Bearer " ומקבל רק את הטוקן

            try {
                // שלוף את שם המשתמש מתוך הטוקן
                String username = jwtTokenProvider.extractUsername(token);
                System.out.println("Extracted username: " + username);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // אם לא קיים Authentication בקונטקסט, צור אחד חדש
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>()) // מבנה של הרשאות ריקות (לא נדרש כאן)
                    );
                }
            } catch (ExpiredJwtException e) {
                // טיפול במקרה של טוקן פג תוקף
                response.setStatus(401);
                response.getWriter().write("Token has expired");
                return;
            }
        }

        // המשך את הביצוע בשרשרת הפילטרים
        filterChain.doFilter(request, response);
    }
}
