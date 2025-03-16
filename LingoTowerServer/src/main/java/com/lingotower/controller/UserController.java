package com.lingotower.controller;
import com.lingotower.model.User;
import com.lingotower.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")  
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        User savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @GetMapping("/{userId}/progress")
    public ResponseEntity<Double> getLearningProgress(@PathVariable Long userId) {
        double progress = userService.getLearningProgress(userId);
        return ResponseEntity.ok(progress);
    }

    @PostMapping("/{userId}/learn-word/{wordId}")
    public ResponseEntity<Void> addLearnedWord(@PathVariable Long userId, @PathVariable Long wordId) {
        userService.addLearnedWord(userId, wordId);
        return ResponseEntity.ok().build();
    }
}
