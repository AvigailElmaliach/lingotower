package com.lingotower.controller;

import com.lingotower.dto.language.LanguageUpdateRequest;
import com.lingotower.dto.user.UserCreateDTO;
import com.lingotower.dto.user.UserDTO;
import com.lingotower.dto.user.UserProgressDTO;
import com.lingotower.dto.user.UserUpdateDTO;
import com.lingotower.data.UserRepository;
import com.lingotower.model.User;
import com.lingotower.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
	 @Autowired
	    private UserRepository userRepository;

	    @Autowired
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getSourceLanguage()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(u -> ResponseEntity.ok(new UserDTO(u.getId(), u.getUsername(), u.getEmail(), u.getSourceLanguage())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<UserDTO> saveUser(@RequestBody UserCreateDTO userCreateDTO) {
        User user = new User();
        user.setUsername(userCreateDTO.getUsername());
        user.setPassword(userCreateDTO.getPassword());
        user.setEmail(userCreateDTO.getEmail());
        user.setSourceLanguage(userCreateDTO.getSourceLanguage());
        user.setTargetLanguage(userCreateDTO.getTargetLanguage());
        User savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserDTO(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getSourceLanguage()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO) {
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setUsername(userUpdateDTO.getUsername());
            user.setEmail(userUpdateDTO.getEmail());
            user.setSourceLanguage(userUpdateDTO.getLanguage());
            User updatedUser = userService.saveUser(user);
            return ResponseEntity.ok(new UserDTO(updatedUser.getId(), updatedUser.getUsername(), updatedUser.getEmail(), updatedUser.getSourceLanguage()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{userId}/progress")
    public ResponseEntity<UserProgressDTO> getLearningProgress(@PathVariable Long userId) {
        double progress = userService.getLearningProgress(userId);
        return ResponseEntity.ok(new UserProgressDTO(userId, progress));
    }

    @PostMapping("/{userId}/learn-word/{wordId}")
    public ResponseEntity<Void> addLearnedWord(@PathVariable Long userId, @PathVariable Long wordId) {
        userService.addLearnedWord(userId, wordId);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/update-languages")
    public ResponseEntity<?> updateLanguages(@RequestBody LanguageUpdateRequest request, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        user.setSourceLanguage(request.getSourceLanguage());
        user.setTargetLanguage(request.getTargetLanguage());
        userRepository.save(user);

        return ResponseEntity.ok("Languages updated successfully");
    }
}

