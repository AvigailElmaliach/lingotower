package com.lingotower.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.lingotower.dto.LoginRequest;
import com.lingotower.dto.RegisterRequest;
import com.lingotower.service.AuthService;
import com.lingotower.model.*;
import com.lingotower.service.CategoryService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        // בדיקה אם ה-DTO מתקבל ומעובד בצורה נכונה
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        // בדיקה אם ה-DTO מתקבל ומעובד בצורה נכונה
        return ResponseEntity.ok(authService.login(request));
    }


}
