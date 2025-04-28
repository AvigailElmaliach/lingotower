package com.lingotower.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import com.lingotower.dto.admin.AdminCreateDTO;
import com.lingotower.dto.admin.AdminResponseDTO;
import com.lingotower.dto.admin.AdminUpdateDTO;
import com.lingotower.model.Admin;
import com.lingotower.model.Role;
import com.lingotower.model.User;

/**
 * Service for managing administrators and admin-only functionality.
 */
public class AdminService extends BaseService {

	/**
	 * Constructor for AdminService.
	 */
	public AdminService() {
		super(); // Initialize the base service
		logger.debug("AdminService initialized");
	}

	/**
	 * Fetches all admins from the server. Requires ADMIN role.
	 * 
	 * @return List of Admins (mapped from AdminResponseDTO)
	 */
	public List<Admin> getAllAdmins() {
		try {
			logger.info("Fetching all admins");

			String url = buildUrl(ADMINS_PATH);
			HttpEntity<?> entity = createAuthEntity(null);

			ResponseEntity<List<AdminResponseDTO>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<AdminResponseDTO>>() {
					});

			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				// Map DTOs to Admin models
				List<Admin> admins = response.getBody().stream().map(dto -> {
					Admin admin = new Admin();
					admin.setUsername(dto.getUsername());
					admin.setRole(dto.getRole().toString());
					admin.setEmail(dto.getEmail());
					admin.setId(dto.getId());
					return admin;
				}).collect(Collectors.toList());

				logger.info("Successfully retrieved {} admins", admins.size());
				return admins;
			} else {
				logger.error("Failed to get admins. Status code: {}", response.getStatusCode());
				return new ArrayList<>();
			}
		} catch (RestClientException e) {
			logger.error("Error fetching all admins: {}", e.getMessage(), e);
			return new ArrayList<>();
		}
	}

	/**
	 * Fetches a specific admin by ID from the server. Requires ADMIN role.
	 * 
	 * @param id The ID of the admin to fetch
	 * @return Admin object if found, null otherwise
	 */
	public Admin getAdminById(Long id) {
		try {
			logger.info("Fetching admin with ID: {}", id);

			String url = buildUrl(ADMINS_PATH, id.toString());
			HttpEntity<?> entity = createAuthEntity(null);

			ResponseEntity<AdminResponseDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					AdminResponseDTO.class);

			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				AdminResponseDTO dto = response.getBody();
				Admin admin = new Admin();
				admin.setId(id);
				admin.setUsername(dto.getUsername());
				admin.setRole(dto.getRole().toString());
				admin.setEmail(dto.getEmail());

				logger.info("Successfully retrieved admin with ID: {}", id);
				return admin;
			} else {
				logger.error("Failed to get admin by ID {}. Status code: {}", id, response.getStatusCode());
				return null;
			}
		} catch (RestClientException e) {
			logger.error("Error fetching admin by ID {}: {}", id, e.getMessage(), e);
			return null;
		}
	}

	public ResponseEntity<String> registerAdmin(AdminCreateDTO adminCreateDTO) {
		try {
			logger.info("Registering new admin: {}", adminCreateDTO.getUsername());

			String url = buildUrl(ADMINS_PATH, "register");

			HttpHeaders headers = createAuthHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<AdminCreateDTO> entity = new HttpEntity<>(adminCreateDTO, headers);

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

			logger.debug("Register admin response status: {}", response.getStatusCode());
			if (response.getStatusCode() == HttpStatus.CREATED) {
				logger.info("Successfully registered admin: {}", adminCreateDTO.getUsername());
			} else {
				logger.error("Failed to register admin. Status code: {}", response.getStatusCode());
				if (response.hasBody()) {
					logger.error("Error details: {}", response.getBody());
				}
			}
			return response;
		} catch (RestClientException e) {
			logger.error("Error registering admin: {}", e.getMessage(), e);
			// Handle RestClientException to potentially extract error message
			if (e instanceof HttpClientErrorException) {
				return ResponseEntity.status(((HttpClientErrorException) e).getStatusCode())
						.body(((HttpClientErrorException) e).getResponseBodyAsString());
			} else if (e instanceof HttpServerErrorException) {
				return ResponseEntity.status(((HttpServerErrorException) e).getStatusCode())
						.body(((HttpServerErrorException) e).getResponseBodyAsString());
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
		}
	}

	/**
	 * Updates an existing admin on the server. Requires ADMIN role.
	 *
	 * @param id             The ID of the admin to update
	 * @param adminUpdateDTO DTO containing updated details
	 * @return AdminResponseDTO of the updated admin
	 */
	public AdminResponseDTO updateAdmin(Long id, AdminUpdateDTO adminUpdateDTO) {
		try {
			logger.info("Updating admin with ID: {}", id);

			String url = buildUrl(ADMINS_PATH, id.toString());

			HttpHeaders headers = createAuthHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<AdminUpdateDTO> entity = new HttpEntity<>(adminUpdateDTO, headers);

			ResponseEntity<AdminResponseDTO> response = restTemplate.exchange(url, HttpMethod.PUT, entity,
					AdminResponseDTO.class);

			if (response.getStatusCode() == HttpStatus.OK) {
				logger.info("Successfully updated admin with ID: {}", id);
				return response.getBody();
			} else {
				logger.error("Failed to update admin with ID: {}. Status code: {}", id, response.getStatusCode());
				return null;
			}
		} catch (RestClientException e) {
			logger.error("Error updating admin {}: {}", id, e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Deletes an admin from the server. Requires ADMIN role.
	 * 
	 * @param id The ID of the admin to delete
	 * @return true if deletion was successful, false otherwise
	 */
	public boolean deleteAdmin(Long id) {
		try {
			logger.info("Deleting admin with ID: {}", id);

			String url = buildUrl(ADMINS_PATH, id.toString());
			HttpEntity<?> entity = createAuthEntity(null);

			ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);

			boolean success = response.getStatusCode() == HttpStatus.NO_CONTENT;
			if (success) {
				logger.info("Successfully deleted admin with ID: {}", id);
			} else {
				logger.error("Failed to delete admin with ID: {}. Status code: {}", id, response.getStatusCode());
			}
			return success;
		} catch (RestClientException e) {
			logger.error("Error deleting admin {}: {}", id, e.getMessage(), e);
			return false;
		}
	}

	/**
	 * Fetches the list of all regular users from the admin endpoint
	 * (/admins/users). Requires ADMIN role.
	 * 
	 * @return List of User objects
	 */
	public List<User> getAllUsers() {
		try {
			logger.info("Fetching all users as admin");

			String url = buildUrl(ADMINS_PATH, "users");
			HttpEntity<?> entity = createAuthEntity(null);

			// Debug output all headers
			HttpHeaders headers = ((HttpEntity<?>) entity).getHeaders();
			logger.debug("Request headers for getAllUsers:");
			headers.forEach((key, values) -> logger.debug("  {} = {}", key, String.join(", ", values)));

			logger.debug("Making request to: {}", url);

			ResponseEntity<List<User>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<User>>() {
					});

			if (response.getStatusCode().is2xxSuccessful()) {
				List<User> users = response.getBody();
				logger.info("Successfully retrieved {} users as admin", users != null ? users.size() : 0);
				return users != null ? users : new ArrayList<>();
			} else {
				logger.error("Failed to get users as admin. Status code: {}", response.getStatusCode());
				return new ArrayList<>();
			}
		} catch (Exception e) {
			logger.error("Error getting users as admin: {}", e.getMessage(), e);
			return new ArrayList<>();
		}
	}

	public String createAdmin(Admin newAdmin) {
		try {
			logger.info("Creating new admin: {}", newAdmin.getUsername());

			AdminCreateDTO dto = new AdminCreateDTO();
			dto.setUsername(newAdmin.getUsername());
			dto.setPassword(newAdmin.getPassword());
			dto.setEmail(newAdmin.getEmail());
			dto.setRole(Role.valueOf(newAdmin.getRole()));
			dto.setSourceLanguage(newAdmin.getSourceLanguage());
			dto.setTargetLanguage(newAdmin.getTargetLanguage());

			ResponseEntity<String> response = registerAdmin(dto);

			if (response.getStatusCode() == HttpStatus.CREATED) {
				logger.info("Successfully created admin: {}", newAdmin.getUsername());
				return null;
			} else {
				logger.error("Failed to create admin: {}", response.getBody());
				return response.getBody();
			}
		} catch (Exception e) {
			logger.error("Error creating admin: {}", e.getMessage(), e);
			return "An unexpected error occurred.";
		}
	}

	/**
	 * Deletes a word as an admin.
	 * 
	 * @param id The ID of the word to delete
	 * @return true if deletion was successful, false otherwise
	 */
	public boolean deleteWordAdmin(Long id) {
		try {
			logger.info("Deleting word with ID {} as admin", id);

			String url = buildUrl(ADMINS_PATH, "word", id.toString());
			HttpEntity<?> entity = createAuthEntity(null);

			ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);

			boolean success = response.getStatusCode().is2xxSuccessful();
			if (success) {
				logger.info("Successfully deleted word with ID {} as admin", id);
			} else {
				logger.error("Failed to delete word with ID {} as admin. Status code: {}", id,
						response.getStatusCode());
			}
			return success;
		} catch (Exception e) {
			logger.error("Error deleting word with ID {} as admin: {}", id, e.getMessage(), e);
			return false;
		}
	}
}