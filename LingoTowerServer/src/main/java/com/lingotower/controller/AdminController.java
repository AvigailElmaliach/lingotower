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
	private WordService wordService;

	public AdminController(AdminService adminService, JwtTokenProvider jwtTokenProvider, UserService userService,
			WordService wordService) {
		this.adminService = adminService;
		this.jwtTokenProvider = jwtTokenProvider;
		this.userService = userService;
		this.wordService = wordService;
	}

	@GetMapping
	public List<AdminResponseDTO> getAllAdmins() {
		List<Admin> admins = adminService.getAllAdmins();
		return admins.stream().map(
				admin -> new AdminResponseDTO(admin.getId(), admin.getUsername(), admin.getEmail(), admin.getRole()))
				.collect(Collectors.toList());
	}

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

	@DeleteMapping("word/{wordId}")
	public ResponseEntity<Void> deleteWord(@PathVariable Long wordId, Principal principal) {
		try {
			wordService.deleteWord(wordId, principal.getName());
			return ResponseEntity.ok().build();
		} catch (ResponseStatusException e) {
			return ResponseEntity.status(e.getStatusCode()).build();// לבדוק אם נכון סטוטס קוד
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<AdminResponseDTO> updateAdmin(@PathVariable Long id,
			@Valid @RequestBody AdminUpdateDTO adminUpdateDTO) {
		try {
			AdminResponseDTO updatedAdmin = adminService.updateAdmin(id, adminUpdateDTO);
			return ResponseEntity.ok(updatedAdmin);
		} catch (AdminNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
		try {
			adminService.deleteAdmin(id);
			return ResponseEntity.noContent().build();
		} catch (AdminNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@GetMapping("/users")
	public ResponseEntity<List<UserDTO>> getAllUsers() {
		List<User> users = userService.getAllUsers();
		List<UserDTO> userDTOs = users.stream()
				.map(user -> new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getSourceLanguage()))
				.collect(Collectors.toList());
		return ResponseEntity.ok(userDTOs);
	}

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
}
