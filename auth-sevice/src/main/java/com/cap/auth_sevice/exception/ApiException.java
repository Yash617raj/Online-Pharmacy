package com.cap.auth_sevice.exception;

public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }
}