package com.lingotower.service;
import com.lingotower.model.Category;
import com.lingotower.data.CategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
@Service
public class CategoryService {
	 private final CategoryRepository categoryRepository;
	 
	 public CategoryService(CategoryRepository categoryRepository) {
	        this.categoryRepository = categoryRepository;
	    }

	    public List<Category> getAllCategories() {
	        return categoryRepository.findAll();
	    }

	    public Optional<Category> getCategoryById(Long id) {
	        return categoryRepository.findById(id);
	    }
	    public Category addCategory(Category category) {
	        // בדיקת אם הקטגוריה עם אותו שם כבר קיימת
	        if (categoryRepository.findByName(category.getName()).isPresent()) {
	            throw new IllegalArgumentException("קטגוריה עם שם זהה כבר קיימת");
	        }
	       
	        return categoryRepository.save(category);
	    }
	    public void deleteAllCategories() {
	        categoryRepository.deleteAll();
	    }

	    public void deleteCategory(Long id) {
	        categoryRepository.deleteById(id);
	    }
	   
	    

}






   

    

