package com.lingotower;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@SpringBootApplication
public class LingotowerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LingotowerApplication.class, args);
    }
}

//    @Bean
//    CommandLineRunner checkBeans(ApplicationContext ctx) {
//        return args -> {
//            System.out.println("🔹 רשימת כל ה-Beans הרשומים ב-Spring:");
//            String[] beanNames = ctx.getBeanDefinitionNames();
//            for (String beanName : beanNames) {
//                System.out.println(beanName);
//            }
//        };
//    }
//}

	
	
	
	
	
	
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

