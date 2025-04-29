package com.lingotower.service;

import com.lingotower.data.ExampleSentenceRepository;
import com.lingotower.exception.ServiceOperationException;
import com.lingotower.model.ExampleSentence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DataCleaningService {

	@Autowired
	private ExampleSentenceRepository exampleSentenceRepository;

	/**
	 * Cleans the sentence text and translated text of all example sentences by
	 * trimming leading/trailing whitespace and removing redundant spaces.
	 * 
	 * @throws ServiceOperationException if an error occurs during the cleaning
	 *                                   process.
	 */
	public void cleanExampleSentenceText() {
		List<ExampleSentence> allSentences = exampleSentenceRepository.findAll();
		try {
			for (ExampleSentence sentence : allSentences) {
				String cleanedSentenceText = sentence.getSentenceText().trim().replaceAll("\\s{2,}", " ");
				String cleanedTranslatedText = (sentence.getTranslatedText() != null)
						? sentence.getTranslatedText().trim().replaceAll("\\s{2,}", " ")
						: null;

				sentence.setSentenceText(cleanedSentenceText);
				sentence.setTranslatedText(cleanedTranslatedText);
				exampleSentenceRepository.save(sentence);
			}
		} catch (Exception e) {
			throw new ServiceOperationException("An error occurred while cleaning example sentence data.", e);
		}
	}
}