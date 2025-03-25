package com.lingotower.exception;

public class UserNotFoundException extends RuntimeException {
   
	private static final long serialVersionUID = 1L;// אני לא צריכה להתשמש בזה אבל רציתי לבטל את האזהרה

	public UserNotFoundException(String message) {
        super(message);
    }
}

