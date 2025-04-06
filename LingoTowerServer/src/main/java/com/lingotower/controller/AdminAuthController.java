package com.lingotower.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lingotower.service.AdminAuthService;
import com.lingotower.data.AdminRepository;
import com.lingotower.model.Admin;
import com.lingotower.dto.LoginRequest;
import com.lingotower.dto.admin.AdminCreateDTO;
import com.lingotower.dto.admin.AdminRegisterRequest;

/**
 * Controller class for handling admin authentication operations such as registration and login.
 */
@RestController
@RequestMapping("/api/auth/admin")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;
    private final AdminRepository adminRepository;

    /**
     * Constructs an instance of {@code AdminAuthController} with necessary dependencies.
     *
     * @param adminAuthService  Service for handling admin authentication.
     * @param adminRepository   Repository for accessing admin data.
     */
    public AdminAuthController(AdminAuthService adminAuthService, AdminRepository adminRepository) {
        this.adminAuthService = adminAuthService;
        this.adminRepository = adminRepository;
    }

    /**
     * Registers a new admin and returns a JWT token.
     *
     * @param request The request containing details for the new admin registration.
     * @return A response with a JWT token and HTTP status 201 (Created).
     */
//    @PostMapping("/register")
//    public ResponseEntity<String> registerAdmin(@RequestBody AdminRegisterRequest request) {
//        // Retrieve the current admin who is attempting to perform the registration
//        Admin currentAdmin = adminRepository.findByEmail(request.getCurrentAdminEmail())
//                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));
//
//        // Call service to register the new admin and generate a JWT token
//        String token = adminAuthService.registerAdmin(request.getAdminCreateDTO(), currentAdmin);
//
//        // Return the JWT token in the response with HTTP status 201 (Created)
//        return ResponseEntity.status(HttpStatus.CREATED).body(token);
//    }

    /**
     * Authenticates an admin and generates a JWT token.
     *
     * @param request The login request containing the admin's identifier and password.
     * @return A response with the generated JWT token if authentication is successful.
     */
    @PostMapping("/login")
    public ResponseEntity<String> loginAdmin(@RequestBody LoginRequest request) {
        // Authenticate the admin and generate a JWT token
        String token = adminAuthService.loginAdmin(request.getIdentifier(), request.getPassword());

        // Return the JWT token in the response with HTTP status 200 (OK)
        return ResponseEntity.ok(token);
    }
    
}
