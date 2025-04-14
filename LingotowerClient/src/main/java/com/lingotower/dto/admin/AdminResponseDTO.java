package com.lingotower.dto.admin;

import com.lingotower.model.Role;

public class AdminResponseDTO {

	private String username;
	private String email;
	private Long id;
	private Role role;

	public AdminResponseDTO(Long id, String username, String email, Role role) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
}
