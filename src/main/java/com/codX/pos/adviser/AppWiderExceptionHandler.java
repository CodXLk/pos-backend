package com.codX.pos.adviser;

import com.codX.pos.exception.UserNameAlreadyExistException;
import com.codX.pos.exception.UserNameOrPasswordIncorrectException;
import com.codX.pos.exception.UsernameNotFoundException;
import com.codX.pos.util.StandardResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppWiderExceptionHandler {
    @ExceptionHandler(UserNameAlreadyExistException.class)
    public ResponseEntity<StandardResponse> handleEmailAlreadyExistException(UserNameAlreadyExistException emailAlreadyExistException){
        return new ResponseEntity<>(new StandardResponse(409, emailAlreadyExistException, emailAlreadyExistException.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNameOrPasswordIncorrectException.class)
    public ResponseEntity<StandardResponse> handleEmailOrPasswordIncorrectException(UserNameOrPasswordIncorrectException emailOrPasswordIncorrectException){
        return new ResponseEntity<>(new StandardResponse(401, emailOrPasswordIncorrectException, emailOrPasswordIncorrectException.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<StandardResponse> handleEmailOrPasswordIncorrectException(UsernameNotFoundException usernameNotFoundException){
        return new ResponseEntity<>(new StandardResponse(404, usernameNotFoundException, usernameNotFoundException.getMessage()), HttpStatus.NOT_FOUND);
    }
}
