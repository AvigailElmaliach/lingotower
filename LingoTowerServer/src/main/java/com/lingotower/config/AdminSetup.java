package com.lingotower.config;

import com.lingotower.model.Admin;
import com.lingotower.model.Role;
import com.lingotower.data.AdminRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class responsible for setting up an initial admin user upon
 * application startup, only if no admins currently exist in the system.
 */
@Configuration
public class AdminSetup {

	private final AdminRepository adminRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${initial.admin.password}")
	private String initialAdminPassword;

	/**
	 * Constructor receiving dependency injection of AdminRepository and
	 * PasswordEncoder.
	 *
	 * @param adminRepository - Repository for accessing admin data in the database.
	 * @param passwordEncoder - Bean of PasswordEncoder used for encoding passwords.
	 */
	public AdminSetup(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
		this.adminRepository = adminRepository;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * Bean of type CommandLineRunner that executes after the Spring context is
	 * ready. Its purpose is to check if any admins exist in the system, and if not
	 * - create a new initial admin user.
	 *
	 * @return CommandLineRunner - Command-line runner that executes upon
	 * application startup.
	 */
	@Bean
	public CommandLineRunner run() {
		return args -> {
			// Check if any administrators exist in the system.
			if (adminRepository.count() == 0) {
				Admin admin = new Admin();
				admin.setUsername("admin");
				admin.setPassword(passwordEncoder.encode(initialAdminPassword));
				admin.setEmail("admin@lingotower.com");
				admin.setRole(Role.ADMIN);
				adminRepository.save(admin);
				System.out.println("Initial admin user created");
			} else {
				System.out.println("Admin user already exists");
			}
		};
	}
}