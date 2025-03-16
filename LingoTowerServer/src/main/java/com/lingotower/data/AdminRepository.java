package com.lingotower.data;

import com.lingotower.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    // כל הפעולות הבסיסיות כמו save, findById, findAll, deleteById זמינות כאן
}
