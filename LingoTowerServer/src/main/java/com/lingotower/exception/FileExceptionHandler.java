package com.lingotower.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.databind.DatabindException;

@ControllerAdvice
	public class FileExceptionHandler {

	    @ExceptionHandler(IOException.class)
	    public ResponseEntity<String> handleIOException(IOException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body("❌ שגיאה בניהול הקבצים: " + e.getMessage());
	    }
	 // טיפול בשגיאות אחרות, כגון DatabindException, StreamWriteException, וכו'.
	    @ExceptionHandler(DatabindException.class)
	    public ResponseEntity<String> handleDatabindException(DatabindException e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body("שגיאה בקליטת נתונים: " + e.getMessage());
	    }
	}

