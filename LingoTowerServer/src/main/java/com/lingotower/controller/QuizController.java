package com.lingotower.controller;

import com.lingotower.dto.quiz.QuestionDTO;
import com.lingotower.model.Difficulty;
import com.lingotower.model.Quiz;
import com.lingotower.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/quizzes")
public class QuizController {

	private final QuizService quizService;

	/**
	 * Constructor for the QuizController, injecting the required service.
	 * 
	 * @param quizService The service layer for quiz-related operations.
	 */
	public QuizController(QuizService quizService) {
		this.quizService = quizService;
	}

	/**
	 * Retrieves a list of all quizzes.
	 * 
	 * @return A list of all Quiz objects.
	 */
	@GetMapping
	public List<Quiz> getAllQuizzes() {
		return quizService.getAllQuizzes();
	}

	/**
	 * Retrieves a specific quiz by its ID.
	 * 
	 * @param id The ID of the quiz to retrieve.
	 * @return ResponseEntity containing the Quiz object and HTTP status OK if
	 *         found, or HTTP status NOT FOUND if the quiz does not exist.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Quiz> getQuizById(@PathVariable Long id) {
		Optional<Quiz> quiz = quizService.getQuizById(id);
		return quiz.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	/**
	 * Creates a new quiz.
	 * 
	 * @param quiz The Quiz object to create.
	 * @return ResponseEntity containing the newly created Quiz object and HTTP
	 *         status OK.
	 */
	@PostMapping
	public ResponseEntity<Quiz> createQuiz(@RequestBody Quiz quiz) {
		Quiz newQuiz = quizService.createQuiz(quiz);
		return ResponseEntity.ok(newQuiz);
	}

	/**
	 * Updates an existing quiz.
	 * 
	 * @param id          The ID of the quiz to update.
	 * @param updatedQuiz The Quiz object containing the updated information.
	 * @return ResponseEntity containing the updated Quiz object and HTTP status OK
	 *         if successful, or HTTP status NOT FOUND if the quiz does not exist.
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Quiz> updateQuiz(@PathVariable Long id, @RequestBody Quiz updatedQuiz) {
		Optional<Quiz> quiz = quizService.updateQuiz(id, updatedQuiz);
		return quiz.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	/**
	 * Deletes a quiz by its ID.
	 * 
	 * @param id The ID of the quiz to delete.
	 * @return ResponseEntity with HTTP status NO CONTENT upon successful deletion.
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
		quizService.deleteQuiz(id);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Generates a quiz for the authenticated user based on the specified category
	 * and difficulty. Allows specifying the number of questions in the quiz.
	 * 
	 * @param categoryId        The ID of the category for the quiz.
	 * @param difficulty        The difficulty level of the quiz questions.
	 * @param numberOfQuestions An optional parameter specifying the number of
	 *                          questions to generate for the quiz. If not provided,
	 *                          a default number of questions might be used by the
	 *                          service.
	 * @param principal         The Principal object representing the currently
	 *                          logged-in user.
	 * @return ResponseEntity containing a list of QuestionDTO representing the
	 *         generated quiz and HTTP status OK.
	 */
	@GetMapping("/generate")
	public ResponseEntity<List<QuestionDTO>> generateQuiz(@RequestParam Long categoryId,
			@RequestParam Difficulty difficulty, @RequestParam(required = false) Integer numberOfQuestions,
			Principal principal) {
		String username = principal.getName();
		List<QuestionDTO> quiz = quizService.generateQuiz(categoryId, difficulty, username, numberOfQuestions);
		return ResponseEntity.ok(quiz);
	}
}