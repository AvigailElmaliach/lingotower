package com.lingotower.service;

import com.lingotower.data.UserRepository;
import com.lingotower.data.WordRepository;
import com.lingotower.dto.translation.TranslationResponseDTO;
import com.lingotower.dto.user.UserUpdateDTO;
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
	public UserService(UserRepository userRepository, WordRepository wordRepository, WordService wordService,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.wordRepository = wordRepository;
		this.wordService = wordService;
		this.passwordEncoder = passwordEncoder;
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
		// כאן להוסיף בדיקה אם המשתמש קיים לפני המחיקה
		userRepository.deleteById(id);
	}

	public double getLearningProgress(String username) {
		List<Word> learnedWords = userRepository.findLearnedWordsByUsername(username);
		long totalWords = wordRepository.count();
		return totalWords == 0 ? 0 : (learnedWords.size() * 100.0) / totalWords;
	}

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
	 @Transactional
	    public void updateUser(String username, UserUpdateDTO userUpdateDTO) {
	        User user = userRepository.findByUsername(username)
	                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

	        // טיפול בעדכון סיסמה אם סופקה סיסמה חדשה וישנה
	        if (userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().isEmpty() &&
	            userUpdateDTO.getOldPassword() != null && !userUpdateDTO.getOldPassword().isEmpty()) {
	            if (!passwordEncoder.matches(userUpdateDTO.getOldPassword(), user.getPassword())) {
	                throw new IllegalArgumentException("Invalid old password");
	            }
	            user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
	        } else if (userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().isEmpty()) {
	            // אפשרות לעדכון סיסמה ללא סיסמה ישנה - שקול אם לאפשר זאת
	            user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
	        }

	        // עדכון שאר הפרטים
	        user.setUsername(userUpdateDTO.getUsername());
	        user.setEmail(userUpdateDTO.getEmail());
	        user.setSourceLanguage(userUpdateDTO.getSourceLanguage());
	        user.setTargetLanguage(userUpdateDTO.getTargetLanguage());

	        userRepository.save(user);
	    }
	@Transactional
	public List<WordByCategory> getLearnedWordsForUser(String username) {
		User user = getUserByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found");
		}

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

//	public void updatePassword(String username, String newPassword) {
//		User user = userRepository.findByUsername(username)
//				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
//		user.setPassword(passwordEncoder.encode(newPassword));
//		userRepository.save(user);
//	}
	public void updatePassword(String username, String newPassword) {
	    User user = userRepository.findByUsername(username)
	            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

	    user.setPassword(passwordEncoder.encode(newPassword));
	    userRepository.save(user);
	}

}