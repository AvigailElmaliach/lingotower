package com.lingotower;

import java.util.List;

import com.lingotower.model.Category;
import com.lingotower.model.User;
import com.lingotower.service.AuthService;
import com.lingotower.service.CategoryService;
import com.lingotower.ui.LingotowerApp;

import javafx.application.Application;

public class MainApplication {
	public static void main(String[] args) {
		AuthService authService = new AuthService();
		boolean authenticated = false;

		// Step 1: Try to login with existing user first
		System.out.println("Trying to login with existing user...");
		User loggedInUser = authService.login("testuser", "password123");

		// If login fails, try to register with a different username
		if (loggedInUser == null) {
			System.out.println("Login failed, trying to register a new user...");

			// Generate a unique username with timestamp
			String uniqueUsername = "user" + System.currentTimeMillis();
			String password = "password123";
			String email = "test@example.com";
			String language = "en";

			System.out.println("Registering new user: " + uniqueUsername);
			User registeredUser = authService.register(uniqueUsername, password, email, language);

			if (registeredUser != null) {
				System.out.println("Registration successful!");

				// Now log in with the new user
				loggedInUser = authService.login(uniqueUsername, password);
				if (loggedInUser != null) {
					System.out.println("Login successful with new user!");
					authenticated = true;
				}
			} else {
				System.out.println("Registration failed!");
			}
		} else {
			System.out.println("Login successful with existing user!");
			authenticated = true;
		}

		// If we're authenticated, try to access categories
		if (authenticated) {
			CategoryService categoryService = new CategoryService();
			try {
				List<Category> categories = categoryService.getAllCategories();

				System.out.println("Categories found: " + (categories != null ? categories.size() : 0));

				if (categories != null && !categories.isEmpty()) {
					System.out.println("Categories:");
					for (Category category : categories) {
						System.out.println("ID: " + category.getId() + ", Name: " + category.getName());
					}
				} else {
					System.out.println("No categories found.");
				}
			} catch (Exception e) {
				System.err.println("Error accessing categories: " + e.getMessage());
			}
		}

		// Launch the JavaFX application
		Application.launch(LingotowerApp.class, args);
	}
}