package com.lingotower;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LingotowerApplication {
	public static void main(String[] args) {
		SpringApplication.run(LingotowerApplication.class, args);
	}

//	@Bean
//	CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//		return args -> {
//			if (!userRepository.findByUsername("demo").isPresent()) {
//				User user = new User();
//				user.setUsername("demo");
//				user.setPassword(passwordEncoder.encode("password"));
//				user.setEmail("demo@example.com");
//				user.setLanguage("Hebrew");
//				userRepository.save(user);
//				System.out.println("Created default user: demo");
//			}
//		};
}
