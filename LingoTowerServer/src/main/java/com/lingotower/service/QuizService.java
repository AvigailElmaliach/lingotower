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
//import com.lingotower.dto.quiz.QuizResponseDTO;
import com.lingotower.dto.word.WordByCategory;
import com.lingotower.service.WordService;
import com.lingotower.dto.quiz.QuestionDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.lingotower.controller.QuizController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuizService {
	@Autowired
	private QuizRepository quizRepository;
	private final WordRepository wordRepository;
	private final UserRepository userRepository;
	private final AdminRepository adminRepository;
	private WordService wordService;

	public QuizService(WordRepository wordRepository, UserRepository userRepository, AdminRepository adminRepository,
			WordService wordService) {
		this.wordRepository = wordRepository;
		this.userRepository = userRepository;
		this.adminRepository = adminRepository;
		this.wordService = wordService;
	}

//    public List<Question> generateExam(Category category, Difficulty difficulty, Long userId) {
//        List<Word> words = wordRepository.findByCategoryAndDifficulty(category, difficulty);
//        Collections.shuffle(words);
//        List<Word> selected = words.stream().limit(10).collect(Collectors.toList());
//
//        List<Question> questions = new ArrayList<>();
//
//        for (Word correctWord : selected) {
//            List<String> wrongOptions = words.stream()
//                .filter(w -> !w.getId().equals(correctWord.getId()))
//                .map(Word::getTranslation)
//                .distinct()
//                .limit(4)
//                .collect(Collectors.toList());
//
//            Question q = new Question();
//            q.setQuestionText(correctWord.getWord());
//            q.setCorrectAnswer(correctWord.getTranslation());
//            q.setWrongAnswers(wrongOptions);
//            q.setCategory(correctWord.getCategory());
//            q.setDifficulty(correctWord.getDifficulty());
//            questions.add(q);
//        }
//
//        return questions;
//    }

//    public ExamResponse submitExam(ExamResponse submitted) {
//        int score = 0;
//        List<UserAnswerDTO> answers = submitted.getAnswers();
//
//        for (UserAnswerDTO answer : answers) {
//            Optional<Word> wordOpt = wordRepository.findById(answer.getQuestionId());
//            if (wordOpt.isPresent()) {
//                Word word = wordOpt.get();
//                boolean isCorrect = word.getTranslation().equalsIgnoreCase(answer.getSelectedAnswer());
//                if (isCorrect) {
//                    score += 10;
//                }
//
//                LearnedWord learned = new LearnedWord();
//                learned.setWord(word);
//                learned.setCorrect(isCorrect);
//                learned.setUser(new User(answer.getUserId())); // יצירת יוזר פיקטיבית
//                learnedWordRepository.save(learned);
//            }
//        }

//        submitted.setScore(score);
//        return submitted;
//    }
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

	public List<QuestionDTO> generateQuiz(Long categoryId, Difficulty difficulty, String username) {
		String userLanguage = wordService.getUserLanguage(username);
		List<WordByCategory> selectedWords = wordService.getRandomTranslatedWordsByCategoryAndDifficulty(categoryId,
				difficulty, userLanguage);
		List<WordByCategory> allWords = wordService.getTranslatedWordsByCategoryAndDifficulty(categoryId, difficulty,
				userLanguage);

		List<QuestionDTO> questions = new ArrayList<>();

		for (WordByCategory correctWord : selectedWords) {
			List<String> wrongOptions = getWrongOptions(allWords, correctWord);
			List<String> options = new ArrayList<>(wrongOptions);
			options.add(correctWord.getTranslatedText());

			Collections.shuffle(options);

			QuestionDTO question = createQuestion(correctWord, options);
			questions.add(question);
		}

		return questions;
	}

	private List<String> getWrongOptions(List<WordByCategory> allWords, WordByCategory correctWord) {
		return allWords.stream().filter(w -> !w.getTranslatedText().equals(correctWord.getTranslatedText()))
				.map(WordByCategory::getTranslatedText).distinct().limit(4).collect(Collectors.toList());
	}

	private QuestionDTO createQuestion(WordByCategory correctWord, List<String> options) {
		return new QuestionDTO(correctWord.getId(), correctWord.getWord(), options, correctWord.getTranslatedText(),
				correctWord.getCategory());
	}

//	public List<QuestionDTO> generateQuiz(Long categoryId, Difficulty difficulty, String username) {
//	    String userLanguage = wordService.getUserLanguage(username);
//	    List<WordByCategory> selectedWords = wordService.getRandomTranslatedWordsByCategoryAndDifficulty(categoryId, difficulty, userLanguage);
//	    List<WordByCategory> allWords = wordService.getTranslatedWordsByCategoryAndDifficulty(categoryId, difficulty, userLanguage); // for wrong options
//	    List<QuestionDTO> questions = new ArrayList<>();
//
//	    for (WordByCategory correctWord : selectedWords) {
//	        List<String> wrongOptions = allWords.stream()
//	                .filter(w -> !w.getTranslatedText().equals(correctWord.getTranslatedText()))
//	                .map(WordByCategory::getTranslatedText)
//	                .distinct()
//	                .limit(4)
//	                .collect(Collectors.toList());
//
//	        List<String> options = new ArrayList<>(wrongOptions);
//	        options.add(correctWord.getTranslatedText());
//
//	        Collections.shuffle(options);
//
//	        QuestionDTO question = new QuestionDTO(
//	                correctWord.getId(),
//	                correctWord.getWord(), 
//	                options,               
//	                correctWord.getTranslatedText(),
//	                correctWord.getCategory()
//	        );
//
//	        questions.add(question);
//	    }
//
//	    return questions;
//	}

}
