package com.lingotower.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.lingotower.security.JwtTokenProvider;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // נסה לבטל CSRF אם אתה משתמש ב-Postman
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**").permitAll() // פתוח לכולם
                .requestMatchers("/api/admin/**").hasRole("ADMIN") // רק למנהלים
                .requestMatchers("/api/user/**").hasRole("USER") // רק למשתמשים רגילים
                .anyRequest().authenticated() // כל בקשה אחרת חייבת להיות מאומתת
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); 
    }
}


//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//	private final JwtTokenProvider jwtTokenProvider;
//
//    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
//        this.jwtTokenProvider = jwtTokenProvider;
//    }
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(csrf -> csrf.disable()) // נסה לבטל CSRF אם אתה משתמש ב-Postman
//            .authorizeHttpRequests(authz -> authz
//            		//  .requestMatchers("/api/categories/**").hasRole("USER") // אם למשל רק למשתמשים עם תפקיד USER
//            	.requestMatchers("/api/auth/**").permitAll() // פתוח לכולם
//            		    .requestMatchers("/", "/about", "/contact", "/api/auth/register","/api/auth/login").permitAll()
//
//            	.anyRequest().authenticated()
//            )
//            .sessionManagement(session -> session
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//            )
//            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .authorizeHttpRequests(authz -> authz
//                .requestMatchers("/api/auth/**").permitAll() // מאפשר גישה חופשית לדפים של auth
//                //.requestMatchers("/categories/**").permitAll()//בדיקה אפשר למחוק
//                .anyRequest().authenticated() // כל בקשה אחרת חייבת להיות מאומתת
//            )
//            .sessionManagement(session -> session
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // אין סשנים - שימוש בטוקנים בלבד
//            )
//            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class); // מוסיף את המסנן של ה-JWT
//
//        return http.build(); 
//    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder(); 
//    }
//}