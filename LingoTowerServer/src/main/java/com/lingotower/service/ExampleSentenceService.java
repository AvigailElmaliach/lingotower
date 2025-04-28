
package com.lingotower.service;

import com.lingotower.model.ExampleSentence;
import com.lingotower.model.Word;
import com.lingotower.constants.LanguageConstants;
import com.lingotower.data.ExampleSentenceRepository;
import com.lingotower.data.WordRepository;
import com.lingotower.dto.exampleSentence.ExampleSentenceCreateDTO;

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

	public ExampleSentenceCreateDTO getTwoExampleSentencesForWord(String word, String username) {
		Optional<Word> optionalWord = wordRepository.findByWord(word);
		if (optionalWord.isPresent()) {
			Word foundWord = optionalWord.get();
			String userTargetLanguage = wordService.getUserLanguage(username);
			List<ExampleSentence> sentences = exampleSentenceRepository.findByWord(foundWord);

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
		return null;
	}

}
