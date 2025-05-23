package com.lingotower.data;

import com.lingotower.model.Category;
import com.lingotower.model.Word;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	Optional<Category> findByName(String name);

	Optional<Category> findById(Long id);

	List<Category> findByTranslationIsNullOrTranslationIs(String translation);

	void deleteAll();

}
