package com.lingotower.controller;

import com.lingotower.dto.admin.AdminCreateDTO;
import com.lingotower.dto.admin.AdminUpdateDTO;
import com.lingotower.dto.user.UserDTO;
import com.lingotower.exception.AdminNotFoundException;
import com.lingotower.dto.admin.AdminResponseDTO;
import com.lingotower.model.Admin;
import com.lingotower.service.AdminService;
import com.lingotower.service.UserService;
import com.lingotower.service.WordService;

import jakarta.validation.Valid;

import com.lingotower.dto.baseUser.PasswordUpdateRequestDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

//import com.lingotower.model.Role;
import com.lingotower.model.User;
import com.lingotower.security.JwtTokenProvider;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/admins")
public class AdminController {

	private final AdminService adminService;
	private final UserService userService;

	private final JwtTokenProvider jwtTokenProvider;
	private final WordService wordService;

	/**
	 * Constructor for the AdminController, injecting required services and
	 * utilities.
	 * 
	 * @param adminService     The service layer for admin-related operations.
	 * @param jwtTokenProvider The utility for handling JWT tokens.
	 * @param userService      The service layer for user-related operations.
	 * @param wordService      The service layer for word-related operations.
	 */
	public AdminController(AdminService adminService, JwtTokenProvider jwtTokenProvider, UserService userService,
			WordService wordService) {
		this.adminService = adminService;
		this.jwtTokenProvider = jwtTokenProvider;
		this.userService = userService;
		this.wordService = wordService;
	}

	/**
	 * Retrieves a list of all admins.
	 * 
	 * @return ResponseEntity containing a list of AdminResponseDTO and HTTP status
	 *         OK.
	 */
	@GetMapping
	public List<AdminResponseDTO> getAllAdmins() {
		List<Admin> admins = adminService.getAllAdmins();
		return admins.stream().map(
				admin -> new AdminResponseDTO(admin.getId(), admin.getUsername(), admin.getEmail(), admin.getRole()))
				.collect(Collectors.toList());
	}

