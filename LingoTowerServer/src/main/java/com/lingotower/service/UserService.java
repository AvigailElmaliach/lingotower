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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	/**
	 * Retrieves all users from the database.
	 * 
	 * @return A list of all User objects.
	 */
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	/**
	 * Retrieves a user by their ID.
	 * 
	 * @param id The ID of the user to retrieve.
	 * @return An Optional containing the User object if found, otherwise an empty
	 *         Optional.
	 */
	public Optional<User> getUserById(Long id) {
		return userRepository.findById(id);
	}

	/**
	 * Saves a new user to the database.
	 * 
	 * @param user The User object to save.
	 * @return The saved User object.
	 */
	public User saveUser(User user) {
		return userRepository.save(user);
	}

	/**
	 * Deletes a user from the database by their ID.
	 * 
	 * @param id The ID of the user to delete.
	 */
	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}

	/**
	 * Calculates the learning progress of a user based on the number of learned
	 * words.
	 * 
	 * @param username The username of the user.
	 * @return A double representing the learning progress percentage (0-100).
	 */
	public double getLearningProgress(String username) {
		List<Word> learnedWords = userRepository.findLearnedWordsByUsername(username);
		long totalWords = wordRepository.count();
		return totalWords == 0 ? 0 : (learnedWords.size() * 100.0) / totalWords;
	}

	/**
	 * Retrieves a user by their username.
	 * 
	 * @param username The username of the user to retrieve.
	 * @return The User object if found, otherwise null.
	 */
	public User getUserByUsername(String username) {
		try {
			return userRepository.findByUsername(username).orElse(null);
		} catch (Exception e) {
			throw new RuntimeException("Error retrieving user by username: " + username, e);
		}
	}

	/**
	 * Updates the source and target languages of a user.
	 * 
	 * @param username   The username of the user to update.
	 * @param sourceLang The new source language code.
	 * @param targetLang The new target language code.
	 * @return True if the update was successful, false otherwise.
	 */
	public boolean updateUserLanguages(String username, String sourceLang, String targetLang) {
		Optional<User> userOptional = userRepository.findByUsername(username);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			user.setSourceLanguage(sourceLang);
			user.setTargetLanguage(targetLang);
			try {
				userRepository.save(user);
				return true;
			} catch (Exception e) {
				throw new RuntimeException("Error updating languages for user: " + username, e);
			}
		}
		return false;
	}

	/**
	 * Updates user information, including password if provided and valid.
	 * 
	 * @param username      The username of the user to update.
	 * @param userUpdateDTO A DTO containing the updated user information.
	 */
	@Transactional
	public void updateUser(String username, UserUpdateDTO userUpdateDTO) {
		try {
			User user = userRepository.findByUsername(username)
					.orElseThrow(() -> new UsernameNotFoundException("User not found"));

			if (userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().isEmpty()
					&& userUpdateDTO.getOldPassword() != null && !userUpdateDTO.getOldPassword().isEmpty()) {
				if (!passwordEncoder.matches(userUpdateDTO.getOldPassword(), user.getPassword())) {
					throw new IllegalArgumentException("Invalid old password");
				}
				user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
			} else if (userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().isEmpty()) {
				user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
			}

			user.setUsername(userUpdateDTO.getUsername());
			user.setEmail(userUpdateDTO.getEmail());
			user.setSourceLanguage(userUpdateDTO.getSourceLanguage());
			user.setTargetLanguage(userUpdateDTO.getTargetLanguage());

			userRepository.save(user);
		} catch (UsernameNotFoundException e) {
			throw e;
		} catch (IllegalArgumentException e) {
			throw e; 
		} catch (Exception e) {
			throw new RuntimeException("Error updating user: " + username, e);
		}
	}

	/**
	 * Retrieves the learned words for a specific user.
	 * 
	 * @param username The username of the user.
	 * @return A list of WordByCategory objects representing the learned words.
	 */
	@Transactional
	public List<WordByCategory> getLearnedWordsForUser(String username) {
		try {
			User user = getUserByUsername(username);
			if (user == null) {
				throw new UsernameNotFoundException("User not found");
			}

			List<Word> learnedWords = user.getLearnedWords().stream().toList();

			return learnedWords.stream().map(word -> new WordByCategory(word.getId(), word.getWord(),
					word.getTranslation(), word.getCategory(), word.getDifficulty())).collect(Collectors.toList());
		} catch (UsernameNotFoundException e) {
			throw e; // Re-throw the specific exception
		} catch (Exception e) {
			throw new RuntimeException("Error retrieving learned words for user: " + username, e);
		}
	}

	/**
	 * Adds a word to the learned words of a user.
	 * 
	 * @param username The username of the user.
	 * @param wordId   The ID of the word to add.
	 */
	@Transactional
	public void addLearnedWord(String username, Long wordId) {
		try {
			User user = userRepository.findByUsername(username)
					.orElseThrow(() -> new UsernameNotFoundException("User not found"));

			Word word = wordRepository.findById(wordId)
					.orElseThrow(() -> new EntityNotFoundException("Word not found"));
			if (!user.getLearnedWords().contains(word)) {
				user.getLearnedWords().add(word);
				userRepository.save(user);
			}
		} catch (UsernameNotFoundException e) {
			throw e; 
		} catch (EntityNotFoundException e) {
			throw e; 
		} catch (Exception e) {
			throw new RuntimeException("Error adding learned word for user: " + username + ", word ID: " + wordId, e);
		}
	}

	/**
	 * Updates the password of a user.
	 * 
	 * @param username    The username of the user to update.
	 * @param newPassword The new password.
	 */
	public void updatePassword(String username, String newPassword) {
		try {
			User user = userRepository.findByUsername(username)
					.orElseThrow(() -> new UsernameNotFoundException("User not found"));

			user.setPassword(passwordEncoder.encode(newPassword));
			userRepository.save(user);
		} catch (UsernameNotFoundException e) {
			throw e; 
		} catch (Exception e) {
			throw new RuntimeException("Error updating password for user: " + username, e);
		}
	}

	/**
	 * Updates user information by ID.
	 * 
	 * @param id            The ID of the user to update.
	 * @param userUpdateDTO A DTO containing the updated user information.
	 */
	@Transactional
	public void updateUserById(Long id, UserUpdateDTO userUpdateDTO) {
		try {
			User user = userRepository.findById(id)
					.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

			if (userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().isEmpty()) {
				user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
			}
			user.setUsername(userUpdateDTO.getUsername());
			user.setEmail(userUpdateDTO.getEmail());
			user.setSourceLanguage(userUpdateDTO.getSourceLanguage());
			user.setTargetLanguage(userUpdateDTO.getTargetLanguage());

			userRepository.save(user);
		} catch (UserNotFoundException e) {
			throw e; 
		} catch (Exception e) {
			throw new RuntimeException("Error updating user with ID: " + id, e);
		}
	}
}