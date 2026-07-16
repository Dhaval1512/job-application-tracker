package com.dhaval.jobtracker.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("email already registered: " + email);
    }
}