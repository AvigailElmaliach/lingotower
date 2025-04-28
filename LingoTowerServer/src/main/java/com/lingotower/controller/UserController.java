package com.lingotower.controller;

import com.lingotower.dto.language.LanguageUpdateRequest;
import com.lingotower.dto.translation.TranslationResponseDTO;
import com.lingotower.dto.baseUser.PasswordUpdateRequestDTO;
import com.lingotower.dto.user.UserCreateDTO;
import com.lingotower.dto.user.UserDTO;
import com.lingotower.dto.user.UserProgressDTO;
import com.lingotower.dto.user.UserUpdateDTO;
import com.lingotower.dto.word.WordByCategory;
import com.lingotower.exception.UserNotFoundException;
import com.lingotower.data.UserRepository;
import com.lingotower.model.User;
import com.lingotower.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

	/**
	 * Retrieves a user by their ID.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
		Optional<User> user = userService.getUserById(id);
		return user.map(
				u -> ResponseEntity.ok(new UserDTO(u.getId(), u.getUsername(), u.getEmail(), u.getSourceLanguage())))
				.orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	/**
	 * Creates a new user.
	 */
	@PostMapping
	public ResponseEntity<UserDTO> saveUser(@RequestBody UserCreateDTO userCreateDTO) {
		User user = new User();
		user.setUsername(userCreateDTO.getUsername());
		user.setPassword(userCreateDTO.getPassword());
		user.setEmail(userCreateDTO.getEmail());
		user.setSourceLanguage(userCreateDTO.getSourceLanguage());
		user.setTargetLanguage(userCreateDTO.getTargetLanguage());
		User savedUser = userService.saveUser(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(new UserDTO(savedUser.getId(), savedUser.getUsername(),
				savedUser.getEmail(), savedUser.getSourceLanguage()));
	}

	/**
	 * Retrieves the learning progress of the currently logged-in user.
	 */
	@GetMapping("/progress")
	public ResponseEntity<UserProgressDTO> getLearningProgress(Principal principal) {
		String username = principal.getName();
		double progress = userService.getLearningProgress(username);
		return ResponseEntity.ok(new UserProgressDTO(username, progress));
	}

	/**
	 * Updates the source and target languages of the currently logged-in user.
	 */
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

	/**
	 * Retrieves the list of words learned by the currently logged-in user.
	 */
	@GetMapping("/learned")
	public ResponseEntity<List<WordByCategory>> getLearnedWords(Principal principal) {
		List<WordByCategory> learnedDTOs = userService.getLearnedWordsForUser(principal.getName());
		return ResponseEntity.ok(learnedDTOs);
	}

	/**
	 * Adds a word to the list of learned words for the currently logged-in user.
	 */
	@PostMapping("/learned/{wordId}")
	public ResponseEntity<Void> addLearnedWord(@PathVariable Long wordId, Principal principal) {
		userService.addLearnedWord(principal.getName(), wordId);
		return ResponseEntity.ok().build();
	}

	/**
	 * Updates the password of the currently logged-in user. Requires the old
	 * password to be sent in the request body.
	 */
	@PutMapping("/password")
	public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateRequestDTO passwordUpdateRequest,
			Principal principal) {
		String username = principal.getName();
		try {
			userService.updatePassword(username, passwordUpdateRequest.getNewPassword());
			return ResponseEntity.ok("Password updated successfully");
		} catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	/**
	 * Retrieves the information of the currently logged-in user.
	 */
	@GetMapping("/me")
	public ResponseEntity<UserDTO> getLoggedInUser(Principal principal) {
		String username = principal.getName();
		User user = userService.getUserByUsername(username);
		if (user != null) {
			UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getSourceLanguage());
			return ResponseEntity.ok(userDTO);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN') or #id == principal.id")
	public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO,
			Principal principal) {
		try {
			userService.updateUserById(id, userUpdateDTO);

			Optional<User> updatedUserOptional = userService.getUserById(id);
			return updatedUserOptional
					.map(updatedUser -> ResponseEntity.ok(new UserUpdateDTO(updatedUser.getUsername(),
							updatedUser.getEmail(), updatedUser.getSourceLanguage(), null)))
					.orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

		} catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}