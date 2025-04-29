package com.lingotower.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lingotower.constants.LanguageConstants;
import com.lingotower.data.AdminRepository;
import com.lingotower.data.CategoryRepository;
import com.lingotower.data.UserRepository;
import com.lingotower.data.WordRepository;
import com.lingotower.dto.translation.TranslationRequestDTO;
import com.lingotower.dto.translation.TranslationResponseDTO;
import com.lingotower.dto.word.WordByCategory;
import com.lingotower.dto.word.WordDTO;
import com.lingotower.model.Admin;
import com.lingotower.model.Category;
import com.lingotower.model.Difficulty;
import com.lingotower.model.User;
import com.lingotower.model.Word;
import com.lingotower.util.TranslationUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class WordService {
	private final WordRepository wordRepository;
	private final CategoryRepository categoryRepository;
	private final TranslationService translationService;
	private final UserRepository userRepository;
	private final AdminRepository adminRepository;
	private static final int DEFAULT_RANDOM_WORDS_LIMIT = 10;
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	public WordService(WordRepository wordRepository, TranslationService translationService,
			CategoryRepository categoryRepository, UserRepository userRepository, AdminRepository adminRepository) {
		this.wordRepository = wordRepository;
		this.translationService = translationService;
		this.categoryRepository = categoryRepository;
		this.userRepository = userRepository;
		this.adminRepository = adminRepository;
	}

	/**
	 * Saves a list of words to the database.
	 * 
	 * @param words The list of Word objects to save.
	 */
	public void saveWords(List<Word> words) {
		try {
			wordRepository.saveAll(words);
		} catch (Exception e) {
			throw new RuntimeException("Error saving words", e);
		}
	}

	/**
	 * Retrieves all words from the database.
	 * 
	 * @return A list of all Word objects.
	 */
	public List<Word> getAllWords() {
		return wordRepository.findAll();
	}

	/**
	 * Deletes all words from the database and clears the entity manager.
	 */
	public void deleteAllWords() {
		try {
			wordRepository.deleteAll();
			entityManager.clear();
		} catch (Exception e) {
			throw new RuntimeException("Error deleting all words", e);
		}
	}

	/**
	 * Saves a word to the database, translating it if the translation is missing.
	 * 
	 * @param word       The Word object to save.
	 * @param sourceLang The source language code for translation.
	 * @param targetLang The target language code for translation.
	 * @return The saved Word object.
	 */
	public Word saveWord(Word word, String sourceLang, String targetLang) {
		try {
			if (word.getTranslation() == null || word.getTranslation().isEmpty()) {
				String translatedText = translationService.translateText(word.getWord(), sourceLang, targetLang);
				if (translatedText != null && !translatedText.isEmpty()) {
					word.setTranslation(translatedText);
				} else {
					System.out.println("Translation not found for this word:" + word.getWord());
				}
			}
			return wordRepository.save(word);
		} catch (Exception e) {
			throw new RuntimeException("Error saving word: " + word.getWord(), e);
		}
	}

	/**
	 * Finds all words in the database that have a null translation.
	 * 
	 * @return A list of Word objects without translation.
	 */
	public List<Word> findWordsWithoutTranslation() {
		return wordRepository.findByTranslationIsNull();
	}

	/**
	 * Retrieves a list of random translated words for a specific category and
	 * difficulty.
	 * 
	 * @param categoryId   The ID of the category.
	 * @param difficulty   The difficulty level.
	 * @param userLanguage The target language for translation display.
	 * @return A list of WordByCategory objects.
	 */
	public List<WordByCategory> getRandomTranslatedWordsByCategoryAndDifficulty(Long categoryId, Difficulty difficulty,
			String userLanguage) {
		List<Word> words = wordRepository.findByCategoryIdAndDifficulty(categoryId, difficulty);
		List<Word> randomWords = getRandomWords(words, 10);
		return mapWordsToLanguage(randomWords, userLanguage);
	}

	/**
	 * Retrieves a list of random words for a specific category.
	 * 
	 * @param categoryId   The ID of the category.
	 * @param userLanguage The target language for translation display.
	 * @return A list of WordByCategory objects.
	 */
	public List<WordByCategory> getRandomWordsByCategory(Long categoryId, String userLanguage) {
		List<Word> words = wordRepository.findByCategoryId(categoryId);
		List<Word> randomWords = getRandomWords(words, 10);
		return mapWordsToLanguage(randomWords, userLanguage);
	}

	/**
	 * Retrieves a list of random words for a specific difficulty level.
	 * 
	 * @param difficulty   The difficulty level.
	 * @param userLanguage The target language for translation display.
	 * @return A list of WordByCategory objects.
	 */
	public List<WordByCategory> getRandomWordsByDifficulty(Difficulty difficulty, String userLanguage) {
		List<Word> words = wordRepository.findByDifficulty(difficulty);
		List<Word> randomWords = getRandomWords(words,DEFAULT_RANDOM_WORDS_LIMIT);
		return mapWordsToLanguage(randomWords, userLanguage);
	}

	/**
	 * Retrieves a list of random translated words from all categories and
	 * difficulties.
	 * 
	 * @param userLanguage The target language for translation display.
	 * @return A list of WordByCategory objects.
	 */
	public List<WordByCategory> getRandomTranslatedWordsForAllCategoriesAndDifficulties(String userLanguage) {
		List<Word> words = wordRepository.findAll();
		List<Word> randomWords = getRandomWords(words,DEFAULT_RANDOM_WORDS_LIMIT);
		return mapWordsToLanguage(randomWords, userLanguage);
	}

	/**
	 * Helper function to get a random sublist of words.
	 * 
	 * @param words The list of Word objects.
	 * @param limit The maximum number of random words to retrieve.
	 * @return A list of random Word objects.
	 */
	private List<Word> getRandomWords(List<Word> words, int limit) {
		if (words.isEmpty()) {
			return Collections.emptyList();
		}
		Random random = new Random();
		Collections.shuffle(words, random);
		return words.stream().limit(limit).collect(Collectors.toList());
	}

	/**
	 * Finds a word by its text.
	 * 
	 * @param wordText The text of the word to find.
	 * @return An Optional containing the Word object if found, otherwise an empty
	 *         Optional.
	 */
	public Optional<Word> findByWord(String wordText) {
		return wordRepository.findByWord(wordText);
	}

	/**
	 * Adds a new word with its translation to the database.
	 * 
	 * @param wordDTO    A DTO containing the word details.
	 * @param targetLang The target language for translation.
	 */
	public void addWordWithTranslation(WordDTO wordDTO, String targetLang) {
		try {
			final String translatedText = translationService.translateText(wordDTO.getWord(),
					wordDTO.getSourceLanguage(), targetLang);

			Category category = categoryRepository.findByName(wordDTO.getCategory()).orElseGet(() -> {
				Category newCategory = new Category(wordDTO.getCategory());
				return categoryRepository.save(newCategory);
			});

			Word word = new Word(wordDTO.getWord(), translatedText, wordDTO.getSourceLanguage());
			word.setCategory(category);
			word.setDifficulty(wordDTO.getDifficulty());

			wordRepository.save(word);
		} catch (Exception e) {
			throw new RuntimeException("Error adding word with translation: " + wordDTO.getWord(), e);
		}
	}

	/**
	 * Retrieves a translated word by its ID for a specific user language.
	 * 
	 * @param id           The ID of the word.
	 * @param userLanguage The target language for translation display.
	 * @return A WordByCategory object representing the translated word.
	 */
	public WordByCategory getTranslatedWordById(Long id, String userLanguage) {
		Word word = wordRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Word not found with ID: " + id));
		String translation = LanguageConstants.HEBREW.equals(userLanguage) ? word.getTranslation() : word.getWord();
		return new WordByCategory(word.getId(), word.getWord(), translation, word.getCategory(), word.getDifficulty());
	}

	/**
	 * Finds a word by its text and source language.
	 * 
	 * @param word           The text of the word to find.
	 * @param sourceLanguage The source language of the word.
	 * @return An Optional containing the Word object if found, otherwise an empty
	 *         Optional.
	 */
	public Optional<Word> getWordByWordAndSourceLanguage(String word, String sourceLanguage) {
		return wordRepository.findByWordAndSourceLanguage(word, sourceLanguage);
	}

	/**
	 * Maps a list of Word objects to a list of WordByCategory objects, adjusting
	 * the word and translation based on the user's language.
	 * 
	 * @param words        The list of Word objects to map.
	 * @param userLanguage The user's target language.
	 * @return A list of WordByCategory objects.
	 */
	public List<WordByCategory> mapWordsToLanguage(List<Word> words, String userLanguage) {
		return words.stream().map(word -> {
			if (LanguageConstants.HEBREW.equals(userLanguage))
				return new WordByCategory(word.getId(), word.getTranslation(), word.getWord(), word.getCategory(),
						word.getDifficulty());
			return new WordByCategory(word.getId(), word.getWord(), word.getTranslation(), word.getCategory(),
					word.getDifficulty());
		}).collect(Collectors.toList());
	}

	/**
	 * Retrieves a list of translated words for a specific category for a given
	 * user.
	 * 
	 * @param categoryId The ID of the category.
	 * @param username   The username of the user to determine the target language.
	 * @return A list of WordByCategory objects.
	 */
	public List<WordByCategory> getTranslatedWordsByCategory(Long categoryId, String username) {
		List<Word> words = wordRepository.findByCategoryId(categoryId);
		if (words.isEmpty()) {
			return Collections.emptyList();
		}
		String userLanguage = getUserLanguage(username);
		return mapWordsToLanguage(words, userLanguage);
	}

	/**
	 * Retrieves the target language of a user or admin.
	 * 
	 * @param username The username of the user or admin.
	 * @return The target language code of the user or admin.
	 * @throws UsernameNotFoundException if the username is not found for either a
	 *                                   user or an admin.
	 */
	public String getUserLanguage(String username) {
		Optional<User> user = userRepository.findByUsername(username);
		if (user.isPresent()) {
			return user.get().getTargetLanguage();
		}
		Optional<Admin> admin = adminRepository.findByUsername(username);
		if (admin.isPresent()) {
			return admin.get().getTargetLanguage();
		}
		throw new UsernameNotFoundException("User not found: " + username);
	}

	/**
	 * Retrieves a list of translated words for a specific category and difficulty
	 * for a given user.
	 * 
	 * @param categoryId   The ID of the category.
	 * @param difficulty   The difficulty level.
	 * @param userLanguage The target language for translation display.
	 * @return A list of WordByCategory objects.
	 */
	public List<WordByCategory> getTranslatedWordsByCategoryAndDifficulty(Long categoryId, Difficulty difficulty,
			String userLanguage) {
		List<Word> words = wordRepository.findByCategoryIdAndDifficulty(categoryId, difficulty);
		if (words.isEmpty()) {
			return Collections.emptyList();
		}
		return mapWordsToLanguage(words, userLanguage);
	}

	/**
	 * Deletes a word by its ID, accessible only to admins.
	 * 
	 * @param wordId   The ID of the word to delete.
	 * @param username The username of the admin performing the deletion.
	 * @throws ResponseStatusException if the word is not found (HTTP 404).
	 */
	@Transactional
	public void deleteWord(Long wordId, String username) {
		// In a real application, you might want to check if the user has admin roles
		// here.
		if (!wordRepository.existsById(wordId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Word not found with ID: " + wordId);
		}
		try {
			wordRepository.deleteById(wordId);
		} catch (Exception e) {
			throw new RuntimeException("Error deleting word with ID: " + wordId, e);
		}
	}

	/**
	 * Updates an existing word with new information, accessible only to admins.
	 * 
	 * @param wordId    The ID of the word to update.
	 * @param updateDTO A DTO containing the updated word information.
	 * @param username  The username of the admin performing the update.
	 * @throws ResponseStatusException if the word or category is not found (HTTP
	 *                                 404 or 400).
	 */
	public void updateWord(Long wordId, WordByCategory updateDTO, String username) {
		// In a real application, you might want to check if the user has admin roles
		// here.
		Word word = wordRepository.findById(wordId).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Word not found with ID: " + wordId));
		word.setWord(updateDTO.getWord());
		word.setDifficulty(updateDTO.getDifficulty());
		word.setTranslation(updateDTO.getTranslatedText());
		Category category = categoryRepository.findByName(updateDTO.getCategory().getName())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Category not found: " + updateDTO.getCategory().getName()));
		word.setCategory(category);
		try {
			wordRepository.save(word);
		} catch (Exception e) {
			throw new RuntimeException("Error updating word with ID: " + wordId, e);
		}
	}

	/**
	 * Retrieves the daily word for a user based on their username and the current
	 * date.
	 * 
	 * @param username The username of the user.
	 * @return A WordByCategory object representing the daily word.
	 * @throws ResponseStatusException if no words are available (HTTP 404).
	 */
	public WordByCategory getDailyWord(String username) {
		String userLanguage = getUserLanguage(username);
		List<Word> allWords = wordRepository.findAll();
		if (allWords.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No words available in the system.");
		}
		String uniqueSeed = username + LocalDate.now().toString();
		int hash = Math.abs(uniqueSeed.hashCode());
		int index = hash % allWords.size();
		Word selectedWord = allWords.get(index);
		if (LanguageConstants.HEBREW.equals(userLanguage)) {
			return new WordByCategory(selectedWord.getId(), selectedWord.getTranslation(), selectedWord.getWord(),
					selectedWord.getCategory(), selectedWord.getDifficulty());
		} else {
			return new WordByCategory(selectedWord.getId(), selectedWord.getWord(), selectedWord.getTranslation(),
					selectedWord.getCategory(), selectedWord.getDifficulty());
		}
	}
}