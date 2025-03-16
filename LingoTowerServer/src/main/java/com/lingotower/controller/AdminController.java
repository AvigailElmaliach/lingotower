package com.lingotower.controller;


import com.lingotower.model.Admin;
import com.lingotower.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admins")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public List<Admin> getAllAdmins() {
        return adminService.getAllAdmins();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Admin> getAdminById(@PathVariable Long id) {
        Optional<Admin> admin = adminService.getAdminById(id);
        return admin.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Admin> saveAdmin(@RequestBody Admin admin) {
        Admin savedAdmin = adminService.saveAdmin(admin);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAdmin);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Admin> updateAdmin(@PathVariable Long id, @RequestBody Admin adminDetails) {
        Optional<Admin> existingAdmin = adminService.getAdminById(id);
        if (existingAdmin.isPresent()) {
            Admin admin = existingAdmin.get();
            admin.setUsername(adminDetails.getUsername());
            admin.setPassword(adminDetails.getPassword());
            admin.setRole(adminDetails.getRole());
            Admin updatedAdmin = adminService.saveAdmin(admin);
            return ResponseEntity.ok(updatedAdmin);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // פעולה למחיקת מנהל לפי מזהה
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
