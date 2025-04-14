package com.lingotower.service;

import com.lingotower.data.AdminRepository;
import com.lingotower.dto.admin.AdminCreateDTO;
import com.lingotower.dto.admin.AdminUpdateDTO;
import com.lingotower.model.Admin;
import com.lingotower.model.Role;
import com.lingotower.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository,
                        JwtTokenProvider jwtTokenProvider,
                        PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    // קבלת כל המנהלים
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    // קבלת מנהל לפי מזהה
    public Optional<Admin> getAdminById(Long id) {
        return adminRepository.findById(id);
    }

    // שמירת מנהל
    public Admin saveAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    // מחיקת מנהל
    public void deleteAdmin(Long id) {
        adminRepository.deleteById(id);
    }

 // רישום מנהל חדש
    public void registerAdmin(AdminCreateDTO adminCreateDTO, String token) {
        String cleanToken = token.replace("Bearer ", "");
        String role = jwtTokenProvider.extractRole(cleanToken);

        if (!role.equals("ADMIN")) {
            throw new SecurityException("Only admins can register new admins.");
        }

        if (adminRepository.existsByEmail(adminCreateDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Admin newAdmin = new Admin(
                adminCreateDTO.getUsername(),
                passwordEncoder.encode(adminCreateDTO.getPassword()),
                adminCreateDTO.getEmail(),
                adminCreateDTO.getSourceLanguage(),
                adminCreateDTO.getTargetLanguage(),
                Role.ADMIN
        );

        adminRepository.save(newAdmin);
    }
//    public Optional<Admin> updateAdmin(Long id, AdminUpdateDTO adminUpdateDTO) {
//        Optional<Admin> existingAdmin = adminRepository.findById(id);
//        if (existingAdmin.isPresent()) {
//            Admin admin = existingAdmin.get();
//            admin.setUsername(adminUpdateDTO.getUsername());
//            admin.setRole(adminUpdateDTO.getRole());
//            
//            if (adminUpdateDTO.getPassword() != null && !adminUpdateDTO.getPassword().isEmpty()) {
//                String encodedPassword = passwordEncoder.encode(adminUpdateDTO.getPassword());
//                admin.setPassword(encodedPassword);
//            }
//
//
//            return Optional.of(adminRepository.save(admin));
//        } else {
//            return Optional.empty();
//        }
//    }
//
    public Optional<Admin> updateAdmin(Long id, AdminUpdateDTO adminUpdateDTO) {
        Optional<Admin> existingAdmin = adminRepository.findById(id);
        if (existingAdmin.isPresent()) {
            Admin admin = existingAdmin.get();

            if (adminUpdateDTO.getUsername() != null) {
                admin.setUsername(adminUpdateDTO.getUsername());
            }

            if (adminUpdateDTO.getRole() != null) {
                admin.setRole(adminUpdateDTO.getRole());
            }

            if (adminUpdateDTO.getEmail() != null) {
                admin.setEmail(adminUpdateDTO.getEmail());
            }

            if (adminUpdateDTO.getPassword() != null && !adminUpdateDTO.getPassword().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(adminUpdateDTO.getPassword());
                admin.setPassword(encodedPassword);
            }

            return Optional.of(adminRepository.save(admin));
        } else {
            return Optional.empty();
        }
    }

}
