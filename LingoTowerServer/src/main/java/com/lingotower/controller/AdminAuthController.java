package com.lingotower.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lingotower.service.AdminAuthService;
import com.lingotower.data.AdminRepository;
import com.lingotower.model.Admin;
import com.lingotower.model.BaseUser;
import com.lingotower.dto.admin.AdminCreateDTO;
import com.lingotower.dto.admin.AdminRegisterRequest;

@RestController
@RequestMapping("/api/auth/admin")
public class AdminAuthController {
    private final AdminAuthService adminAuthService;
    private final AdminRepository adminRepository;

    public AdminAuthController(AdminAuthService adminAuthService, AdminRepository adminRepository) {
        this.adminAuthService = adminAuthService;
        this.adminRepository = adminRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<BaseUser> registerAdmin(@RequestBody AdminRegisterRequest request) {
        // מציאת האדמין שמנסה לבצע את ההרשמה
        Admin currentAdmin = adminRepository.findByEmail(request.getCurrentAdminEmail())
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        // קריאה למחלקת השירות עם הנתונים של האדמין החדש
        BaseUser newAdmin = adminAuthService.registerAdmin(request.getAdminCreateDTO(), currentAdmin);

        // מחזירים את האדמין החדש עם קוד הסטטוס 201 (נוצר בהצלחה)
        return ResponseEntity.status(HttpStatus.CREATED).body(newAdmin);
    }

}
