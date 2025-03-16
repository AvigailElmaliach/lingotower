package com.lingotower.service;

import com.lingotower.model.Admin;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class AdminService {

    private static final String BASE_URL = "http://localhost:8080/admins";
    private RestTemplate restTemplate;

    public AdminService() {
        this.restTemplate = new RestTemplate();
    }

    public List<Admin> getAllAdmins() {
        ResponseEntity<List> response = restTemplate.exchange(BASE_URL, HttpMethod.GET, null, List.class);
        return response.getBody();
    }

    public Admin getAdminById(Long id) {
        String url = BASE_URL + "/" + id;
        ResponseEntity<Admin> response = restTemplate.exchange(url, HttpMethod.GET, null, Admin.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        return null;
    }

    public Admin createAdmin(Admin admin) {
        ResponseEntity<Admin> response = restTemplate.exchange(
                BASE_URL, HttpMethod.POST, new HttpEntity<>(admin), Admin.class);
        return response.getBody();
    }

    public boolean updateAdmin(Long id, Admin admin) {
        String url = BASE_URL + "/" + id;
        HttpEntity<Admin> entity = new HttpEntity<>(admin);
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
        return response.getStatusCode() == HttpStatus.OK;
    }

    public boolean deleteAdmin(Long id) {
        String url = BASE_URL + "/" + id;
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
        return response.getStatusCode() == HttpStatus.NO_CONTENT;
    }
}
