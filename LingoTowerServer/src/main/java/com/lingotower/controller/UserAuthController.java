package com.lingotower.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.lingotower.dto.LoginRequest;
import com.lingotower.dto.RegisterRequest;
import com.lingotower.service.UserAuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth/user")
@Validated
public class UserAuthController {
	private final UserAuthService userAuthService;

	/**
	 * Constructor for the UserAuthController, injecting the required authentication
	 * service.
	 * 
	 * @param userAuthService The service layer for user authentication and
	 *                        registration.
	 */
	public UserAuthController(UserAuthService userAuthService) {
		this.userAuthService = userAuthService;
	}

	/**
	 * Registers a new user. Accepts a RegisterRequest object in the request body.
	 * 
	 * @param request The RegisterRequest DTO containing the user's registration
	 *                details.
	 * @return ResponseEntity containing a success message (the generated JWT token)
	 *         and HTTP status OK.
	 */
	@PostMapping("/register")
	public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
		return ResponseEntity.ok(userAuthService.registerUser(request));
	}

	/**
	 * Logs in an existing user. Accepts a LoginRequest object in the request body.
	 * 
	 * @param request The LoginRequest DTO containing the user's login credentials
	 *                (username and password).
	 * @return ResponseEntity containing the JWT token upon successful login and
	 *         HTTP status OK.
	 * @throws IllegalArgumentException If the login fails due to invalid
	 *                                  credentials. The exception message will
	 *                                  provide details about the failure.
	 */
	@PostMapping("/login")
	public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
		try {
			return ResponseEntity.ok(userAuthService.login(request));
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
}