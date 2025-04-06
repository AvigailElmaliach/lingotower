package com.lingotower.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lingotower.data.AdminRepository;
import com.lingotower.model.Role;
import com.lingotower.model.Admin;
import com.lingotower.dto.admin.AdminCreateDTO;
import com.lingotower.security.JwtTokenProvider;

/**
 * Service class responsible for admin authentication and registration.
 * This class handles admin sign-up, login, and token generation.
 */
@Service
public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Constructs an instance of {@code AdminAuthService} with necessary dependencies.
     *
     * @param adminRepository    Repository for accessing admin data.
     * @param passwordEncoder   Encoder for securing passwords.
     * @param jwtTokenProvider  Provider for generating JWT authentication tokens.
     */
    @Autowired
    public AdminAuthService(AdminRepository adminRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Registers a new admin.
     *
     * @param adminCreateDTO The registration request containing admin details.
     * @param currentAdmin   The currently authenticated admin (used for validation).
     * @return A JWT token for the newly registered admin.
     * @throws IllegalArgumentException if the email or username is already taken.
     * @throws SecurityException if the current user is not an admin.
     */
//    public String registerAdmin(AdminCreateDTO adminCreateDTO, Admin currentAdmin) {
//        // Check if the current user is an admin or superadmin
//        if (currentAdmin.getRole() != Role.ADMIN ) {
//            throw new SecurityException("Only an admin can register another admin!");
//        }
//
//        // Check if the email already exists
//        if (adminRepository.existsByEmail(adminCreateDTO.getEmail())) {
//            throw new IllegalArgumentException("Email already exists");
//        }
//
//        // Create a new admin object
//        Admin newAdmin = new Admin(
//            adminCreateDTO.getUsername(),
//            passwordEncoder.encode(adminCreateDTO.getPassword()),
//            adminCreateDTO.getEmail(),
//            adminCreateDTO.getSourceLanguage(),
//            adminCreateDTO.getTargetLanguage(),
//            Role.ADMIN
//        );
//
//        // Save the new admin to the database
//        Admin savedAdmin = adminRepository.save(newAdmin);
//
//        // Generate and return a JWT token for the new admin
//        return jwtTokenProvider.generateToken(savedAdmin);
//    }

    /**
     * Authenticates an admin and generates a JWT token.
     *
     * @param identifier The login identifier (username or email).
     * @param password   The password for authentication.
     * @return A JWT token if authentication is successful.
     * @throws IllegalArgumentException if the credentials are invalid.
     */
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
