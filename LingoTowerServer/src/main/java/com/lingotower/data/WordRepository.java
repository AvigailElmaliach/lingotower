package com.lingotower.data;

//import com.lingotower.model.Category;
import com.lingotower.model.Difficulty;
import com.lingotower.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
	  @Query("SELECT w FROM Word w WHERE w.category.id = :categoryId")
	    List<Word> findByCategoryId(@Param("categoryId") Long categoryId);
   // List<Word> findByCategory(Long categoryId);
  //  List<Word> getCategoryById(Long categoryId);
   // List<Word> findByCategoryId(Long categoryId);
    List<Word> findByCategoryIdAndDifficulty(Long categoryId, Difficulty difficulty);
    Optional<Word> findByWord(String word);
    List<Word> findByCategoryIdAndDifficultyAndTranslation(Long categoryId, Difficulty difficulty, String translation);
    List<Word> findByTranslationIsNull();


}
