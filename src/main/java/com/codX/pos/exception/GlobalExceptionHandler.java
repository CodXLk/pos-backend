package com.codX.pos.exception;

import com.codX.pos.util.StandardResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNameAlreadyExistException.class)
    public ResponseEntity<?> handleUserNameAlreadyExistException(UserNameAlreadyExistException ex) {
        return new ResponseEntity<>(
                new StandardResponse(409, null, ex.getMessage()),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(UserNameOrPasswordIncorrectException.class)
    public ResponseEntity<?> handleUserNameOrPasswordIncorrectException(UserNameOrPasswordIncorrectException ex) {
        return new ResponseEntity<>(
                new StandardResponse(401, null, ex.getMessage()),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorizedException(UnauthorizedException ex) {
        return new ResponseEntity<>(
                new StandardResponse(403, null, ex.getMessage()),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
        return new ResponseEntity<>(
                new StandardResponse(403, null, "Access denied"),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        return new ResponseEntity<>(
                new StandardResponse(400, null, "Validation failed"),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(
                new StandardResponse(500, null, ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
