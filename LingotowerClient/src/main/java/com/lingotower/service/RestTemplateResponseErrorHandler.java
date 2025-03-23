package com.lingotower.service;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import com.lingotower.security.TokenStorage;

/**
 * מטפל בשגיאות מהשרת, במיוחד בשגיאות אימות
 */
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
    
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || 
               response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            // הטוקן לא תקף או פג תוקף
            TokenStorage.clearToken();
            System.out.println("טוקן JWT לא תקף או פג תוקף. יש להתחבר מחדש.");
            // כאן אפשר להציג חלון התחברות מחדש או להעביר למסך התחברות
        }
        // במקרה אמיתי, כאן יהיה טיפול בשגיאות אחרות
        // לדוגמה: לוג של השגיאה, אירוע מערכת, הצגת הודעת שגיאה למשתמש וכו'
    }
}