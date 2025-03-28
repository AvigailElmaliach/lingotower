package com.lingotower.data;

import com.lingotower.model.Admin;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
//import com.lingotower.model.BaseUser;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);
    boolean existsByEmail(String email);
//	Optional<Admin> findByEmail1(String currentAdminEmail);
}
