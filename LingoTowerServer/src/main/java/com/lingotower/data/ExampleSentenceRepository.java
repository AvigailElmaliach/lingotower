package com.lingotower.data;

import com.lingotower.model.ExampleSentence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.lingotower.model.Word;

import java.util.List;

@Repository
public interface ExampleSentenceRepository extends JpaRepository<ExampleSentence, Long> {
	List<ExampleSentence> findByWord(Word word);

}
