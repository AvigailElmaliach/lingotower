package com.lingotower.data;
import com.lingotower.model.User;
import com.lingotower.model.Word;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    
    @Query("SELECT u.learnedWords FROM User u WHERE u.id = :userId")
    List<Word> findLearnedWordsByUserId(@Param("userId") Long userId);

    Optional<User> findByUsername(String username); // עדכון ההחזרה
}

