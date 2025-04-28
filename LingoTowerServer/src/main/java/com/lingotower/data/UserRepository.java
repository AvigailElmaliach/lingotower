package com.lingotower.data;

import com.lingotower.model.BaseUser;
import com.lingotower.model.User;

import com.lingotower.model.Word;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseUserRepository<User> {

	Optional<User> findByUsername(String username);

	boolean existsByEmail(String email);

	Optional<User> findByEmail(String currentAdminEmail);

	@Query("SELECT u.learnedWords FROM User u WHERE u.username = :username")
	List<Word> findLearnedWordsByUsername(@Param("username") String username);

	@Query("SELECT w FROM User u JOIN u.learnedWords w WHERE u.username = :username AND w.targetLanguage = :targetLanguage")
	List<Word> findLearnedWordsByUsernameAndTargetLanguage(@Param("username") String username,
			@Param("targetLanguage") String targetLanguage);

}
