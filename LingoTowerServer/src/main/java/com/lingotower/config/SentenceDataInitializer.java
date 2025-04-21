package com.lingotower.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingotower.dto.exampleSentence.ExampleSentenceCreateDTO;
import com.lingotower.model.ExampleSentence;
import com.lingotower.model.Word;
import com.lingotower.data.ExampleSentenceRepository;
import com.lingotower.service.ExampleSentenceService;
import com.lingotower.service.TranslationService;
import com.lingotower.service.WordService;

@Component
@Order(3)
public class SentenceDataInitializer implements CommandLineRunner {

	@Autowired
	private WordService wordService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ResourcePatternResolver resourcePatternResolver;

	@Autowired
	private TranslationService translationService;
	@Autowired
	private ExampleSentenceService exampleSentenceService;
	@Autowired
	private ExampleSentenceRepository exampleSentenceRepository;

	/**
	 * Runs after the application context is loaded to initialize example sentences from JSON files.
	 * It loads sentences and then triggers the translation of any missing translations.
	 * @param args Incoming command line arguments.
	 * @throws Exception If an error occurs during initialization.
	 */
	@Override
	public void run(String... args) throws Exception {
		System.out.println("Initializing example sentences");
		loadExampleSentencesFromJson("classpath:sentences/*.json");
		//exampleSentenceService.translateMissingSentences();
		System.out.println("Example sentence initialization completed.");
	}

	/**
	 * Loads example sentences from JSON files matching the provided location pattern.
	 * It reads each file and processes the word-sentence pairs within, and then updates missing translations.
	 * @param locationPattern The pattern to locate JSON sentence files in the classpath.
	 */
	private void loadExampleSentencesFromJson(String locationPattern) {
		try {
			System.out.println("Loading example sentences from : " + locationPattern);
			Resource[] sentenceFiles = resourcePatternResolver.getResources(locationPattern);

			if (sentenceFiles.length == 0) {
				System.out.println("    No sentence files found matching : " + locationPattern);
				return;
			}

			for (Resource sentenceFile : sentenceFiles) {
				String filename = sentenceFile.getFilename();
				System.out.println("Reading sentence file: " + filename);
				processSentenceFileForLoading(sentenceFile); // Load new sentences and translations
				processSentenceFileForUpdatingTranslations(sentenceFile); // Update existing sentences with translations from file
			}
			System.out.println("  Finished processing example sentences from JSON files.");
		} catch (IOException e) {
			System.err.println("    Error loading/updating example sentences: " + e.getMessage());
		}
	}

	/**
	 * Processes a single JSON sentence file to load new words and their example sentences with translations.
	 * @param sentenceFile The Resource representing the JSON sentence file.
	 */
	private void processSentenceFileForLoading(Resource sentenceFile) {
		try {
			List<ExampleSentenceCreateDTO> wordWithSentencesList = objectMapper.readValue(
					sentenceFile.getInputStream(), new TypeReference<List<ExampleSentenceCreateDTO>>() {});

			for (ExampleSentenceCreateDTO wordWithSentences : wordWithSentencesList) {
				String wordStr = wordWithSentences.getWord();
				Optional<Word> existingWord = wordService.findByWord(wordStr);

				if (existingWord.isPresent()) {
					Word word = existingWord.get();
					List<String> exampleSentences = wordWithSentences.getSentences();
					List<String> exampleTranslations = wordWithSentences.getTranslations();
					if (exampleSentences != null) {
						addSentencesToWord(word, exampleSentences, exampleTranslations);
						wordService.saveWord(word, word.getSourceLanguage(), word.getTargetLanguage());
					}
				} else {
					System.out.println("    Word '" + wordStr + "' not found in the database for sentences.");
				}
			}
			System.out.println("  Loaded words and sentences from file " + sentenceFile.getFilename() + ".");

		} catch (IOException e) {
			System.err.println("    Error reading sentence file " + sentenceFile.getFilename() + ": " + e.getMessage());
		}
	}

