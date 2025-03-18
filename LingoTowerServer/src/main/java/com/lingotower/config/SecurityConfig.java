package com.lingotower.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // מבטלים CSRF כדי לאפשר POST/PUT
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // כל הבקשות פתוחות ללא צורך באימות
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // לא מנהלים סשנים
            .httpBasic(AbstractHttpConfigurer::disable) // מבטלים HTTP Basic Authentication
            .formLogin(AbstractHttpConfigurer::disable); // מבטלים את טופס ההתחברות

        return http.build();
    }
}




















