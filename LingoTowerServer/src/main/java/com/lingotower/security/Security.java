//package com.lingotower.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class Security {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/", "/home").permitAll() // אפשר גישה ללא צורך בהתחברות
//                .anyRequest().authenticated()  // כל יתר הבקשות דורשות התחברות
//            )
//            .formLogin(form -> form
//                .loginPage("/login")
//                .permitAll()  // גישה לדף התחברות
//            )
//            .logout(logout -> logout.permitAll());  // גישה לפונקציות יציאה
//
//        return http.build();
//    }
//
//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user = User.withUsername("lingo")
//                .password(passwordEncoder().encode("Avigail&&123456"))
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(user);  // ניהול משתמשים בזיכרון
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();  // מנגנון הצפנה לחסינות סיסמאות
//    }
//}
