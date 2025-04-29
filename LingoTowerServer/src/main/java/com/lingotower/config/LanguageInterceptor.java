package com.lingotower.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.lingotower.constants.LanguageConstants;
import com.lingotower.model.User;
import com.lingotower.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LanguageInterceptor implements HandlerInterceptor {

	@Autowired
	private UserService userService;

	/**
	 * Intercepts incoming HTTP requests before they are handled by the controller.
	 * It retrieves the username of the currently authenticated user (if any) and
	 * sets the "sourceLanguage" and "targetLanguage" attributes in the request
	 * based on the user's preferences. If the user is not authenticated or not
	 * found, it defaults to "en" as the source language and "he" as the target
	 * language.
	 * 
	 * @param request  The incoming HttpServletRequest.
	 * @param response The outgoing HttpServletResponse.
	 * @param handler  The handler object being executed (usually a controller
	 *                 method).
	 * @return true if the request should be processed further, false otherwise.
	 * @throws Exception If an error occurs during the interception.
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
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
				request.setAttribute("sourceLanguage", LanguageConstants.ENGLISH);
				request.setAttribute("targetLanguage", LanguageConstants.HEBREW);
			}
		} else {
			request.setAttribute("sourceLanguage", LanguageConstants.ENGLISH);
			request.setAttribute("targetLanguage", LanguageConstants.HEBREW);
		}
		return true;
	}
}