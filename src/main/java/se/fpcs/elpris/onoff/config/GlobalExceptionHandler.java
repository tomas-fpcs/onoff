package se.fpcs.elpris.onoff.config;


import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import se.fpcs.elpris.onoff.db.DatabaseOperationException;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(
      ConstraintViolationException ex) {

    final String errors = ex.getConstraintViolations().stream()
        .map(violation -> violation.getPropertyPath().toString() + ": " + violation.getMessage())
        .collect(Collectors.joining(", "));

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.builder()
            .error("Validation error(s)")
            .message(errors)
            .status(HttpStatus.BAD_REQUEST.value())
            .build());

  }

  @ExceptionHandler(DatabaseOperationException.class)
  public ResponseEntity<ErrorResponse> handleDatabaseOperationException(
      DatabaseOperationException ex) {

    log.error("Database error: {}", ex.getMessage());

    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(ErrorResponse.builder()
            .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .build());

  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {

    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(ErrorResponse.builder()
            .error(HttpStatus.FORBIDDEN.getReasonPhrase())
            .message(e.getMessage())
            .status(HttpStatus.FORBIDDEN.value())
            .build());

  }

  // Catch-all exception handler
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAllOtherExceptions(Exception ex, WebRequest request) {

    log.error("{}: {}", ex.getClass().getName(), ex.getMessage(), ex);

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponse.builder() // do not leak internal state by returning error message
            .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .build());
  }

}
