package com.lingotower.controller;

import com.lingotower.dto.admin.AdminCreateDTO;
import com.lingotower.dto.admin.AdminUpdateDTO;
import com.lingotower.dto.user.UserDTO;
import com.lingotower.dto.admin.AdminResponseDTO;
import com.lingotower.model.Admin;
import com.lingotower.service.AdminService;
import com.lingotower.service.UserService;
import com.lingotower.service.WordService;
import com.lingotower.dto.baseUser.PasswordUpdateRequestDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.lingotower.model.Role;
import com.lingotower.model.User;
import com.lingotower.security.JwtTokenProvider;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
		Optional<Admin> admin = adminService.getAdminById(id);
		return admin.map(
				a -> ResponseEntity.ok(new AdminResponseDTO(a.getId(), a.getUsername(), a.getEmail(), a.getRole())))
				.orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	@PostMapping("/register")
	public ResponseEntity<String> createAdmin(@RequestBody AdminCreateDTO adminCreateDTO,
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

	// לחשוב אם כדאי לבדוק אם משתמש הוא מנהל
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
			@RequestBody AdminUpdateDTO adminUpdateDTO) {
		Optional<Admin> updatedAdmin = adminService.updateAdmin(id, adminUpdateDTO);
		if (updatedAdmin.isPresent()) {
			Admin a = updatedAdmin.get();
			AdminResponseDTO responseDTO = new AdminResponseDTO(a.getId(), a.getUsername(), a.getEmail(), a.getRole());
			return ResponseEntity.ok(responseDTO);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
		Optional<Admin> admin = adminService.getAdminById(id);
		if (admin.isPresent()) {
			adminService.deleteAdmin(id);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} else {
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
	public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateRequestDTO passwordUpdateRequest,
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
}
