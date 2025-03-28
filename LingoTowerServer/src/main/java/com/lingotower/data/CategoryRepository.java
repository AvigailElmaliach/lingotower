package com.lingotower.data;
import com.lingotower.model.Category;
import com.lingotower.model.Word;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    Optional<Category> findById(Long id);
   // public List<Word> findByCategory(Category category);//שינוי אחרון 
   // public List<Word> findByCategory_Id(Long categoryId);

    void deleteAll();



}




