package com.ssafy.forest.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiExceptionAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidError(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors()
            .forEach(e -> errors.put(((FieldError) e).getField(), e.getDefaultMessage()));
        log.warn("MethodArgumentNotValidException occurs");
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationError(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String path = violation.getPropertyPath().toString();
            errors.put(path, violation.getMessage());
        }
        log.warn("ConstraintViolationException occurs");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentError(IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        log.warn("IllegalArgumentException occurs");
        return ResponseEntity.badRequest().body(error);
    }

}