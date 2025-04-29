package com.lingotower.service;

import com.lingotower.data.QuestionRepository;
import com.lingotower.exception.ServiceOperationException;
import com.lingotower.model.Question;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

	private final QuestionRepository questionRepository;

	public QuestionService(QuestionRepository questionRepository) {
		this.questionRepository = questionRepository;
	}

	/**
	 * Retrieves all questions from the database.
	 * 
	 * @return A list of all Question objects.
	 */
	public List<Question> getAllQuestions() {
		return questionRepository.findAll();
	}

	/**
	 * Retrieves a question by its ID.
	 * 
	 * @param id The ID of the question to retrieve.
	 * @return An Optional containing the Question object if found, otherwise an
	 *         empty Optional.
	 * @throws ServiceOperationException if an error occurs during retrieval.
	 */
	public Optional<Question> getQuestionById(Long id) {
		try {
			return questionRepository.findById(id);
		} catch (Exception e) {
			throw new ServiceOperationException("Error retrieving question with ID: " + id, e);
		}
	}

	/**
	 * Retrieves all questions associated with a specific quiz ID.
	 * 
	 * @param quizId The ID of the quiz.
	 * @return A list of Question objects associated with the given quiz ID.
	 * @throws ServiceOperationException if an error occurs during retrieval.
	 */
	public List<Question> getQuestionsByQuizId(Long quizId) {
		try {
			return questionRepository.findByQuizId(quizId);
		} catch (Exception e) {
			throw new ServiceOperationException("Error retrieving questions for quiz ID: " + quizId, e);
		}
	}

	/**
	 * Adds a new question to the database.
	 * 
	 * @param question The Question object to add.
	 * @return The saved Question object.
	 * @throws ServiceOperationException if an error occurs during saving.
	 */
	public Question addQuestion(Question question) {
		try {
			return questionRepository.save(question);
		} catch (Exception e) {
			throw new ServiceOperationException("Error adding new question.", e);
		}
	}

	/**
	 * Deletes a question from the database by its ID.
	 * 
	 * @param id The ID of the question to delete.
	 * @throws ServiceOperationException if an error occurs during deletion or if
	 *                                   the question is not found.
	 */
	public void deleteQuestion(Long id) {
		try {
			if (!questionRepository.existsById(id)) {
				throw new ServiceOperationException("Question with ID: " + id + " not found, cannot delete.");
			}
			questionRepository.deleteById(id);
		} catch (Exception e) {
			throw new ServiceOperationException("Error deleting question with ID: " + id, e);
		}
	}
}