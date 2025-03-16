package com.lingotower;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import com.lingotower.service.CategoryService;
import com.lingotower.model.Category;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LingotowerApplication {
	public static void main(String[] args) {
		SpringApplication.run(LingotowerApplication.class, args);
	}

	//@Bean
/*	public CommandLineRunner demo(CategoryService categoryService) {
		return (args) -> {
			/* יצירת קטגוריה חדשה
			Category newCategory = new Category();
			newCategory.setName("Language Learning");
			categoryService.saveCategory(newCategory);

			// הצגת כל הקטגוריות
			//categoryService.getAllCategories().forEach(category -> System.out.println(category.getName()));
		};
	} */
}
