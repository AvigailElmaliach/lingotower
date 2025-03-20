package com.lingotower.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.lingotower.dto.LoginRequest;
import com.lingotower.dto.RegisterRequest;
import com.lingotower.model.User;
import com.lingotower.data.UserRepository;
import com.lingotower.security.JwtTokenProvider;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // פונקציה לרישום משתמש חדש
    public String register(RegisterRequest request) {
        // בדוק אם שם המשתמש כבר קיים
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username is already taken");
        }

        // הצפנת סיסמה
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // יצירת משתמש חדש
        User user = new User(request.getUsername(), encodedPassword, request.getEmail(), request.getLanguage());  // הוספת email ו language
        userRepository.save(user);

        // יצירת טוקן JWT למשתמש
        return jwtTokenProvider.generateToken(user);
    }

    // פונקציה להתחברות
    public String login(LoginRequest request) {
        // חיפוש המשתמש לפי שם המשתמש
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // בדיקת סיסמה
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // יצירת טוקן JWT למשתמש
        return jwtTokenProvider.generateToken(user);
    }
}
