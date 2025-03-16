package com.lingotower;
import com.lingotower.model.Category;
import com.lingotower.service.CategoryService;

import java.util.List;

public class Main {

 public static void main(String[] args) {
	 //הקוד פה נועד לצורך בדיקה שהלקוח מצליח לקרוא נתונים מהשרת 
	 //אפשר למחוק
     CategoryService categoryService = new CategoryService(); 
     List<Category> categories = categoryService.getAllCategories();  
     
     if (categories != null && !categories.isEmpty()) {
         System.out.println("קטגוריות שנמצאו:");
         for (Category category : categories) {
             System.out.println("ID: " + category.getId() + ", Name: " + category.getName());
         }
     } else {
         System.out.println("לא נמצאו קטגוריות.");
     }
 }
}
