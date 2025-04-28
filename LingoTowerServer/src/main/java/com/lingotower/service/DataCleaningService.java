package com.lingotower.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.lingotower.model.ExampleSentence;
import com.lingotower.data.ExampleSentenceRepository;
import java.util.List;

@Service
public class DataCleaningService {

	@Autowired
	private ExampleSentenceRepository exampleSentenceRepository;

	public void cleanExampleSentenceText() {
		List<ExampleSentence> allSentences = exampleSentenceRepository.findAll();
		for (ExampleSentence sentence : allSentences) {
			String cleanedSentenceText = sentence.getSentenceText().trim().replaceAll("\\s{2,}", " ");
			String cleanedTranslatedText = (sentence.getTranslatedText() != null)
					? sentence.getTranslatedText().trim().replaceAll("\\s{2,}", " ")
					: null;

			sentence.setSentenceText(cleanedSentenceText);
			sentence.setTranslatedText(cleanedTranslatedText);
			exampleSentenceRepository.save(sentence);
		}
	}
}