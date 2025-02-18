package se.fpcs.elpris.onoff.config;


import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import se.fpcs.elpris.onoff.db.DatabaseOperationException;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

  //TODO handle org.springframework.security.authentication.LockedException (user account locked)

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handle(
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
  public ResponseEntity<ErrorResponse> handle(
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
  public ResponseEntity<ErrorResponse> handle(AccessDeniedException e) {

    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(ErrorResponse.builder()
            .error(HttpStatus.FORBIDDEN.getReasonPhrase())
            .message(e.getMessage())
            .status(HttpStatus.FORBIDDEN.value())
            .build());

  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handle(HttpRequestMethodNotSupportedException e) {

    return ResponseEntity
        .status(HttpStatus.METHOD_NOT_ALLOWED)
        .body(ErrorResponse.builder()
            .error(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase())
            .message(e.getMessage())
            .status(HttpStatus.METHOD_NOT_ALLOWED.value())
            .build());

  }

  // Catch-all exception handler
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handle(Exception ex, WebRequest request) {

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
