package com.lingotower.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lingotower.data.AdminRepository;
import com.lingotower.model.Role;
import com.lingotower.model.User;
import com.lingotower.model.Admin;
import com.lingotower.model.BaseUser;
import com.lingotower.dto.admin.AdminCreateDTO;

@Service
public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminAuthService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Admin registerAdmin(AdminCreateDTO adminCreateDTO, Admin currentAdmin) {
        // בדיקה שהמשתמש הנוכחי הוא אכן אדמין
    	if ( currentAdmin.getRole() != Role.SUPERADMIN) {
    	    throw new SecurityException("Only an admin or superadmin can register another admin!");
    	}


        // בדיקה אם הדוא"ל כבר קיים במערכת
        if (adminRepository.existsByEmail(adminCreateDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // יצירת אובייקט אדמין חדש
        Admin newAdmin = new Admin(
            adminCreateDTO.getUsername(),
            passwordEncoder.encode(adminCreateDTO.getPassword()),
            adminCreateDTO.getEmail(),
            adminCreateDTO.getLanguage(),
            Role.ADMIN
        );

        // שמירת האדמין החדש במסד הנתונים
        return adminRepository.save(newAdmin);
    }

	
}
