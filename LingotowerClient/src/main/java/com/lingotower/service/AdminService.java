package com.lingotower.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType; // Import MediaType
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException; // Import RestClientException

// Assuming these DTOs exist or will be created on the client-side, mirroring the server
import com.lingotower.dto.admin.AdminCreateDTO; // Placeholder for client DTO
import com.lingotower.dto.admin.AdminResponseDTO; // Placeholder for client DTO
import com.lingotower.dto.admin.AdminUpdateDTO; // Placeholder for client DTO
import com.lingotower.model.Admin;
import com.lingotower.model.Role;
import com.lingotower.model.User;

public class AdminService extends BaseService {

	// Base URL for admin-related endpoints on the server
	private static final String ADMIN_API_BASE_URL = "http://localhost:8080/admins";
	// Base URL for admin authentication endpoints on the server
	// private static final String AUTH_ADMIN_API_BASE_URL =
	// "http://localhost:8080/api/auth/admin"; // Not used directly here

	public AdminService() {
		super(); // Initializes RestTemplate from BaseService
		System.out.println("AdminService created");
		// TokenStorage.logTokenStatus("AdminService constructor"); // Optional logging
	}

	/**
	 * Fetches all admins from the server. Requires ADMIN role.
	 * 
	 * @return List of Admins (mapped from AdminResponseDTO).
	 */
	public List<Admin> getAllAdmins() {
		String url = ADMIN_API_BASE_URL;
		try {
			HttpHeaders headers = createAuthHeaders(); // Get auth headers
			HttpEntity<?> entity = new HttpEntity<>(headers);

			// Server returns List<AdminResponseDTO>
			ResponseEntity<List<AdminResponseDTO>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<AdminResponseDTO>>() {
					});

			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				// Map DTOs to Admin models
				return response.getBody().stream().map(dto -> {
					Admin admin = new Admin();
					// Map fields available in AdminResponseDTO
					admin.setUsername(dto.getUsername());
					admin.setRole(dto.getRole().toString()); // Convert Role to String
					// Note: ID, email etc. might not be in the DTO from this specific endpoint
					return admin;
				}).collect(Collectors.toList());
			} else {
				System.err.println("Failed to get admins: " + response.getStatusCode());
				return new ArrayList<>();
			}
		} catch (RestClientException e) {
			System.err.println("Error fetching all admins: " + e.getMessage());
			// Consider logging e.printStackTrace() for detailed debugging
			return new ArrayList<>();
		}
	}

	/**
	 * Fetches a specific admin by ID from the server. Requires ADMIN role.
	 * 
	 * @param id The ID of the admin to fetch.
	 * @return Admin object if found, null otherwise.
	 */
	public Admin getAdminById(Long id) {
		String url = ADMIN_API_BASE_URL + "/" + id;
		try {
			HttpHeaders headers = createAuthHeaders(); // Get auth headers
			HttpEntity<?> entity = new HttpEntity<>(headers);

			// Server returns AdminResponseDTO
			ResponseEntity<AdminResponseDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					AdminResponseDTO.class);

			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				AdminResponseDTO dto = response.getBody();
				Admin admin = new Admin();
				admin.setId(id); // Set the ID from the path param
				admin.setUsername(dto.getUsername());
				admin.setRole(dto.getRole().toString()); // Convert Role to String
				// Full details might require another call or DTO adjustment on server
				return admin;
			} else {
				System.err.println("Failed to get admin by ID " + id + ": " + response.getStatusCode());
				return null;
			}
		} catch (RestClientException e) {
			System.err.println("Error fetching admin by ID " + id + ": " + e.getMessage());
			return null;
		}
	}

	/**
	 * Registers a new admin on the server. Requires current user to be an Admin.
	 * Corresponds to POST /admins/register on the server.
	 * 
	 * @param adminCreateDTO DTO containing new admin's details.
	 * @return true if registration was successful (status 201), false otherwise.
	 */
	public boolean registerAdmin(AdminCreateDTO adminCreateDTO) {
		// Correct endpoint from AdminController
		String url = ADMIN_API_BASE_URL + "/register";
		try {
			HttpHeaders headers = createAuthHeaders(); // Get auth headers from BaseService
			headers.setContentType(MediaType.APPLICATION_JSON); // Set content type for POST body

			HttpEntity<AdminCreateDTO> entity = new HttpEntity<>(adminCreateDTO, headers);

			// Server's register endpoint returns ResponseEntity<String> on success (201
			// Created)
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

			// Check for 201 Created status
			if (response.getStatusCode() == HttpStatus.CREATED) {
				System.out.println("Admin registered successfully: " + response.getBody());
				return true;
			} else {
				System.err
						.println("Failed to register admin: " + response.getStatusCode() + " - " + response.getBody());
				return false;
			}
		} catch (RestClientException e) {
			System.err.println("Error registering admin: " + e.getMessage());
			// Consider handling specific HTTP errors like 403 Forbidden, 400 Bad Request
			return false;
		}
	}

	/**
	 * Updates an existing admin on the server. Requires ADMIN role.
	 * 
	 * @param id             The ID of the admin to update.
	 * @param adminUpdateDTO DTO containing updated details.
	 * @return true if update was successful (status 200), false otherwise.
	 */
	public boolean updateAdmin(Long id, AdminUpdateDTO adminUpdateDTO) {
		String url = ADMIN_API_BASE_URL + "/" + id;
		try {
			HttpHeaders headers = createAuthHeaders(); // Get auth headers
			headers.setContentType(MediaType.APPLICATION_JSON); // Set content type for PUT body

			HttpEntity<AdminUpdateDTO> entity = new HttpEntity<>(adminUpdateDTO, headers);

			// Server PUT /admins/{id} returns AdminResponseDTO on success (200 OK)
			ResponseEntity<AdminResponseDTO> response = restTemplate.exchange(url, HttpMethod.PUT, entity,
					AdminResponseDTO.class);

			// Check for 200 OK status
			return response.getStatusCode() == HttpStatus.OK;

		} catch (RestClientException e) {
			System.err.println("Error updating admin " + id + ": " + e.getMessage());
			return false;
		}
	}

	/**
	 * Deletes an admin from the server. Requires ADMIN role.
	 * 
	 * @param id The ID of the admin to delete.
	 * @return true if deletion was successful (status 204 No Content), false
	 *         otherwise.
	 */
	public boolean deleteAdmin(Long id) {
		String url = ADMIN_API_BASE_URL + "/" + id;
		try {
			HttpHeaders headers = createAuthHeaders(); // Get auth headers
			HttpEntity<?> entity = new HttpEntity<>(headers); // No body needed for DELETE

			ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);

			// Check for 204 No Content status
			return response.getStatusCode() == HttpStatus.NO_CONTENT;

		} catch (RestClientException e) {
			System.err.println("Error deleting admin " + id + ": " + e.getMessage());
			return false;
		}
	}

	/**
	 * Fetches the list of all regular users from the admin endpoint
	 * (/admins/users). Requires ADMIN role.
	 * 
	 * @return List of User objects (mapped from UserDTOs).
	 */
	public List<User> getAllUsers() {
		try {
			System.out.println("AdminService.getAllUsers() called");
			// Create an entity with auth headers
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<?> entity = new HttpEntity<>(headers);

			// Debug output all headers
			headers.forEach((key, values) -> System.out.println(key + ": " + String.join(", ", values)));

			// Make the request
			String url = ADMIN_API_BASE_URL + "/users";
			System.out.println("Making request to: " + url);

			ResponseEntity<List<User>> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					new ParameterizedTypeReference<List<User>>() {
					});

			return response.getBody();
		} catch (Exception e) {
			System.err.println("Error getting all users: " + e.getMessage());
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public Admin createAdmin(Admin newAdmin) {
		AdminCreateDTO dto = new AdminCreateDTO();
		dto.setUsername(newAdmin.getUsername());
		dto.setPassword(newAdmin.getPassword());
		dto.setEmail(newAdmin.getEmail());
		dto.setRole(Role.valueOf(newAdmin.getRole()));

		// Call the register method with the DTO
		boolean success = registerAdmin(dto);

		if (success) {
			// Return the created admin (might need to fetch it by username if ID is needed)
			return newAdmin;
		}
		return null;
	}

	public boolean updateAdmin(Long id, Admin admin) {
		AdminUpdateDTO dto = new AdminUpdateDTO();
		dto.setUsername(admin.getUsername());
		// Only set password if not empty
		if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
			dto.setPassword(admin.getPassword());
		}
		dto.setRole(Role.valueOf(admin.getRole()));

		// Call the update method with the DTO
		return updateAdmin(id, dto);
	}
}
