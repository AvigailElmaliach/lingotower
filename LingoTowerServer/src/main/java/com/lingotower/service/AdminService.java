package com.lingotower.service;

import com.lingotower.data.AdminRepository;
import com.lingotower.data.BaseUserRepository;
import com.lingotower.data.UserRepository;
import com.lingotower.dto.admin.AdminCreateDTO;
import com.lingotower.dto.admin.AdminResponseDTO;
import com.lingotower.dto.admin.AdminUpdateDTO;
import com.lingotower.exception.AdminNotFoundException;
import com.lingotower.exception.IncorrectPasswordException;
import com.lingotower.model.Admin;
import com.lingotower.model.Role;
import com.lingotower.model.User;
import com.lingotower.security.JwtTokenProvider;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AdminService extends BaseUserService<Admin> {

	private static final Logger logger = LoggerFactory.getLogger(AdminService.class);
	private final AdminRepository adminRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;

	@Autowired
	public AdminService(PasswordEncoder passwordEncoder, BaseUserRepository<Admin> baseUserRepository,
			AdminRepository adminRepository, JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
		super(passwordEncoder, baseUserRepository);
		this.adminRepository = adminRepository;
		this.jwtTokenProvider = jwtTokenProvider;
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
	}

	// Change password of an admin by username
	@Override
	public void updatePassword(String username, String newPassword) {
		Admin admin = findByUsername(username);
		super.updatePasswordInternal(admin, newPassword);
	}

	// Save admin to the database
	@Override
	protected void saveUser(Admin admin) {
		adminRepository.save(admin);
	}

	// Find an admin by their username
	@Override
	public Admin findByUsername(String username) {
		return adminRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Admin not found with username: " + username));
	}

	// Get a list of all admins
	public List<Admin> getAllAdmins() {
		return adminRepository.findAll();
	}

//	// Get one admin by ID
	public Admin getAdminById(Long id) {
		return adminRepository.findById(id)
				.orElseThrow(() -> new AdminNotFoundException("Admin not found with ID: " + id));
	}

	// Save a given admin object
	public Admin saveAdmin(Admin admin) {
		return adminRepository.save(admin);
	}

	// Delete an admin by their ID
	public void deleteAdmin(Long id) {
		if (!adminRepository.existsById(id)) {
			throw new AdminNotFoundException("Admin not found with ID: " + id);
		}
		adminRepository.deleteById(id);
	}

	// Create a new admin, only if the request is made by an existing admin
	public void registerAdmin(AdminCreateDTO adminCreateDTO, String token) {
		String cleanToken = token.replace("Bearer ", "");
		String role = jwtTokenProvider.extractRole(cleanToken);

		if (!role.equals("ADMIN")) {
			throw new SecurityException("Only admins can register new admins.");
		}

		if (adminRepository.existsByEmail(adminCreateDTO.getEmail())) {
			throw new IllegalArgumentException("Email already exists");
		}

		if (!AuthHelperService.isPasswordStrong(adminCreateDTO.getPassword())) {
			throw new IllegalArgumentException("Password is too weak. Please choose a stronger password.");
		}

		Admin newAdmin = new Admin(adminCreateDTO.getUsername(), passwordEncoder.encode(adminCreateDTO.getPassword()),
				adminCreateDTO.getEmail(), adminCreateDTO.getSourceLanguage(), adminCreateDTO.getTargetLanguage(),
				Role.ADMIN);

		adminRepository.save(newAdmin);
	}

	@Transactional
	public void resetUserPasswordByAdmin(Long userId, String newPassword) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}

	public AdminResponseDTO updateAdmin(Long id, AdminUpdateDTO adminUpdateDTO) {
		Admin admin = adminRepository.findById(id)
				.orElseThrow(() -> new AdminNotFoundException("Admin not found with ID: " + id));
		logger.info("Attempting to update admin with ID: {}", id);
		logger.debug("Old password received from client: {}", adminUpdateDTO.getOldPassword());
		logger.debug("Current password stored in database: {}", admin.getPassword());

		// Check if the old password matches
		if (passwordEncoder.matches(adminUpdateDTO.getOldPassword(), admin.getPassword())) {
			// Only if the old password is correct, proceed to update other fields
			updateAdminFields(admin, adminUpdateDTO);
			Admin updatedAdmin = adminRepository.save(admin);
			logger.info("Successfully updated admin with ID: {}", id);
			return new AdminResponseDTO(updatedAdmin.getId(), updatedAdmin.getUsername(), updatedAdmin.getEmail(),
					updatedAdmin.getRole());
		} else {
			logger.warn("Incorrect current password provided for admin ID: {}", id);
			throw new IncorrectPasswordException("Incorrect current password");
		}
	}

	// Helper method to update fields of the admin if they are not null
	private void updateAdminFields(Admin admin, AdminUpdateDTO adminUpdateDTO) {
		if (adminUpdateDTO.getUsername() != null) {
			admin.setUsername(adminUpdateDTO.getUsername());
		}

		if (adminUpdateDTO.getRole() != null) {
			admin.setRole(adminUpdateDTO.getRole());
		}

		if (adminUpdateDTO.getEmail() != null) {
			admin.setEmail(adminUpdateDTO.getEmail());
		}

		if (adminUpdateDTO.getPassword() != null && !adminUpdateDTO.getPassword().isEmpty()) {
			String encodedPassword = passwordEncoder.encode(adminUpdateDTO.getPassword());
			admin.setPassword(encodedPassword);
		}
	}
}
