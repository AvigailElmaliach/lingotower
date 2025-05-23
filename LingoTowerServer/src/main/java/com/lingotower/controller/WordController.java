package com.lingotower.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingotower.dto.translation.TranslationResponseDTO;
import com.lingotower.dto.word.WordByCategory;
import com.lingotower.dto.word.WordDTO;
import com.lingotower.model.Category;
import com.lingotower.model.Difficulty;
import com.lingotower.model.Role;
import com.lingotower.model.User;
import com.lingotower.model.BaseUser;
import com.lingotower.model.Word;
import com.lingotower.service.CategoryService;
import com.lingotower.service.TranslationService;
import com.lingotower.service.UserService;
import com.lingotower.service.WordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/words")
public class WordController {
	private final WordService wordService;
	private final CategoryService categoryService;
	private TranslationService translationService;
	private final UserService userService;
	private static final int DEFAULT_RANDOM_WORD_COUNT = 10;

	@Autowired
	private ObjectMapper objectMapper;

	public WordController(WordService wordService, CategoryService categoryService,
			TranslationService translationService, UserService userService) {
		this.wordService = wordService;
		this.categoryService = categoryService;
		this.translationService = translationService;
		this.userService = userService;
	}

	@GetMapping
	public ResponseEntity<List<Word>> getAllWords() {
		return ResponseEntity.ok(wordService.getAllWords());
	}

	@PutMapping("/{wordId}")
	public ResponseEntity<Void> updateWord(@PathVariable Long wordId, @RequestBody WordByCategory updateDTO,
			Principal principal) {

		String username = principal.getName();

		if (updateDTO.getId() != null && !updateDTO.getId().equals(wordId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mismatch between path ID and body ID");
		}

		wordService.updateWord(wordId, updateDTO, username);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/category/{categoryId}/random")
	public ResponseEntity<List<WordByCategory>> getRandomWordsByCategory(@PathVariable Long categoryId,
			Principal principal) {

		User user = userService.getUserByUsername(principal.getName());
		if (user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String userLanguage = user.getTargetLanguage();

		List<WordByCategory> randomWords = wordService.getRandomWordsByCategory(categoryId, userLanguage);

		return randomWords.isEmpty() ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
				: ResponseEntity.ok(randomWords);
	}

	@GetMapping("/difficulty/{difficulty}/random")
	public ResponseEntity<List<WordByCategory>> getRandomWordsByDifficulty(@PathVariable Difficulty difficulty,
			Principal principal) {

		User user = userService.getUserByUsername(principal.getName());
		if (user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String userLanguage = user.getTargetLanguage();

		List<WordByCategory> randomWords = wordService.getRandomWordsByDifficulty(difficulty, userLanguage);

		return randomWords.isEmpty() ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
				: ResponseEntity.ok(randomWords);
	}

	@GetMapping("/random")
	public ResponseEntity<List<WordByCategory>> getRandomTranslatedWordsForAllCategoriesAndDifficulties(
			Principal principal) {

		User user = userService.getUserByUsername(principal.getName());
		if (user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String userLanguage = user.getTargetLanguage();

		List<WordByCategory> randomWords = wordService
				.getRandomTranslatedWordsForAllCategoriesAndDifficulties(userLanguage);

		return randomWords.isEmpty() ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
				: ResponseEntity.ok(randomWords);
	}

	@GetMapping("/category/{categoryId}/translate")
	public ResponseEntity<List<WordByCategory>> getTranslatedWordsByCategory(@PathVariable Long categoryId,
			Principal principal) {

		String username = principal.getName();

		List<WordByCategory> translatedWords = wordService.getTranslatedWordsByCategory(categoryId, username);

		return translatedWords.isEmpty() ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
				: ResponseEntity.ok(translatedWords);
	}

	@GetMapping("/category/{categoryId}/difficulty/{difficulty}/translate")
	public ResponseEntity<List<WordByCategory>> getTranslatedWordsByCategoryAndDifficulty(@PathVariable Long categoryId,
			@PathVariable Difficulty difficulty, Principal principal) {
		User user = userService.getUserByUsername(principal.getName());
		if (user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String userLanguage = user.getTargetLanguage();

		List<WordByCategory> translatedWords = wordService.getTranslatedWordsByCategoryAndDifficulty(categoryId,
				difficulty, userLanguage);

		return ResponseEntity.ok(translatedWords);
	}

	@GetMapping("/category/{categoryId}/difficulty/{difficulty}/random/translate")
	public ResponseEntity<List<WordByCategory>> getRandomTranslatedWordsByCategoryAndDifficulty(
			@PathVariable Long categoryId, @PathVariable Difficulty difficulty, Principal principal) {

		User user = userService.getUserByUsername(principal.getName());
		if (user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		String userLanguage = user.getTargetLanguage();

		List<WordByCategory> translatedWords = wordService.getRandomTranslatedWordsByCategoryAndDifficulty(categoryId,
				difficulty, userLanguage);

		return ResponseEntity.ok(translatedWords);
	}

	@GetMapping("/{id}/translate")
	public ResponseEntity<WordByCategory> getTranslatedWordById(@PathVariable Long id,
			@RequestParam String targetLang) {
		try {
			WordByCategory translatedWord = wordService.getTranslatedWordById(id, targetLang);
			return ResponseEntity.ok(translatedWord);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@DeleteMapping("/deleteAll")
	public ResponseEntity<String> deleteAllWords() {
		try {
			wordService.deleteAllWords();
			return ResponseEntity.ok("כל המילים נמחקו בהצלחה");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("שגיאה במחיקת המילים: " + e.getMessage());
		}
	}

	@PostMapping("/upload")
	public ResponseEntity<String> uploadWords(@RequestParam("category") String categoryName,
			@RequestParam("file") MultipartFile file) {
		try {
			System.out.println("Category: " + categoryName);
			System.out.println("File name: " + file.getOriginalFilename());

			Category category = categoryService.getOrCreateCategory(categoryName);
			List<Word> words = file.getOriginalFilename().endsWith(".csv") ? parseCsv(file, category)
					: parseJson(file, category);

			wordService.saveWords(words);
			return ResponseEntity.ok("Words added successfully");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("Error: " + e.getMessage());
		}
	}

	private List<Word> parseJson(MultipartFile file, Category category) throws Exception {
		Word[] wordsArray = objectMapper.readValue(file.getInputStream(), Word[].class);
		for (Word word : wordsArray) {
			word.setCategory(category);
			word.setTranslation(translationService.translateText(word.getWord(), word.getSourceLanguage(),
					word.getTargetLanguage()));
		}
		return List.of(wordsArray);
	}

	private List<Word> parseCsv(MultipartFile file, Category category) throws Exception {
		List<Word> words = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			String[] parts = line.split(",");
			if (parts.length < 4)
				continue;

			String wordText = parts[0].trim();
			String sourceLang = parts[1].trim();
			String targetLang = parts[2].trim();
			Difficulty difficulty = Difficulty.valueOf(parts[3].trim().toUpperCase());
			String translation = translationService.translateText(wordText, sourceLang, targetLang);

			words.add(new Word(null, wordText, translation, sourceLang, targetLang, difficulty, category));
		}
		return words;
	}

	@GetMapping("/daily")
	public ResponseEntity<WordByCategory> getDailyWord(Principal principal) {
		String username = principal.getName();
		WordByCategory word = wordService.getDailyWord(username);
		return ResponseEntity.ok(word);
	}

}
