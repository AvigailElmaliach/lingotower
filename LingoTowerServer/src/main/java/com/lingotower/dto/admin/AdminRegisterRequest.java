package com.lingotower.dto.admin;

/**
 * Represents a request to register a new admin. This class contains the details
 * of the new admin along with the email of the current admin performing the
 * registration.
 */
public class AdminRegisterRequest {
	private AdminCreateDTO adminCreateDTO;
	private String currentAdminEmail;

	/**
	 * Default constructor (required for frameworks like Jackson for JSON object
	 * conversion).
	 */
	public AdminRegisterRequest() {
	}

	/**
	 * Constructs an admin registration request with the new admin's details and the
	 * current admin's email.
	 *
	 * @param adminCreateDTO    An {@link AdminCreateDTO} object containing the new
	 *                          admin's details.
	 * @param currentAdminEmail The email of the current admin performing the
	 *                          registration.
	 */
	public AdminRegisterRequest(AdminCreateDTO adminCreateDTO, String currentAdminEmail) {
		this.adminCreateDTO = adminCreateDTO;
		this.currentAdminEmail = currentAdminEmail;
	}

	/**
	 * Retrieves the details of the new admin.
	 *
	 * @return An {@link AdminCreateDTO} object containing the new admin's details.
	 */
	public AdminCreateDTO getAdminCreateDTO() {
		return adminCreateDTO;
	}

	/**
	 * Retrieves the email of the current admin performing the registration.
	 *
	 * @return The email of the current admin.
	 */
	public String getCurrentAdminEmail() {
		return currentAdminEmail;
	}

	/**
	 * Sets the details of the new admin.
	 *
	 * @param adminCreateDTO An {@link AdminCreateDTO} object containing the new
	 *                       admin's details.
	 */
	public void setAdminCreateDTO(AdminCreateDTO adminCreateDTO) {
		this.adminCreateDTO = adminCreateDTO;
	}

	/**
	 * Sets the email of the current admin performing the registration.
	 *
	 * @param currentAdminEmail The email of the current admin.
	 */
	public void setCurrentAdminEmail(String currentAdminEmail) {
		this.currentAdminEmail = currentAdminEmail;
	}
}
