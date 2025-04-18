package com.lingotower.service;

import com.lingotower.data.UserRepository;
import com.lingotower.data.WordRepository;
import com.lingotower.dto.translation.TranslationResponseDTO;
import com.lingotower.dto.word.WordByCategory;
import com.lingotower.model.User;
import com.lingotower.model.Word;
import com.lingotower.service.WordService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.lingotower.exception.UserNotFoundException;
import com.lingotower.exception.WordNotFoundException;

//import com.lingotower.exception;
@Service
public class UserService {
	private final UserRepository userRepository;
	private final WordRepository wordRepository;
	@Autowired
	private WordService wordService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	public UserService(UserRepository userRepository, WordRepository wordRepository, WordService wordService,PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.wordRepository = wordRepository;
		this.wordService = wordService;
		this.passwordEncoder=passwordEncoder;
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public Optional<User> getUserById(Long id) {
		return userRepository.findById(id);
	}

	public User saveUser(User user) {
		// אם המשתמש קיים לא עשתי בדיקה
		return userRepository.save(user);
	}

	public void deleteUser(Long id) {
		// כאן  להוסיף בדיקה אם המשתמש קיים לפני המחיקה
		userRepository.deleteById(id);
	}

	public double getLearningProgress(String username) {
		List<Word> learnedWords = userRepository.findLearnedWordsByUsername(username);
		long totalWords = wordRepository.count();
		return totalWords == 0 ? 0 : (learnedWords.size() * 100.0) / totalWords;
	}

//	public double getLearningProgress(Long userId) {
//		List<Word> learnedWords = userRepository.findLearnedWordsByUser(userId);
//		long totalWords = wordRepository.count();
//		return totalWords == 0 ? 0 : (learnedWords.size() * 100.0) / totalWords;
//	}

//	public void addLearnedWord(Long userId, Long wordId) {
//		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
//		Word word = wordRepository.findById(wordId).orElseThrow(() -> new WordNotFoundException("Word not found"));
//
//		if (!user.getLearnedWords().contains(word)) {
//			user.getLearnedWords().add(word);
//			userRepository.save(user);
//		}
//	}

	public User getUserByUsername(String username) {
		return userRepository.findByUsername(username).orElse(null);
	}

	public boolean updateUserLanguages(String username, String sourceLang, String targetLang) {
		Optional<User> userOptional = userRepository.findByUsername(username);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			user.setSourceLanguage(sourceLang);
			user.setTargetLanguage(targetLang);
			userRepository.save(user);
			return true;
		}
		return false;
	}

//	public List<WordByCategory> getLearnedWordsForUser(String username) {
//		User user = getUserByUsername(username);
//		if (user == null) {
//			throw new UsernameNotFoundException("User not found");
//		}
//
//		String targetLanguage = user.getTargetLanguage();
//
//		List<Word> learnedWords = userRepository.findLearnedWordsByUsernameAndTargetLanguage(username, targetLanguage);
//
//		return wordService.mapWordsToLanguage(learnedWords, targetLanguage);
//	}
	@Transactional
	public List<WordByCategory> getLearnedWordsForUser(String username) {
		User user = getUserByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found");
		}

		// שליפת כל המילים שנלמדו (בלי קשר לשפת יעד)
		List<Word> learnedWords = user.getLearnedWords().stream().toList();

		// מחזירים את כולן כמו שהן – עם המילה והתרגום
		return learnedWords.stream().map(word -> new WordByCategory(word.getId(), word.getWord(), word.getTranslation(),
				word.getCategory(), word.getDifficulty())).collect(Collectors.toList());
	}

	@Transactional
	public void addLearnedWord(String username, Long wordId) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		Word word = wordRepository.findById(wordId).orElseThrow(() -> new EntityNotFoundException("Word not found"));
		if (!user.getLearnedWords().contains(word)) {
			user.getLearnedWords().add(word);
			userRepository.save(user);
		}

	}
	public void updatePassword(String username, String newPassword) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}
}