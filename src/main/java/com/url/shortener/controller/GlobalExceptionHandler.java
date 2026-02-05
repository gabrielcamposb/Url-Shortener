package com.url.shortener.controller;

import com.url.shortener.exception.BadRequestException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequest(BadRequestException e) {
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return ResponseEntity
                .badRequest()
                .body("Invalid parameter: hours must be greater than 0");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException e) {
        return ResponseEntity
                .badRequest()
                .body("Invalid parameter: hours must be greater than 0");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException e) {
        if (e.getMessage() != null && e.getMessage().toLowerCase().contains("Expired")) {
            return ResponseEntity
                    .status(HttpStatus.GONE)
                    .body(e.getMessage());
        }

        return ResponseEntity
                .internalServerError()
                .body("Internal error");
    }
}
