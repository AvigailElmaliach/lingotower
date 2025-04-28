package com.lingotower.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.lingotower.model.User;
import com.lingotower.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LanguageInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String username = null;
        if (request.getUserPrincipal() != null) {
            username = request.getUserPrincipal().getName();
        }

        if (username != null) {
            User user = userService.getUserByUsername(username);
            if (user != null) {
                request.setAttribute("sourceLanguage", user.getSourceLanguage());
                request.setAttribute("targetLanguage", user.getTargetLanguage());
            } else {
                request.setAttribute("sourceLanguage", "en");  
                request.setAttribute("targetLanguage", "he");  
            }
        } else {
            request.setAttribute("sourceLanguage", "en");
            request.setAttribute("targetLanguage", "he");
        }
        return true;
    }

}