	/**
	 * Processes a single JSON sentence file to update existing example sentences in the database with translations
	 * provided in the file.
	 * @param sentenceFile The Resource representing the JSON sentence file.
	 */
	private void processSentenceFileForUpdatingTranslations(Resource sentenceFile) {
		try {
			List<ExampleSentenceCreateDTO> wordWithSentencesList = objectMapper.readValue(
					sentenceFile.getInputStream(), new TypeReference<List<ExampleSentenceCreateDTO>>() {});

			for (ExampleSentenceCreateDTO wordWithSentences : wordWithSentencesList) {
				String wordStr = wordWithSentences.getWord();
				Optional<Word> existingWordOptional = wordService.findByWord(wordStr);

				if (existingWordOptional.isPresent()) {
					Word word = existingWordOptional.get();
					List<String> exampleSentencesFromDto = wordWithSentences.getSentences();
					List<String> exampleTranslationsFromDto = wordWithSentences.getTranslations();

					if (exampleSentencesFromDto != null && exampleTranslationsFromDto != null && exampleSentencesFromDto.size() == exampleTranslationsFromDto.size()) {
						List<ExampleSentence> existingSentences = exampleSentenceRepository.findByWord(word);
						for (int i = 0; i < exampleSentencesFromDto.size(); i++) {
							String sentenceTextFromDto = exampleSentencesFromDto.get(i);
							String translatedTextFromDto = exampleTranslationsFromDto.get(i);

							for (ExampleSentence existingSentence : existingSentences) {
								if (existingSentence.getSentenceText().equals(sentenceTextFromDto) && (existingSentence.getTranslatedText() == null || existingSentence.getTranslatedText().isEmpty()) && translatedTextFromDto != null && !translatedTextFromDto.isEmpty()) {
									existingSentence.setTranslatedText(translatedTextFromDto);
									exampleSentenceRepository.save(existingSentence);
								}
							}
						}
					}
				}
			}
			System.out.println("  Updated missing translations from file " + sentenceFile.getFilename() + ".");

		} catch (IOException e) {
			System.err.println("    Error updating translations from file " + sentenceFile.getFilename() + ": " + e.getMessage());
		}
	}

	/**
	 * Adds a list of example sentences (with their translations) to a given Word object.
	 * It checks for existing sentences before adding new ones.
	 * @param word The Word object to add sentences to.
	 * @param exampleSentences The list of example sentences to add.
	 * @param exampleTranslations The list of corresponding translations for the sentences.
	 * @return The number of sentences added to the word.
	 */
	private int addSentencesToWord(Word word, List<String> exampleSentences, List<String> exampleTranslations) {
		List<ExampleSentence> newSentences = new ArrayList<>();
		int sentencesAddedCount = 0;

		if (word.getSentences() == null) {
			word.setSentences(new ArrayList<>());
		}

		for (int i = 0; i < exampleSentences.size(); i++) {
			String sentenceText = exampleSentences.get(i);//first check if exist translation in the json file
			String translatedText = getTranslatedText(word, sentenceText, exampleTranslations, i);

			if (!sentenceExists(word.getSentences(), sentenceText)) {
				ExampleSentence exampleSentence = new ExampleSentence(sentenceText, translatedText, word);
				newSentences.add(exampleSentence);
				sentencesAddedCount++;
			}
		}
		word.getSentences().addAll(newSentences);
		return sentencesAddedCount;
	}

	/**
	 * Retrieves the translated text for a sentence. It first checks if a translation is provided
	 * in the exampleTranslations list. If not, it uses the translation service.
	 * @param word The Word object associated with the sentence.
	 * @param sentenceText The original text of the sentence.
	 * @param exampleTranslations The list of provided translations.
	 * @param index The index of the sentence in the list.
	 * @return The translated text of the sentence.
	 */
	private String getTranslatedText(Word word, String sentenceText, List<String> exampleTranslations, int index) {
		if (exampleTranslations != null && index < exampleTranslations.size() && exampleTranslations.get(index) != null && !exampleTranslations.get(index).isEmpty()) {
			return exampleTranslations.get(index);
		} else {
			try {
				return translationService.translateText(sentenceText, word.getSourceLanguage(), word.getTargetLanguage());
			} catch (HttpClientErrorException.TooManyRequests e) {
				System.out.println("Translation API limit reached for sentence: " + sentenceText + ". Translation not available.");
				return null; // Or return an empty string, depending on your logic
			} catch (Exception e) {
				System.err.println("Error during translation for sentence: " + sentenceText + ": " + e.getMessage());
				return null; // Handle other potential exceptions as well
			}
		}
	}

	/**
	 * Checks if a given sentence text already exists in the list of example sentences for a word.
	 * This is used to prevent duplicate entries.
	 * @param existingSentences The list of existing example sentences for the word.
	 * @param sentenceText The sentence text to check for.
	 * @return True if the sentence exists, false otherwise.
	 */
	private boolean sentenceExists(List<ExampleSentence> existingSentences, String sentenceText) {
		for (ExampleSentence existingSentence : existingSentences) {
			if (existingSentence.getSentenceText().equals(sentenceText)) {
				return true;
			}
		}
		return false;
	}
}