package com.cap.admin_service.exception;

public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }
}