	/**
	 * Retrieves an admin by their ID.
	 * 
	 * @param id The ID of the admin to retrieve.
	 * @return ResponseEntity containing the AdminResponseDTO and HTTP status OK if
	 *         found, or HTTP status NOT FOUND if the admin does not exist.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<AdminResponseDTO> getAdminById(@PathVariable Long id) {
		try {
			Admin admin = adminService.getAdminById(id);
			AdminResponseDTO responseDTO = new AdminResponseDTO(admin.getId(), admin.getUsername(), admin.getEmail(),
					admin.getRole());
			return ResponseEntity.ok(responseDTO);
		} catch (AdminNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	/**
	 * Registers a new admin. Requires a valid JWT token in the Authorization
	 * header.
	 * 
	 * @param adminCreateDTO The DTO containing the information for the new admin.
	 * @param token          The JWT token from the Authorization header.
	 * @return ResponseEntity with a success message and HTTP status CREATED, or an
	 *         error message and appropriate HTTP status (FORBIDDEN, BAD_REQUEST).
	 */
	@PostMapping("/register")
	public ResponseEntity<String> createAdmin(@Valid @RequestBody AdminCreateDTO adminCreateDTO,
			@RequestHeader("Authorization") String token) {
		try {
			adminService.registerAdmin(adminCreateDTO, token);
			return ResponseEntity.status(HttpStatus.CREATED).body("Admin registered successfully");
		} catch (SecurityException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	/**
	 * Deletes a word by its ID. Accessible to admins.
	 * 
	 * @param wordId    The ID of the word to delete.
	 * @param principal The Principal object representing the currently logged-in
	 *                  user (admin).
	 * @return ResponseEntity with HTTP status OK if the word was deleted, or an
	 *         appropriate HTTP status based on the ResponseStatusException.
	 */
	@DeleteMapping("word/{wordId}")
	public ResponseEntity<Void> deleteWord(@PathVariable Long wordId, Principal principal) {
		try {
			wordService.deleteWord(wordId, principal.getName());
			return ResponseEntity.ok().build();
		} catch (ResponseStatusException e) {
			return ResponseEntity.status(e.getStatusCode()).build();
		}
	}

	/**
	 * Deletes an admin by their ID.
	 * 
	 * @param id The ID of the admin to delete.
	 * @return ResponseEntity with HTTP status NO CONTENT if the admin was deleted,
	 *         or HTTP status NOT FOUND if the admin does not exist.
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
		try {
			adminService.deleteAdmin(id);
			return ResponseEntity.noContent().build();
		} catch (AdminNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	/**
	 * Retrieves a list of all users. Accessible to admins.
	 * 
	 * @return ResponseEntity containing a list of UserDTO and HTTP status OK.
	 */
	@GetMapping("/users")
	public ResponseEntity<List<UserDTO>> getAllUsers() {
		List<User> users = userService.getAllUsers();
		List<UserDTO> userDTOs = users.stream()
				.map(user -> new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getSourceLanguage()))
				.collect(Collectors.toList());
		return ResponseEntity.ok(userDTOs);
	}

	/**
	 * Updates the password of the currently logged-in admin.
	 * 
	 * @param passwordUpdateRequest The DTO containing the new password.
	 * @param principal             The Principal object representing the currently
	 *                              logged-in admin.
	 * @return ResponseEntity with a success message and HTTP status OK, or an error
	 *         message and appropriate HTTP status (NOT FOUND, BAD_REQUEST).
	 */
	@PutMapping("/password")
	public ResponseEntity<?> updatePassword(@Valid @RequestBody PasswordUpdateRequestDTO passwordUpdateRequest,
			Principal principal) {
		String username = principal.getName();
		try {
			adminService.updatePassword(username, passwordUpdateRequest.getNewPassword());
			return ResponseEntity.ok("Admin password updated successfully");
		} catch (UsernameNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	/**
	 * Resets the password of a specific user by an admin. Requires ADMIN role.
	 * 
	 * @param id      The ID of the user whose password needs to be reset.
	 * @param request A map containing the new password under the key "newPassword".
	 * @return ResponseEntity with a success message and HTTP status OK, or an error
	 *         message and appropriate HTTP status (BAD_REQUEST, NOT_FOUND).
	 */
	@PutMapping("/admin/reset-password/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<?> resetUserPasswordByAdmin(@PathVariable Long id, @RequestBody Map<String, String> request) {
		String newPassword = request.get("newPassword");
		if (newPassword == null || newPassword.isEmpty()) {
			return ResponseEntity.badRequest().body("New password cannot be empty");
		}

		try {
			adminService.resetUserPasswordByAdmin(id, newPassword);
			return ResponseEntity.ok("Password reset successfully for user ID: " + id);
		} catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	/**
	 * Updates an existing admin's information.
	 * 
	 * @param id             The ID of the admin to update.
	 * @param adminUpdateDTO The DTO containing the updated information for the
	 *                       admin.
	 * @return ResponseEntity containing the updated AdminResponseDTO and HTTP
	 *         status OK if successful, or an error message and appropriate HTTP
	 *         status (NOT FOUND, BAD_REQUEST, FORBIDDEN).
	 */
	@PutMapping("/{id}")
	public ResponseEntity<AdminResponseDTO> updateAdmin(@PathVariable Long id,
			@Valid @RequestBody AdminUpdateDTO adminUpdateDTO) {
		try {
			AdminResponseDTO updatedAdmin = adminService.updateAdmin(id, adminUpdateDTO);
			return ResponseEntity.ok(updatedAdmin);
		} catch (AdminNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new AdminResponseDTO(null, null, ex.getMessage(), null));
		} catch (SecurityException ex) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(new AdminResponseDTO(null, null, ex.getMessage(), null));
		}
	}
}