package com.lingotower.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lingotower.data.UserRepository;
import com.lingotower.model.Role;
import com.lingotower.model.User;
import com.lingotower.dto.LoginRequest;
import com.lingotower.dto.RegisterRequest;
import com.lingotower.security.JwtTokenProvider;

/**
 * Service class responsible for user authentication and registration.
 * This class handles user sign-up, login, and token generation.
 */
@Service
public class UserAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Constructs an instance of {@code UserAuthService} with necessary dependencies.
     *
     * @param userRepository    Repository for accessing user data.
     * @param passwordEncoder   Encoder for securing passwords.
     * @param jwtTokenProvider  Provider for generating JWT authentication tokens.
     */
    @Autowired
    public UserAuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Registers a new user.
     *
     * @param request The registration request containing user details.
     * @return A JWT token for the newly registered user.
     * @throws IllegalArgumentException if the email or username is already taken.
     */
//    public String registerUser(RegisterRequest request) {
//        if (userRepository.existsByEmail(request.getEmail())) {
//            throw new IllegalArgumentException("Email already exists");
//        }
//
//        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
//            throw new IllegalArgumentException("Username is already taken");
//        }
//        String target=request.getSourceLanguage();
//        User newUser = new User(
//            request.getUsername(),
//            passwordEncoder.encode(request.getPassword()),
//            request.getEmail(),
//            target,
//            request.getTargetLanguage(target),//לשנות את זה בהמשך לא טוב
//            Role.USER  
//        );
//
//        userRepository.save(newUser);
//        return jwtTokenProvider.generateToken(newUser);
//    }
    public String registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username is already taken");
        }

        User newUser = new User(
            request.getUsername(),
            passwordEncoder.encode(request.getPassword()),
            request.getEmail(),
            request.getSourceLanguage(),
            request.getTargetLanguage(), 
            Role.USER  
        );

        userRepository.save(newUser);
        return jwtTokenProvider.generateToken(newUser);
    }


    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param request The login request containing username and password.
     * @return A JWT token if authentication is successful.
     * @throws IllegalArgumentException if the user is not found or credentials are invalid.
     */
    public String login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getIdentifier())
                .orElseGet(() -> userRepository.findByEmail(request.getIdentifier())
                .orElseThrow(() -> new IllegalArgumentException("User not found")));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return jwtTokenProvider.generateToken(user);
    }

//    public String login(LoginRequest request) {
//        User user = userRepository.findByUsername(request.getUsername())
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//            throw new IllegalArgumentException("Invalid credentials");
//        }
//
//        return jwtTokenProvider.generateToken(user);
//    }
}
