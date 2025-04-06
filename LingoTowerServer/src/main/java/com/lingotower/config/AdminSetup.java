package com.lingotower.config;
import com.lingotower.model.Admin;
import com.lingotower.model.Role;
import com.lingotower.data.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSetup {
	


	    private final AdminRepository adminRepository;
	    private final PasswordEncoder passwordEncoder;

	    public AdminSetup(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
	        this.adminRepository = adminRepository;
	        this.passwordEncoder = passwordEncoder;
	    }


	    @Bean
	    public CommandLineRunner run() {
	        return args -> {
	            // בדוק אם כבר קיים מנהל ראשוני
	            if (!adminRepository.existsByUsername("admin")) {
	                // יצירת מנהל ראשוני
	                Admin admin = new Admin();
	                admin.setUsername("admin");
	                admin.setPassword(passwordEncoder.encode("admin_password")); 
	                admin.setEmail("admin@lingotower.com");
	                admin.setRole(Role.ADMIN);  
	                adminRepository.save(admin);
	                System.out.println("Admin user created");
	            }
	        };
	    }
	}






