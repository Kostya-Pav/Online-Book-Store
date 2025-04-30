package com.onlinebookstore.exeption;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(e -> {
                    if (e instanceof FieldError fieldError) {
                        return fieldError.getField() + " " + fieldError.getDefaultMessage();
                    }
                    return e.getDefaultMessage();
                })
                .toList();
        Message message = new Message(now(), BAD_REQUEST, errors);
        return new ResponseEntity<>(message, headers, status);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(
            EntityNotFoundException ex,
            WebRequest request) {
        Message message = getMessage(ex, NOT_FOUND);
        return new ResponseEntity<>(message, NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {
        Message message = getMessage(ex, BAD_REQUEST);
        return new ResponseEntity<>(message, BAD_REQUEST);
    }

    @ExceptionHandler(UsernameConflictException.class)
    public ResponseEntity<Object> handleUsernameConflictException(
            UsernameConflictException ex,
            WebRequest request) {
        Message message = getMessage(ex, CONFLICT);
        return new ResponseEntity<>(message, CONFLICT);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(
            AccessDeniedException ex,
            WebRequest request) {
        Message message = getMessage(ex, BAD_REQUEST);
        return new ResponseEntity<>(message, BAD_REQUEST);
    }

    private static Message getMessage(Exception ex, HttpStatus status) {
        return new Message(now(), status, ex.getMessage());
    }
}
