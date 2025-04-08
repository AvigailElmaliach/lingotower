//package com.lingotower.service;
//
//import java.security.Principal;
//
//import org.springframework.stereotype.Service;
//
//@Service
//public class UserRoleService {
//
//    public boolean isAdmin(Principal principal) {
//        return principal.getAuthorities().stream()
//                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
//    }
//
//    public boolean isUser(Principal principal) {
//        return principal.getAuthorities().stream()
//                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER"));
//    }
//}
