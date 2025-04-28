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

	public void saveWords(List<Word> words) {
		wordRepository.saveAll(words);
	}

	public List<Word> getAllWords() {
		return wordRepository.findAll();
	}

	public void deleteAllWords() {
		wordRepository.deleteAll();
		entityManager.clear();
	}

	public Word saveWord(Word word, String sourceLang, String targetLang) {
		if (word.getTranslation() == null || word.getTranslation().isEmpty()) {
			String translatedText = translationService.translateText(word.getWord(), sourceLang, targetLang);
			if (translatedText != null && !translatedText.isEmpty()) {
				word.setTranslation(translatedText);
			} else {
				System.out.println("Translation not found for this word:" + word.getWord());
			}
		}
		return wordRepository.save(word);
	}

	public List<Word> findWordsWithoutTranslation() {
		return wordRepository.findByTranslationIsNull();
	}

	public List<WordByCategory> getRandomTranslatedWordsByCategoryAndDifficulty(Long categoryId, Difficulty difficulty,
			String userLanguage) {
		List<Word> words = wordRepository.findByCategoryIdAndDifficulty(categoryId, difficulty);
		List<Word> randomWords = getRandomWords(words, 10);
		return mapWordsToLanguage(randomWords, userLanguage);
	}

	public List<WordByCategory> getRandomWordsByCategory(Long categoryId, String userLanguage) {
		List<Word> words = wordRepository.findByCategoryId(categoryId);
		List<Word> randomWords = getRandomWords(words, 10);
		return mapWordsToLanguage(randomWords, userLanguage);
	}

	public List<WordByCategory> getRandomWordsByDifficulty(Difficulty difficulty, String userLanguage) {
		List<Word> words = wordRepository.findByDifficulty(difficulty);
		List<Word> randomWords = getRandomWords(words, 10);
		return mapWordsToLanguage(randomWords, userLanguage);
	}

	public List<WordByCategory> getRandomTranslatedWordsForAllCategoriesAndDifficulties(String userLanguage) {
		List<Word> words = wordRepository.findAll();
		List<Word> randomWords = getRandomWords(words, 10);
		return mapWordsToLanguage(randomWords, userLanguage);
	}

	private List<Word> getRandomWords(List<Word> words, int limit) {
		if (words.isEmpty()) {
			return Collections.emptyList();
		}

		Random random = new Random();
		Collections.shuffle(words, random);
		return words.stream().limit(limit).collect(Collectors.toList());
	}

	public Optional<Word> findByWord(String wordText) {
		return wordRepository.findByWord(wordText);
	}

	public void addWordWithTranslation(WordDTO wordDTO, String targetLang) {
		final String translatedText = translationService.translateText(wordDTO.getWord(), wordDTO.getSourceLanguage(),
				targetLang);

		Category category = categoryRepository.findByName(wordDTO.getCategory()).orElseGet(() -> {
			Category newCategory = new Category(wordDTO.getCategory());
			return categoryRepository.save(newCategory);
		});

		Word word = new Word(wordDTO.getWord(), translatedText, wordDTO.getSourceLanguage());
		word.setCategory(category);
		word.setDifficulty(wordDTO.getDifficulty());

		wordRepository.save(word);
	}

	public WordByCategory getTranslatedWordById(Long id, String userLanguage) {
		Word word = wordRepository.findById(id).orElseThrow(() -> new RuntimeException("Word not found"));

		String translation = LanguageConstants.HEBREW.equals(userLanguage) ? word.getTranslation() : word.getWord();
		return new WordByCategory(word.getId(), word.getWord(), translation, word.getCategory(), word.getDifficulty());// אפשר
																														// במקום
																														// לקרוא
																														// לפונקצי
																														// //
																														// MAP
	}

	public Optional<Word> getWordByWordAndSourceLanguage(String word, String sourceLanguage) {
		return wordRepository.findByWordAndSourceLanguage(word, sourceLanguage);
	}

	public List<WordByCategory> mapWordsToLanguage(List<Word> words, String userLanguage) {
		return words.stream().map(word -> {
			if (LanguageConstants.HEBREW.equals(userLanguage))
				return new WordByCategory(word.getId(), word.getTranslation(), word.getWord(), word.getCategory(),
						word.getDifficulty());
			return new WordByCategory(word.getId(), word.getWord(), word.getTranslation(), word.getCategory(),
					word.getDifficulty());
		}).collect(Collectors.toList());
	}

	public List<WordByCategory> getTranslatedWordsByCategory(Long categoryId, String username) {
		List<Word> words = wordRepository.findByCategoryId(categoryId);

		if (words.isEmpty()) {
			return Collections.emptyList();
		}

		String userLanguage = getUserLanguage(username);
		return mapWordsToLanguage(words, userLanguage);
	}

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

	public List<WordByCategory> getTranslatedWordsByCategoryAndDifficulty(Long categoryId, Difficulty difficulty,
			String userLanguage) {
		List<Word> words = wordRepository.findByCategoryIdAndDifficulty(categoryId, difficulty);

		if (words.isEmpty()) {
			return Collections.emptyList();
		}

		return mapWordsToLanguage(words, userLanguage);
	}

	@Transactional
	public void deleteWord(Long wordId, String username) {
		if (!wordRepository.existsById(wordId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Word not found");
		}

		wordRepository.deleteById(wordId);
	}

	public void updateWord(Long wordId, WordByCategory updateDTO, String username) {
		Word word = wordRepository.findById(wordId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Word not found"));

		word.setWord(updateDTO.getWord());
		word.setDifficulty(updateDTO.getDifficulty());
		word.setTranslation(updateDTO.getTranslatedText());
		Category category = categoryRepository.findByName(updateDTO.getCategory().getName())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found"));
		word.setCategory(category);
		wordRepository.save(word);
	}

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