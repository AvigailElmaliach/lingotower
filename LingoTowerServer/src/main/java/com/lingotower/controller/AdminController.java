package com.lingotower.controller;

import com.lingotower.dto.admin.AdminCreateDTO;
import com.lingotower.dto.admin.AdminUpdateDTO;
import com.lingotower.dto.admin.AdminResponseDTO;
import com.lingotower.model.Admin;
import com.lingotower.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.lingotower.model.Role;
import com.lingotower.security.JwtTokenProvider;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admins")
public class AdminController {

    private final AdminService adminService;

   
    private final JwtTokenProvider jwtTokenProvider;  // הוספת תלות ב־JwtTokenProvider

    public AdminController(AdminService adminService, JwtTokenProvider jwtTokenProvider) {
        this.adminService = adminService;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    @GetMapping
    public List<AdminResponseDTO> getAllAdmins() {
        List<Admin> admins = adminService.getAllAdmins();
        return admins.stream()
                .map(admin -> new AdminResponseDTO(admin.getUsername(), admin.getRole()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminResponseDTO> getAdminById(@PathVariable Long id) {
        Optional<Admin> admin = adminService.getAdminById(id);
        return admin.map(a -> ResponseEntity.ok(new AdminResponseDTO(a.getUsername(), a.getRole())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<AdminResponseDTO> createAdmin(@RequestBody AdminCreateDTO adminCreateDTO, 
                                                        @RequestHeader("Authorization") String token) {
        // שליפת תפקיד המשתמש מהטוקן
        String role = jwtTokenProvider.extractRole(token.replace("Bearer ", ""));
        
        // אם המשתמש לא מנהל, לא נאפשר לו להוסיף מנהל חדש
        if (!role.equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // אם המשתמש הוא מנהל, ניצור את המנהל החדש
        Admin admin = new Admin();
        admin.setUsername(adminCreateDTO.getUsername());
        admin.setPassword(adminCreateDTO.getPassword());
        admin.setRole(adminCreateDTO.getRole());

        Admin savedAdmin = adminService.saveAdmin(admin);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AdminResponseDTO(savedAdmin.getUsername(), savedAdmin.getRole()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminResponseDTO> updateAdmin(@PathVariable Long id, @RequestBody AdminUpdateDTO adminUpdateDTO) {
        Optional<Admin> existingAdmin = adminService.getAdminById(id);
        if (existingAdmin.isPresent()) {
            Admin admin = existingAdmin.get();
            admin.setUsername(adminUpdateDTO.getUsername());
            admin.setRole(adminUpdateDTO.getRole());
            
            if (adminUpdateDTO.getPassword() != null && !adminUpdateDTO.getPassword().isEmpty()) {
                admin.setPassword(adminUpdateDTO.getPassword());
            }

            Admin updatedAdmin = adminService.saveAdmin(admin);
            return ResponseEntity.ok(new AdminResponseDTO(updatedAdmin.getUsername(), updatedAdmin.getRole()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        Optional<Admin> admin = adminService.getAdminById(id);
        if (admin.isPresent()) {
            adminService.deleteAdmin(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
