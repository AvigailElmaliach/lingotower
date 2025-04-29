package com.lingotower.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingotower.model.Category;
import com.lingotower.model.Word;
import com.lingotower.constants.LanguageConstants;
import com.lingotower.dto.word.WordDTO;
import com.lingotower.service.CategoryService;
import com.lingotower.service.TranslationService;
import com.lingotower.service.WordService;

@Component
@Order(2) // Ensures words are loaded after categories
public class WordDataInitializer implements CommandLineRunner {

	@Autowired
	private WordService wordService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private TranslationService translationService;

	/**
	 * Runs after the application context is loaded to initialize word data. It
	 * loads words from JSON files based on a predefined category mapping. This
	 * initializer is ordered to run after CategoryDataInitializer.
	 * 
	 * @param args Command line arguments passed to the application.
	 */
	@Override
	public void run(String... args) {
		System.out.println("Initializing words...");
		loadAllCategoriesAndWords();
		System.out.println("Word initialization completed.");
	}

	/**
	 * Loads words for all predefined categories from their respective JSON files.
	 * It iterates through the category mapping, loads words for each category, and
	 * processes them to save into the database.
	 */
	private void loadAllCategoriesAndWords() {
		Map<String, String> categoryMapping = getCategoryMapping();

		for (Map.Entry<String, String> entry : categoryMapping.entrySet()) {
			String resourcePath = "classpath:" + entry.getKey();
			String categoryName = entry.getValue();
			loadWordsForCategory(resourcePath, categoryName);
		}
	}

	/**
	 * Defines a mapping between JSON file names and their corresponding category
	 * names. This mapping is used to load words into the correct categories.
	 * 
	 * @return A map where the key is the JSON file name and the value is the
	 *         category name.
	 */
	private Map<String, String> getCategoryMapping() {
		Map<String, String> mapping = new HashMap<>();
		mapping.put("everyday.json", "Everyday Life and Essential Vocabulary");
		mapping.put("people.json", "People and Relationships");
		mapping.put("work.json", "Work and Education");
		mapping.put("health.json", "Health and Well being");
		mapping.put("travel.json", "Travel and Leisure");
		mapping.put("environment.json", "Environment and Nature");
		return mapping;
	}

	/**
	 * Loads words for a specific category from a JSON file and saves them to the
	 * database. It retrieves or creates the category, loads words from the file,
	 * and then processes each word.
	 * 
	 * @param resourcePath The classpath path to the JSON file containing words for
	 *                     the category.
	 * @param categoryName The name of the category to which the words belong.
	 */
	private void loadWordsForCategory(String resourcePath, String categoryName) {
		try {
			Word[] wordsArray = loadWordsFromFile(resourcePath);
			Category category = categoryService.getOrCreateCategory(categoryName);

			int addedCount = 0;

			for (Word word : wordsArray) {
				if (processSingleWord(word, category)) {
					addedCount++;
				}

				WordDTO wordDTO = new WordDTO();
				wordDTO.setWord(word.getWord());
				wordDTO.setCategory(categoryName);
				wordDTO.setTranslate(word.getTranslation());
				wordDTO.setDifficulty(word.getDifficulty());
				wordDTO.setSourceLanguage(LanguageConstants.ENGLISH);
				wordDTO.setTargetLanguage(LanguageConstants.HEBREW);

			}

			int existingCount = wordsArray.length - addedCount;
			System.out
					.println("Category: " + categoryName + " - Added: " + addedCount + ", Existing: " + existingCount);

		} catch (IOException e) {
			System.err.println("Error loading words from file " + resourcePath + ": " + e.getMessage());
		}
	}

	/**
	 * Loads an array of Word objects from a JSON file.
	 * 
	 * @param resourcePath The classpath path to the JSON file containing the words.
	 * @return An array of Word objects parsed from the JSON file.
	 * @throws IOException If the file is not found or an error occurs during
	 *                     reading.
	 */
	private Word[] loadWordsFromFile(String resourcePath) throws IOException {
		Resource resource = resourceLoader.getResource(resourcePath);
		if (!resource.exists()) {
			throw new IOException("File not found: " + resourcePath);
		}
		return objectMapper.readValue(resource.getInputStream(), Word[].class);
	}

	/**
	 * Processes a single Word object by setting its category, checking for existing
	 * words, translating if necessary, and saving it to the database.
	 * 
	 * @param word     The Word object to process.
	 * @param category The Category object to which the word belongs.
	 * @return true if the word was successfully processed and saved, false if it
	 *         already existed.
	 */
	private boolean processSingleWord(Word word, Category category) {
		word.setCategory(category);
		Optional<Word> existingWord = wordService.findByWord(word.getWord());

		if (existingWord.isPresent()) {
			return false;
		}

		if (word.getTranslation() == null || word.getTranslation().isBlank()) {
			String translated = translationService.translateText(word.getWord(), LanguageConstants.ENGLISH,
					LanguageConstants.HEBREW);
			word.setTranslation(translated);
		}

		wordService.saveWord(word, LanguageConstants.ENGLISH, LanguageConstants.HEBREW);
		return true;
	}
}