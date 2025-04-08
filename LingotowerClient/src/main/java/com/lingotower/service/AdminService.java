package com.lingotower.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.lingotower.model.Admin;
import com.lingotower.model.User;
import com.lingotower.security.TokenStorage;

public class AdminService extends BaseService {

	private static final String BASE_URL = "http://localhost:8080/admins";

	public AdminService() {
		super(); // This initializes the RestTemplate with error handler
		System.out.println("AdminService created");
		TokenStorage.logTokenStatus("AdminService constructor");
	}

	public List<Admin> getAllAdmins() {
		ResponseEntity<List> response = restTemplate.exchange(BASE_URL, HttpMethod.GET, null, List.class);
		return response.getBody();
	}

	public Admin getAdminById(Long id) {
		String url = BASE_URL + "/" + id;
		ResponseEntity<Admin> response = restTemplate.exchange(url, HttpMethod.GET, null, Admin.class);
		if (response.getStatusCode() == HttpStatus.OK) {
			return response.getBody();
		}
		return null;
	}

	public Admin createAdmin(Admin admin) {
		ResponseEntity<Admin> response = restTemplate.exchange(BASE_URL, HttpMethod.POST, new HttpEntity<>(admin),
				Admin.class);
		return response.getBody();
	}

	public boolean updateAdmin(Long id, Admin admin) {
		String url = BASE_URL + "/" + id;
		HttpEntity<Admin> entity = new HttpEntity<>(admin);
		ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
		return response.getStatusCode() == HttpStatus.OK;
	}

	public boolean deleteAdmin(Long id) {
		String url = BASE_URL + "/" + id;
		ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
		return response.getStatusCode() == HttpStatus.NO_CONTENT;
	}

	public List<User> getAllUsers() {
		try {
			System.out.println("AdminService.getAllUsers() called");
			// Create an entity with auth headers
			HttpHeaders headers = createAuthHeaders();
			HttpEntity<?> entity = new HttpEntity<>(headers);

			// Debug output all headers
			headers.forEach((key, values) -> System.out.println(key + ": " + String.join(", ", values)));

			// Make the request
			String url = BASE_URL + "/users";
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
}
