package com.codX.pos.adviser;

import com.codX.pos.exception.EmailAlreadyExistException;
import com.codX.pos.exception.EmailOrPasswordIncorrectException;
import com.codX.pos.exception.UsernameNotFoundException;
import com.codX.pos.util.StandardResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppWiderExceptionHandler {
    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<StandardResponse> handleEmailAlreadyExistException(EmailAlreadyExistException emailAlreadyExistException){
        return new ResponseEntity<>(new StandardResponse(409, emailAlreadyExistException, emailAlreadyExistException.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmailOrPasswordIncorrectException.class)
    public ResponseEntity<StandardResponse> handleEmailOrPasswordIncorrectException(EmailOrPasswordIncorrectException emailOrPasswordIncorrectException){
        return new ResponseEntity<>(new StandardResponse(401, emailOrPasswordIncorrectException, emailOrPasswordIncorrectException.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<StandardResponse> handleEmailOrPasswordIncorrectException(UsernameNotFoundException usernameNotFoundException){
        return new ResponseEntity<>(new StandardResponse(404, usernameNotFoundException, usernameNotFoundException.getMessage()), HttpStatus.NOT_FOUND);
    }
}
