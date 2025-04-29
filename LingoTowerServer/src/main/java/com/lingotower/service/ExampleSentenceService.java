package com.lingotower.service;

import com.lingotower.constants.LanguageConstants;
import com.lingotower.data.ExampleSentenceRepository;
import com.lingotower.data.WordRepository;
import com.lingotower.dto.exampleSentence.ExampleSentenceCreateDTO;
import com.lingotower.exception.ServiceOperationException;
import com.lingotower.model.ExampleSentence;
import com.lingotower.model.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExampleSentenceService {

	@Autowired
	private ExampleSentenceRepository exampleSentenceRepository;

	@Autowired
	private WordRepository wordRepository;

	@Autowired
	private WordService wordService;

	@Autowired
	private TranslationService translationService;

	/**
	 * Retrieves up to two example sentences for a given word based on the user's
	 * target language.
	 * 
	 * @param word     The word to find example sentences for.
	 * @param username The username of the user to determine the target language.
	 * @return An ExampleSentenceCreateDTO containing the word and a list of example
	 *         sentences, or null if the word is not found.
	 * @throws ServiceOperationException if an error occurs while retrieving data.
	 */
	public ExampleSentenceCreateDTO getTwoExampleSentencesForWord(String word, String username) {
		Optional<Word> optionalWord = wordRepository.findByWord(word);
		if (optionalWord.isEmpty()) {
			throw new ServiceOperationException("Word not found: " + word);
		}
		Word foundWord = optionalWord.get();
		String userTargetLanguage = wordService.getUserLanguage(username);
		List<ExampleSentence> sentences = exampleSentenceRepository.findByWord(foundWord);

		if (sentences.isEmpty()) {

			return new ExampleSentenceCreateDTO(foundWord.getWord(), Collections.emptyList());
		}

		List<String> sentenceTexts = sentences.stream().map(sentence -> {
			if (userTargetLanguage.equalsIgnoreCase(LanguageConstants.HEBREW)) {
				return sentence.getTranslatedText();
			} else if (userTargetLanguage.equalsIgnoreCase(LanguageConstants.ENGLISH)) {
				return sentence.getSentenceText();
			} else {
				return null;
			}
		}).filter(text -> text != null && !text.isEmpty()).limit(2).collect(Collectors.toList());

		return new ExampleSentenceCreateDTO(foundWord.getWord(), sentenceTexts);
	}

}