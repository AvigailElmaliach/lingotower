package com.lingotower.service;

import com.lingotower.model.Admin;
import com.lingotower.data.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    // קונסטרקטור
    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    // פעולה לקבלת כל המנהלים
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    // פעולה לקבלת מנהל לפי מזהה
    public Optional<Admin> getAdminById(Long id) {
        return adminRepository.findById(id);
    }

    // פעולה לשמירת מנהל
    public Admin saveAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    // פעולה למחיקת מנהל
    public void deleteAdmin(Long id) {
        adminRepository.deleteById(id);
    }
}
