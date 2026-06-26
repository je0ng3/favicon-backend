package com.capstone.favicon.config.exception;

/**
 * 요청한 리소스(엔티티)를 찾을 수 없을 때 던진다. GlobalExceptionHandler 에서 404 로 매핑된다.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
