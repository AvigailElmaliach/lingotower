package com.lingotower.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lingotower.data.AdminRepository;
import com.lingotower.model.Role;
import com.lingotower.model.Admin;
import com.lingotower.dto.admin.AdminCreateDTO;
import com.lingotower.security.JwtTokenProvider;


@Service
public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    
    @Autowired
    public AdminAuthService(AdminRepository adminRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    
    public String loginAdmin(String identifier, String password) {
        Admin admin;

        // Login by email or username
        if (identifier.contains("@")) {
            // Login using email
            admin = adminRepository.findByEmail(identifier)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        } else {
            // Login using username
            admin = adminRepository.findByUsername(identifier)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        }

        // Validate the password
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Generate and return a JWT token for the admin
        return jwtTokenProvider.generateToken(admin);
    }
}
