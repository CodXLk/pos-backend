package com.codX.pos.exception;

public class UserNameOrPasswordIncorrectException extends RuntimeException {
    public UserNameOrPasswordIncorrectException(String message) {
        super(message);
    }
}
