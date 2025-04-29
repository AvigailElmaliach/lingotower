package com.lingotower.service;

import com.lingotower.data.WordRepository;
import com.lingotower.dto.word.WordByCategory;
import com.lingotower.exception.ServiceOperationException;
import com.lingotower.model.Category;
import com.lingotower.model.Difficulty;
import com.lingotower.model.Question;
import com.lingotower.model.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class QuestionBankService {

	private static final int NUMBER_OF_WRONG_OPTIONS = 4;

	@Autowired
	private WordRepository wordRepository;
	@Autowired
	private WordService wordService;
	@Autowired
	private CategoryService categoryService;

	private final Random random = new Random();

	/**
	 * Retrieves a list of random translated words for a given category and
	 * difficulty.
	 * 
	 * @param categoryName The name of the category.
	 * @param difficulty   The difficulty level.
	 * @param userLanguage The target language for translation.
	 * @param count        The number of words to retrieve.
	 * @return A list of WordByCategory objects.
	 * @throws ServiceOperationException if the category is not found.
	 */
	public List<WordByCategory> getRandomTranslatedWords(String categoryName, Difficulty difficulty,
			String userLanguage, int count) {
		Optional<Category> categoryOptional = categoryService.findByName(categoryName);
		if (categoryOptional.isEmpty()) {
			throw new ServiceOperationException("Category not found: " + categoryName);
		}
		Category category = categoryOptional.get();
		List<Word> allWords = wordRepository.findByCategoryAndDifficulty(category, difficulty);
		if (allWords.isEmpty()) {
			return Collections.emptyList(); // It's acceptable to return an empty list if no words are found.
		}
		Collections.shuffle(allWords, random);
		return wordService.mapWordsToLanguage(allWords.stream().limit(count).collect(Collectors.toList()),
				userLanguage);
	}

	/**
	 * Retrieves a list of wrong options for a quiz question.
	 * 
	 * @param correctWord  The correct word.
	 * @param categoryName The name of the category.
	 * @param difficulty   The difficulty level.
	 * @param count        The number of wrong options to retrieve.
	 * @param userLanguage The target language for translation.
	 * @return A list of wrong answer strings.
	 * @throws ServiceOperationException if the category is not found.
	 */
	public List<String> getWrongOptions(Word correctWord, String categoryName, Difficulty difficulty, int count,
			String userLanguage) {
		Optional<Category> categoryOptional = categoryService.findByName(categoryName);
		if (categoryOptional.isEmpty()) {
			throw new ServiceOperationException("Category not found: " + categoryName);
		}
		Category category = categoryOptional.get();
		return wordRepository.findByCategoryAndDifficulty(category, difficulty).stream()
				.filter(w -> !w.getId().equals(correctWord.getId())).map(w -> {
					List<WordByCategory> translatedWords = wordService.mapWordsToLanguage(Collections.singletonList(w),
							userLanguage);
					return translatedWords.isEmpty() ? w.getWord() : translatedWords.get(0).getTranslatedText();
				}).distinct().limit(NUMBER_OF_WRONG_OPTIONS).collect(Collectors.toList());
	}

	/**
	 * Retrieves a list of wrong completion options.
	 * 
	 * @param categoryName  The name of the category.
	 * @param difficulty    The difficulty level.
	 * @param correctAnswer The correct answer.
	 * @param count         The number of wrong options to retrieve.
	 * @return A list of wrong answer strings.
	 * @throws ServiceOperationException if the category is not found.
	 */
	public List<String> getWrongCompletionOptions(String categoryName, Difficulty difficulty, String correctAnswer,
			int count) {
		Optional<Category> categoryOptional = categoryService.findByName(categoryName);
		if (categoryOptional.isEmpty()) {
			throw new ServiceOperationException("Category not found: " + categoryName);
		}
		Category category = categoryOptional.get();
		return wordRepository.findByCategoryAndDifficulty(category, difficulty).stream()
				.filter(w -> !w.getWord().equalsIgnoreCase(correctAnswer)).map(Word::getWord).distinct()
				.limit(NUMBER_OF_WRONG_OPTIONS).collect(Collectors.toList());
	}

	/**
	 * Creates a quiz question object.
	 * 
	 * @param correctWord  The correct word and its translation.
	 * @param wrongOptions A list of wrong answer options.
	 * @return A Question object.
	 */
	public Question createQuizQuestion(WordByCategory correctWord, List<String> wrongOptions) {
		Question question = new Question();
		question.setQuestionText(correctWord.getWord());
		question.setCorrectAnswer(correctWord.getTranslatedText());
		question.setWrongAnswers(wrongOptions.stream().limit(NUMBER_OF_WRONG_OPTIONS).collect(Collectors.toList()));
		question.setCategory(correctWord.getCategory());
		question.setDifficulty(correctWord.getDifficulty());
		return question;
	}

	/**
	 * Creates a completion question object.
	 * 
	 * @param questionText  The text of the question with the blank.
	 * @param correctAnswer The correct answer.
	 * @param wrongAnswers  A list of wrong answer options.
	 * @param category      The category of the question.
	 * @param difficulty    The difficulty level of the question.
	 * @return A Question object.
	 */
	public Question createCompletionQuestion(String questionText, String correctAnswer, List<String> wrongAnswers,
			Category category, Difficulty difficulty) {
		Question question = new Question();
		question.setQuestionText(questionText);
		question.setCorrectAnswer(correctAnswer);
		question.setWrongAnswers(wrongAnswers);
		question.setCategory(category);
		question.setDifficulty(difficulty);
		return question;
	}
}