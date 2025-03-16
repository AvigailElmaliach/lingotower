package com.lingotower.service;
import com.lingotower.data.UserRepository;
import com.lingotower.data.WordRepository;
import com.lingotower.model.User;
import com.lingotower.model.Word;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
@Service
public class UserService {
	private final UserRepository userRepository;
	private final WordRepository wordRepository;

	public UserService(UserRepository userRepository, WordRepository wordRepository) {
		this.userRepository = userRepository;
		this.wordRepository = wordRepository;
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public Optional<User> getUserById(Long id) {
		return userRepository.findById(id);
	}

	public User saveUser(User user) {
		return userRepository.save(user);
	}

	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}
	public double getLearningProgress(Long userId) {
        List<Word> learnedWords = userRepository.findLearnedWordsByUserId(userId);
        long totalWords = wordRepository.count(); // כל המילים במערכת
        return totalWords == 0 ? 0 : (learnedWords.size() * 100.0) / totalWords;
    }
	 public void addLearnedWord(Long userId, Long wordId) {
	        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
	        Word word = wordRepository.findById(wordId).orElseThrow(() -> new RuntimeException("Word not found"));

	        if (!user.getLearnedWords().contains(word)) {
	            user.getLearnedWords().add(word);
	            userRepository.save(user);
	        }
	    }

}











