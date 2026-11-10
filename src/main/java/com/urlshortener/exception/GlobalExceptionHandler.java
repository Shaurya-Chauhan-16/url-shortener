package com.urlshortener.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles invalid request arguments.
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleBadRequest(IllegalArgumentException ex) {
        return Map.of("status", HttpStatus.BAD_REQUEST.value(), "error", ex.getMessage());
    }

    // Handles requests for missing resources.
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(NoSuchElementException ex) {
        return Map.of("status", HttpStatus.NOT_FOUND.value(), "error", ex.getMessage());
    }

    // Handles malformed request bodies.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Malformed request body. Send JSON like {\"originalUrl\": \"https://example.com\"}"
        );
    }

    // Handles unexpected service state errors.
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleServerError(IllegalStateException ex) {
        return Map.of(
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "error", "Something went wrong. Please try again."
        );
    }
}
