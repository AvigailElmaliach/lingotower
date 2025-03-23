package com.lingotower;

import com.lingotower.ui.LingotowerApp;

import javafx.application.Application;

public class MainApplication {
	public static void main(String[] args) {
		// הקוד פה נועד לצורך בדיקה שהלקוח מצליח לקרוא נתונים מהשרת
		// אפשר למחוק
//		CategoryService categoryService = new CategoryService();
//		List<Category> categories = categoryService.getAllCategories();
//
//		if (categories != null && !categories.isEmpty()) {
//			System.out.println("קטגוריות שנמצאו:");
//			for (Category category : categories) {
//				System.out.println("ID: " + category.getId() + ", Name: " + category.getName());
//			}
//		} else {
//			System.out.println("לא נמצאו קטגוריות.");
//		}

		// Launch the JavaFX application
		Application.launch(LingotowerApp.class, args);
	}
}