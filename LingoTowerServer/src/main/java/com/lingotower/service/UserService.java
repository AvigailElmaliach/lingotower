package com.lingotower.service;

import com.lingotower.data.UserRepository;
import com.lingotower.data.WordRepository;
import com.lingotower.model.User;
import com.lingotower.model.Word;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import com.lingotower.exception.UserNotFoundException;
import com.lingotower.exception.WordNotFoundException;
//import com.lingotower.exception;
@Service
public class UserService {
    private final UserRepository userRepository;
    private final WordRepository wordRepository;
    
    @Autowired
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
        // כאן אפשר להוסיף אימות אם המשתמש כבר קיים
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        // כאן אפשר להוסיף בדיקה אם המשתמש קיים לפני המחיקה
        userRepository.deleteById(id);
    }

    public double getLearningProgress(Long userId) {
        List<Word> learnedWords = userRepository.findLearnedWordsByUserId(userId);
        long totalWords = wordRepository.count();
        return totalWords == 0 ? 0 : (learnedWords.size() * 100.0) / totalWords;
    }
    
    public void addLearnedWord(Long userId, Long wordId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Word word = wordRepository.findById(wordId).orElseThrow(() -> new WordNotFoundException("Word not found"));

        if (!user.getLearnedWords().contains(word)) {
            user.getLearnedWords().add(word);
            userRepository.save(user);
        }
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
	
}
