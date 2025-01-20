package se.fpcs.elpris.onoff.config;


import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import se.fpcs.elpris.onoff.db.DatabaseOperationException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handle(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handle(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handle(MissingServletRequestParameterException ex) {
        String name = ex.getParameterName();
        Map<String, String> error = new HashMap<>();
        error.put(name, name + " parameter is missing");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handle(MethodArgumentTypeMismatchException ex) {
        final String requiredType = Optional.ofNullable(ex.getRequiredType())
                .map(Class::getSimpleName)
                .orElse(null);
        var value = ex.getValue();
        String message = String.format("Parameter '%s' should be of requiredType '%s' but the value '%s' is not", ex.getName(), requiredType, value);
        Map<String, String> error = new HashMap<>();
        error.put(ex.getName(), message);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<Map<String, String>> handle(ServletRequestBindingException ex) {
        log.error("ServletRequestBindingException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(Map.of(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, String>> handle(NullPointerException ex) {
        log.error("NullPointerException: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(Map.of(), HttpStatus.BAD_REQUEST);
    }

    // Catch-all exception handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex, WebRequest request) {
        log.error("{}: {}", ex.getClass().getName(), ex.getMessage(), ex);
        return new ResponseEntity<>("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DatabaseOperationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleDatabaseOperationException(DatabaseOperationException ex) {
        log.error("Failed to save entity to database: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
