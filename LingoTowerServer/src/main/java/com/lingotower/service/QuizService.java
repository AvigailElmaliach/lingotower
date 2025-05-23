package com.lingotower.service;

import com.lingotower.model.Category;
import com.lingotower.model.Difficulty;
import com.lingotower.model.Question;
import com.lingotower.model.Quiz;
import com.lingotower.model.Word;
import com.lingotower.data.AdminRepository;
import com.lingotower.data.QuizRepository;
import com.lingotower.data.UserRepository;
import com.lingotower.data.WordRepository;
import com.lingotower.dto.quiz.QuestionDTO;
import com.lingotower.dto.word.WordByCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuizService {
	private static final int DEFAULT_QUIZ_SIZE = 10;
	private static final int NUMBER_OF_WRONG_OPTIONS = 4;
	private static final int NUMBER_OF_QUIZ_OPTIONS = 5;

	@Autowired
	private QuizRepository quizRepository;
	private final WordRepository wordRepository;
	private final UserRepository userRepository;
	private final AdminRepository adminRepository;
	private final WordService wordService;

	public QuizService(WordRepository wordRepository, UserRepository userRepository, AdminRepository adminRepository,
			WordService wordService) {
		this.wordRepository = wordRepository;
		this.userRepository = userRepository;
		this.adminRepository = adminRepository;
		this.wordService = wordService;
	}

	public List<Quiz> getAllQuizzes() {
		return quizRepository.findAll();
	}

	public Optional<Quiz> getQuizById(Long id) {
		return quizRepository.findById(id);
	}

	public Quiz createQuiz(Quiz quiz) {
		return quizRepository.save(quiz);
	}

	public void deleteQuiz(Long id) {
		quizRepository.deleteById(id);
	}

	public Optional<Quiz> updateQuiz(Long id, Quiz updatedQuiz) {
		return quizRepository.findById(id).map(existingQuiz -> {
			existingQuiz.setName(updatedQuiz.getName());
			existingQuiz.setCategory(updatedQuiz.getCategory());
			existingQuiz.setDifficulty(updatedQuiz.getDifficulty());
			existingQuiz.setAdminByCreated(updatedQuiz.getAdminByCreated());
			return quizRepository.save(existingQuiz);
		});
	}

	/**
	 * Generates a list of quiz questions. It selects random words based on category
	 * and difficulty, retrieves wrong options, and creates QuestionDTO objects.
	 * 
	 * @param categoryId        The ID of the category for the quiz.
	 * @param difficulty        The difficulty level for the quiz.
	 * @param username          The username of the user to determine the target
	 *                          language.
	 * @param numberOfQuestions The desired number of questions in the quiz.
	 * @return A list of QuestionDTO objects.
	 */
	public List<QuestionDTO> generateQuiz(Long categoryId, Difficulty difficulty, String username,
			Integer numberOfQuestions) {
		int numQuestions = (numberOfQuestions != null) ? numberOfQuestions : DEFAULT_QUIZ_SIZE;
		String userLanguage = wordService.getUserLanguage(username);
		List<WordByCategory> selectedWords = wordService.getRandomTranslatedWordsByCategoryAndDifficulty(categoryId,
				difficulty, userLanguage);
		List<WordByCategory> allWords = wordService.getTranslatedWordsByCategoryAndDifficulty(categoryId, difficulty,
				userLanguage);

		List<QuestionDTO> questions = new ArrayList<>();

		for (WordByCategory correctWord : selectedWords) {
			List<String> wrongOptions = allWords.stream()
					.filter(w -> !w.getTranslatedText().equals(correctWord.getTranslatedText()))
					.map(WordByCategory::getTranslatedText).distinct().limit(NUMBER_OF_WRONG_OPTIONS)
					.collect(Collectors.toList());

			List<String> options = new ArrayList<>(wrongOptions);
			options.add(correctWord.getTranslatedText());
			Collections.shuffle(options);

			List<String> finalOptions = options.stream().limit(NUMBER_OF_QUIZ_OPTIONS).collect(Collectors.toList());

			QuestionDTO question = new QuestionDTO(correctWord.getId(), correctWord.getWord(), finalOptions,
					correctWord.getTranslatedText(), correctWord.getCategory());
			questions.add(question);
		}

		return questions;
	}

	private List<String> getWrongOptions(List<WordByCategory> allWords, WordByCategory correctWord) {
		return allWords.stream().filter(w -> !w.getTranslatedText().equals(correctWord.getTranslatedText()))
				.map(WordByCategory::getTranslatedText).distinct().limit(NUMBER_OF_WRONG_OPTIONS)
				.collect(Collectors.toList());
	}

	private QuestionDTO createQuestion(WordByCategory correctWord, List<String> options) {
		return new QuestionDTO(correctWord.getId(), correctWord.getWord(), options, correctWord.getTranslatedText(),
				correctWord.getCategory());
	}
}