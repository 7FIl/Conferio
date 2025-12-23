package com.conference.management_system.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom runtime exception that carries an HTTP status for consistent API error responses.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    private ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public static ApiException badRequest(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, message);
    }

    public static ApiException unauthorized(String message) {
        return new ApiException(HttpStatus.UNAUTHORIZED, message);
    }

    public static ApiException forbidden(String message) {
        return new ApiException(HttpStatus.FORBIDDEN, message);
    }

    public static ApiException notFound(String message) {
        return new ApiException(HttpStatus.NOT_FOUND, message);
    }

    public static ApiException conflict(String message) {
        return new ApiException(HttpStatus.CONFLICT, message);
    }

    public static ApiException internalServerError(String message) {
        return new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
