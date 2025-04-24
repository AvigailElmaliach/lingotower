package com.lingotower.data;

import com.lingotower.model.BaseUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface BaseUserRepository<T extends BaseUser> extends JpaRepository<T, Long> {
	Optional<T> findByUsername(String username);

	Optional<T> findByEmail(String email);

	boolean existsByEmail(String email);
}